package cz.musilto5.testnitrite

import android.content.res.AssetManager
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader

interface IAssetsDataSource {

    suspend fun loadFromAssets(assetsFileName: String): JSONObject?
}

class AssetsDataSource(private val assetManager: AssetManager) : IAssetsDataSource {


    override suspend fun loadFromAssets(assetsFileName: String): JSONObject? {

        var assetsReader: Reader? = null

        return try {
            assetsReader = openAssetsFile(assetsFileName)
            JSONObject(assetsReader.convertToString())
        } catch (e: Exception) {

            null
        } finally {
            assetsReader?.close()
        }
    }

    private fun Reader.convertToString(): String {
        val stringBuilder = StringBuilder()

        val bufred = BufferedReader(this)

        var line: String? = bufred.readLine()
        while (line != null) {
            stringBuilder.append(line)
            line = bufred.readLine()
        }
        return stringBuilder.toString()
    }

    private fun openAssetsFile(assetsFileName: String): Reader {
        return InputStreamReader(assetManager.open("$DEMO_DIRECTORY/$assetsFileName"))
    }

    companion object {
        const val DEMO_DIRECTORY = "demo"
    }
}