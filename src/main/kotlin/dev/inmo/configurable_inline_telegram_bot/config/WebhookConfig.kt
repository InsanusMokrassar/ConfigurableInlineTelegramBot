package dev.inmo.configurable_inline_telegram_bot.config

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.setWebhookInfoAndStartListenWebhooks
import dev.inmo.tgbotapi.requests.abstracts.toInputFile
import dev.inmo.tgbotapi.requests.webhook.SetWebhook
import dev.inmo.tgbotapi.updateshandlers.UpdatesFilter
import dev.inmo.tgbotapi.updateshandlers.webhook.WebhookPrivateKeyConfig
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.tomcat.Tomcat
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.io.File
import java.util.concurrent.Executors

@Serializable
data class WebhookConfig(
    val url: String,
    val listenHost: String = "0.0.0.0",
    val listenRoute: String = "/",
    val port: Int = System.getenv("PORT").toInt(),
    val certificatePath: String? = null,
    val maxConnections: Int = 40,
    val privateKeyConfig: WebhookPrivateKeyConfig? = null
) {
    init {
        println(this)
    }

    suspend fun setWebhookAndCreateServer(
        requestsExecutor: RequestsExecutor,
        filter: UpdatesFilter,
        scope: CoroutineScope = CoroutineScope(Executors.newFixedThreadPool(maxConnections / 2).asCoroutineDispatcher())
    ): ApplicationEngine = (certificatePath ?.let {
            requestsExecutor.setWebhookInfoAndStartListenWebhooks(
                port,
                Tomcat,
                SetWebhook(url, File(it).toInputFile(), maxAllowedConnections = maxConnections, allowedUpdates = filter.allowedUpdates),
                { throwable: Throwable ->
                    throwable.printStackTrace()
                },
                listenHost,
                listenRoute,
                privateKeyConfig = privateKeyConfig,
                scope = scope,
                block = filter.asUpdateReceiver
            )
        } ?: requestsExecutor.setWebhookInfoAndStartListenWebhooks(
            port,
            Tomcat,
            SetWebhook(
                url,
                maxAllowedConnections = maxConnections,
                allowedUpdates = filter.allowedUpdates
            ),
            { throwable: Throwable ->
                throwable.printStackTrace()
            },
            listenHost,
            listenRoute,
            privateKeyConfig = privateKeyConfig,
            scope = scope,
            block = filter.asUpdateReceiver
        )
    ).also {
        it.environment.connectors.forEach {
            println(it)
        }
        it.start(false)
    }
}