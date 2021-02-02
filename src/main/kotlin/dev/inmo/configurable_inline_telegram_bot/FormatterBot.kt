package dev.inmo.configurable_inline_telegram_bot

import dev.inmo.tgbotapi.bot.exceptions.RequestException
import dev.inmo.tgbotapi.extensions.api.answers.answerInlineQuery
import dev.inmo.tgbotapi.extensions.api.webhook.deleteWebhook
import dev.inmo.configurable_inline_telegram_bot.config.BotConfig
import dev.inmo.configurable_inline_telegram_bot.config.Restrictions
import dev.inmo.configurable_inline_telegram_bot.models.OfferTemplate
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviour
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onBaseInlineQuery
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPolling
import dev.inmo.tgbotapi.updateshandlers.FlowsUpdatesFilter
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable

suspend fun BehaviourContext.enableFormatterBot(
    templates: List<OfferTemplate>,
    restrictions: Restrictions? = null
) {
    onBaseInlineQuery { query ->
        if (restrictions ?.check(query) == false) {
            answerInlineQuery(query, cachedTime = 0)
            return@onBaseInlineQuery
        }
        try {
            answerInlineQuery(
                query,
                templates.mapIndexedNotNull { index, offerTemplate ->
                    offerTemplate.createArticleResult(
                        index.toString(),
                        query.query
                    )
                },
                cachedTime = 0
            )
        } catch (e: RequestException) {
            bot.answerInlineQuery(
                query,
                cachedTime = 0
            )
        }
    }
}

@Serializable
data class FormatterBot(
    private val botConfig: BotConfig,
    private val templates: List<OfferTemplate>,
    private val restrictions: Restrictions? = null
) {

    suspend fun start(scope: CoroutineScope = GlobalScope) {
        val bot = botConfig.createBot()
        val filter = FlowsUpdatesFilter()
        bot.buildBehaviour(
            scope,
            filter
        ) {
            enableFormatterBot(templates, restrictions)
        }
        botConfig.webhookConfig ?.setWebhookAndCreateServer(
            bot,
            filter,
            scope
        ) ?.start(false) ?: bot.apply {
            scope.launch {
                deleteWebhook()
                longPolling(
                    filter,
                    exceptionsHandler = { it.printStackTrace() },
                    scope = scope
                )
            }
        }
    }
}