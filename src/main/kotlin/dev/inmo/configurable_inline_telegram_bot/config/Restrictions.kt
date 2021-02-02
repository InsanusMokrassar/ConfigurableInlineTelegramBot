package dev.inmo.configurable_inline_telegram_bot.config

import dev.inmo.tgbotapi.types.*
import dev.inmo.tgbotapi.types.InlineQueries.abstracts.InlineQuery
import kotlinx.serialization.Serializable

@Serializable
data class Restrictions(
    val allowedUsers: List<ChatIdentifier> = emptyList()
) {
    fun check(query: InlineQuery): Boolean {
        return query.from.id in allowedUsers || query.from.username ?.let { it in allowedUsers } ?: false
    }
}