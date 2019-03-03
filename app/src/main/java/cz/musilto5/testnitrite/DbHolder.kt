package cz.musilto5.testnitrite

import org.dizitart.no2.*
import org.dizitart.no2.filters.Filters.eq
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


object DbHolder {

    private const val ARTICLE_COLLECTION = "articleCollection"

    private const val ID_ARTICLE = "idArticle"

    private lateinit var database: Nitrite

    fun initializeDB(file: File) {
        database = Nitrite.builder()
            .filePath(file)
            .compressed()
            .openOrCreate()

        var collection = database.getCollection(ARTICLE_COLLECTION)
        collection.drop()
        collection = database.getCollection(ARTICLE_COLLECTION)

        collection.createIndex(ID_ARTICLE, IndexOptions.indexOptions(IndexType.Unique))

        collection.close()
        database.commit()

    }

    fun fillDatabase(jsonObject: JSONObject) {
        val collection = database.getCollection(ARTICLE_COLLECTION)

        val articleList = ((jsonObject["result"] as JSONObject)["articleList"] as JSONObject)["articles"] as JSONArray

        for (index in 0 until articleList.length()) {
            val article = articleList.get(index) as JSONObject
            collection.remove(eq(ID_ARTICLE, article[ID_ARTICLE]))
            collection.insert(createArticleDocument(article))
        }

        collection.close()
        database.commit()
    }

    private fun createArticleDocument(jsonObject: JSONObject): Document {
        val document = Document()

        val articleId = jsonObject[ID_ARTICLE] as String
        document[articleId] = jsonObject.toString()
        document[ID_ARTICLE] = articleId

        return document
    }


    fun findArticlesById(vararg articlesId: String): List<JSONObject> {
        val collection = database.getCollection(ARTICLE_COLLECTION)

        val resultList = arrayListOf<JSONObject>()
        for (idIndex in 0 until articlesId.size) {
            val cursor = collection.find(eq(ID_ARTICLE, articlesId[idIndex]))
            for (document in cursor) {
                // process the document
                resultList.add(document.articleDocumentToJSONObject())
            }
        }
        return resultList
    }

    fun findAllArticles(): List<JSONObject> {
        val collection = database.getCollection(ARTICLE_COLLECTION)

        val cursor = collection.find(FindOptions(ID_ARTICLE, SortOrder.Descending))

        val resultList = arrayListOf<JSONObject>()
        for (document in cursor) {
            resultList.add(document.articleDocumentToJSONObject())
        }
        collection.close()

        return resultList
    }

    private fun Document.articleDocumentToJSONObject(): JSONObject {
        val articleId = this[ID_ARTICLE]
        return JSONObject(this[articleId] as String)
    }

}