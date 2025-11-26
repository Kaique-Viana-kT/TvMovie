package com.rose.tvmovie.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.rose.tvmovie.model.Category
import com.rose.tvmovie.model.Movie
import com.rose.tvmovie.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    val executor = Executors.newSingleThreadExecutor()

    interface Callback{
        fun onPreExecute()
        fun onResult(movieDetail: MovieDetail)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
        callback.onPreExecute()

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

                if(statusCode == 400){
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)
                    val jsonAsString = toString(buffer)

                    val json = JSONObject(jsonAsString)
                    json.getString("message")

                }else if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream
//                val jsonAsString = stream.bufferedReader().use{ it.readText() }

                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                val movieDetail = toMovieDetail(jsonAsString)

                handler.post{
                    callback.onResult(movieDetail)
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

    private fun toMovieDetail(jsonAsString: String) : MovieDetail{
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val cover_url = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similiars = mutableListOf<Movie>()
        for (i in 0 until jsonMovies.length()){
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId, similarCoverUrl)
            similiars.add(m)
        }

        val movie = Movie(id, cover_url, title, desc, cast)

        return MovieDetail(movie, similiars)

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