package com.rose.tvmovie

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rose.tvmovie.model.Category
import com.rose.tvmovie.model.Movie
import com.rose.tvmovie.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progressBar: ProgressBar

    private lateinit var rvTendence: RecyclerView
    private lateinit var rvMovie: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private val categories = mutableListOf<Category>()
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            window.decorView.systemUiVisibility = 0
        }

        adapter = CategoryAdapter(categories) { id ->
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)

        }

        rvTendence = findViewById(R.id.rv_movie_tendence)
        rvMovie = findViewById(R.id.recyclerMovie)

        rvTendence.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvTendence.adapter = MovieAdapter(movies, R.layout.movie_item) {id ->
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)

        }

        rvMovie.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvMovie.adapter = adapter

        CategoryTask(this).execute("https://atway.tiagoaguiar.co/fenix/netflixapp/home?apiKey=ce619eb9-7d79-4139-afde-166b1c3d23ee")

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onResult(categories: List<Category>) {
        //quando o category task chamará de volta (callback)
        progressBar.visibility = View.GONE

        if(categories.isNotEmpty()){
            val tendece = categories[0]
            movies.clear()
            movies.addAll(tendece.movies)
            rvTendence.adapter?.notifyDataSetChanged()
        }

        val category = categories.subList(1, categories.size)
        this.categories.clear()
        this.categories.addAll(category)
        adapter.notifyDataSetChanged()

//        this.categories.clear()
//        this.categories.addAll(categories)
//        adapter.notifyDataSetChanged()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE
    }
}