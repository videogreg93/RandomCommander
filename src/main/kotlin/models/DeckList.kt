package com.odencave.models

import kotlinx.serialization.Serializable

@Serializable
data class DeckList(
    val meta: Meta,
    val data: List<DeckData>
)

@Serializable
data class Meta(
    val date: String,
    val version: String,
)