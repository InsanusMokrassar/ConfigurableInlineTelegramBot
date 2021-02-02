package dev.inmo.configurable_inline_telegram_bot

import kotlinx.coroutines.*
import java.io.File

fun main(vararg args: String) {
    val config = args.first()
    val bot = try {
        serialFormat.decodeFromString(
            FormatterBot.serializer(),
            config
        )
    } catch (e: Throwable) {
        File(config).readText().let {
            serialFormat.decodeFromString(
                FormatterBot.serializer(),
                it
            )
        }
    }
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        bot.start(scope)
    }
    runBlocking {
        scope.coroutineContext[Job]!!.join()
    }
}