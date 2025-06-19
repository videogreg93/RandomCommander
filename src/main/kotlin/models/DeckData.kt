package com.odencave.models

import kotlinx.serialization.Serializable

@Serializable
data class DeckData(
    val code: String,
    val fileName: String,
    val name: String,
    val releaseDate: String,
    val type: String,
)