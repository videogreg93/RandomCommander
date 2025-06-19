package com.odencave.models

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val meta: Meta,
    val data: Data
) {
    fun getDeckForClipboard(): String {
        val sb = StringBuilder()
        data.commander.forEach {
            sb.append(it.name + "\n")
        }
        data.mainBoard.forEach { card ->
            repeat(card.count) {
                sb.append(card.name + "\n")
            }
        }

        return sb.toString()
    }
}

@Serializable
data class Data(
    val commander: List<Card>,
    val mainBoard: List<Card>,
)

@Serializable
data class Card(
    val identifiers: Identifiers,
    val name: String,
    val count: Int,
)

@Serializable
data class Identifiers(
    val scryfallId: String
)