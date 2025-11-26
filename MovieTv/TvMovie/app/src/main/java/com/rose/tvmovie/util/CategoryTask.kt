package com.rose.tvmovie.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.rose.tvmovie.model.Category
import com.rose.tvmovie.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())

    interface Callback{
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
        callback.onPreExecute()
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            var buffer: BufferedInputStream? = null

            try {
                val requestUrl = URL(url)
                urlConnection = requestUrl.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                //tudo aqui é sucesso
                stream = urlConnection.inputStream
//                val jsonAsString = stream.bufferedReader().use{ it.readText() }

                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)
                val categories = toCategorie(jsonAsString)

                handler.post{
                    callback.onResult(categories)
                }


            } catch (e: IOException) {
                val message = e.message ?: "erro desconhecido"
                Log.e("teste", message, e)
                handler.post{
                    callback.onFailure(message)
                }

            } finally{
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun toCategorie(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategoiries = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategoiries.length()) {
            val jsonCategory = jsonCategoiries.getJSONObject(i)//acessando a primeira categoria
            //a cada loop feito, pega as informações de outra categoria

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()

            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)

                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))

            }

            categories.add(Category(title, movies))
        }

        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val bAoS = ByteArrayOutputStream()
        var read: Int

        while (true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            bAoS.write(bytes, 0, read)
        }

        return String(bAoS.toByteArray())
    }

}