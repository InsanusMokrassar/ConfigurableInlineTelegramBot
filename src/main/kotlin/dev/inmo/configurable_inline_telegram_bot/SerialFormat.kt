package dev.inmo.configurable_inline_telegram_bot

import kotlinx.serialization.json.Json

val serialFormat = Json {
    ignoreUnknownKeys = true
    allowSpecialFloatingPointValues = true
    useArrayPolymorphism = true
}
