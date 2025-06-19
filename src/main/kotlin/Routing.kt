package com.odencave

import com.odencave.models.Deck
import com.odencave.models.DeckData
import com.odencave.models.DeckList
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.serialization.json.Json

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

fun Application.configureRouting() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
    routing {
        get("/styles.css") {
            call.respondCss {
                html {
                    display = Display.table
                    margin = Margin(LinearDimension.auto)
                }
                body {
                    display = Display.tableCell
                    verticalAlign = VerticalAlign.middle
                }
                rule(".commander") {
                    width = LinearDimension("30%")
                }
                rule(".header") {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                }
                rule(".allCards") {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    flexWrap = FlexWrap.wrap
                }
                rule(".allCardsItem") {
                    width = LinearDimension("20%")
                }
                rule(".textAreaList") {
                    width = LinearDimension("40%")
                    height = LinearDimension("400px")
                }
            }
        }
        get("/") {
            val response = client.get("https://mtgjson.com/api/v5/DeckList.json")
            val decks: DeckList = response.body()
            val deckData: DeckData = decks.data.filter { it.type == "Commander Deck" }.random()
            val deckResponse: Deck = client.get("https://mtgjson.com/api/v5/decks/${deckData.fileName}.json").body()
            val commanders = deckResponse.data.commander
            val clipBoard = deckResponse.getDeckForClipboard()
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
                }
                body {
                    h1 {
                        +deckData.name
                    }
                    commanders.forEach {
                        a {
                            href = "https://api.scryfall.com/cards/${it.identifiers.scryfallId}?format=image"
                            img {
                                classes = setOf("commander")
                                src = "https://api.scryfall.com/cards/${it.identifiers.scryfallId}?format=image"
                            }
                        }
                    }
                    textArea {
                        classes = setOf("textAreaList")
                        +clipBoard
                    }
                    div {
                        classes = setOf("allCards")
                        deckResponse.data.mainBoard.forEach {
                            img {
                                classes = setOf("allCardsItem")
                                src = "https://api.scryfall.com/cards/${it.identifiers.scryfallId}?format=image"
                            }
                        }
                    }

                }

            }
        }
    }
}
