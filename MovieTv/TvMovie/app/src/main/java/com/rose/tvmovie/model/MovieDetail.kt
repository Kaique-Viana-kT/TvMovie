package com.rose.tvmovie.model

data class MovieDetail(
    val movie: Movie,
    val similars: List<Movie>
)
