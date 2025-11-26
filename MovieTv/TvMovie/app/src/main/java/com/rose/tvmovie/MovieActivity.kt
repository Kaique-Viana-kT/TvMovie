package com.rose.tvmovie

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rose.tvmovie.model.Movie
import com.rose.tvmovie.model.MovieDetail
import com.rose.tvmovie.util.DowloadImageTask
import com.rose.tvmovie.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var txtTitle: TextView
    private lateinit var txtCast: TextView
    private lateinit var txtDesc: TextView
    private lateinit var rvSimilar: RecyclerView
    private lateinit var  progress: ProgressBar
    private lateinit var adapter: MovieAdapter

    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            window.decorView.systemUiVisibility = 0
        }

        txtTitle = findViewById(R.id.txt_title)
        txtDesc = findViewById(R.id.desc_movie)
        txtCast = findViewById(R.id.cast_movie)
        rvSimilar = findViewById(R.id.movie_rv_similar)
        progress = findViewById(R.id.progress_movie)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID não foi encontrado")

        val url = "https://atway.tiagoaguiar.co/fenix/netflixapp/movie/$id?apiKey=ce619eb9-7d79-4139-afde-166b1c3d23ee"

        MovieTask(this).execute(url)

        adapter = MovieAdapter(movies, R.layout.movie_item_similar){id ->
            val intent = Intent(this@MovieActivity, MovieActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        rvSimilar.layoutManager = GridLayoutManager(this, 3)
        rvSimilar.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        val button: Button = findViewById(R.id.button_player)

        button.setOnClickListener{
            Toast.makeText(this, "voce apertou", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

    override fun onResult(movieDetail: MovieDetail) {
        progress.visibility = View.GONE

        txtTitle.text = movieDetail.movie.title
        txtDesc.text = movieDetail.movie.desc
        txtCast.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        DowloadImageTask(object : DowloadImageTask.Callback {
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadow) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)

                val coverImg: ImageView = findViewById(R.id.movie_img)
                coverImg.setImageDrawable(layerDrawable)
            }
        }).execute(movieDetail.movie.cover_url)

    }

    override fun onFailure(message: String) {
        progress.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}