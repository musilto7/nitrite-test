package cz.musilto5.testnitrite

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {

            logMessage("Starting creating db")
            withContext(Dispatchers.IO) {
                DbHolder.initializeDB(File(filesDir, "test.db"))
            }
            logMessage("Db created")

            logMessage("Starting loading data from assets")
            val jsonObject = withContext(Dispatchers.IO) {
                AssetsDataSource(assets).loadFromAssets("articles.json")
            }
            logMessage("Assets data loaded")

            logMessage("Filling database")
            withContext(Dispatchers.IO) {
                DbHolder.fillDatabase(jsonObject!!)
            }
            logMessage("database Filled")

            logMessage("Find articles")
            val documents = withContext(Dispatchers.IO) {
                DbHolder.findArticlesById(
                    "A190228_162730_zahranicni_dtt", "A190228_171006_brno-zpravy_krut",
                    "A190226_121716_veda_pka", "A190228_135555_lyzovani_par", "A190228_181639_lyzovani_rou",
                    "A190228_174317_ekonomika_are"
                )
            }
            logMessage("Items founded")

            logMessage("Find all articles")
            val allArticles = withContext(Dispatchers.IO) {
                DbHolder.findAllArticles()
            }
            logMessage("All articles loaded ${allArticles.size} : ")
            val articleAtPosition = allArticles[50]
            print(articleAtPosition)

        }
    }

    private var lasTimeStamp = 0L
    private fun logMessage(message: String) {
        val countOfSeconds = (System.currentTimeMillis() - lasTimeStamp)
        Log.i(TAG, "$message $countOfSeconds")
        lasTimeStamp = System.currentTimeMillis()
    }

    companion object {
        const val TAG = "TESTNITRITE"
    }
}
