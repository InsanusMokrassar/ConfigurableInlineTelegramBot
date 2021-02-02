package dev.inmo.configurable_inline_telegram_bot.config

import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.utils.TelegramAPIUrlsKeeper
import dev.inmo.tgbotapi.utils.telegramBotAPIDefaultUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.Serializable

@Serializable
data class BotConfig(
    val botToken: String,
    val apiUrl: String = telegramBotAPIDefaultUrl,
    val clientConfig: HttpClientConfig? = null,
    val webhookConfig: WebhookConfig? = null
) {
    fun createBot(): RequestsExecutor = telegramBot(
        botToken
    ) {
        client = HttpClient(OkHttp.create(clientConfig ?.builder ?: {}))
        telegramAPIUrlsKeeper = TelegramAPIUrlsKeeper(
            botToken,
            apiUrl
        )
    }
}
