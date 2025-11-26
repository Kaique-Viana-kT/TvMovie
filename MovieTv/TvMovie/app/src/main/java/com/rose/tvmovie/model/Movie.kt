package com.rose.tvmovie.model

import androidx.annotation.DrawableRes

data class Movie(
    val id: Int,
    val cover_url: String,
    val title: String = "",
    val desc: String = "",
    val cast: String = ""
)