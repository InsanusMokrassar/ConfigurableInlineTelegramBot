package dev.inmo.configurable_inline_telegram_bot.config

import io.ktor.client.engine.okhttp.OkHttpConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import okhttp3.*
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

@Serializable
data class HttpClientConfig(
    val proxy: ProxySettings? = null,
    val connectTimeout: Long = 0,
    val writeTimeout: Long = 0,
    val readTimeout: Long = 0
) {
    @Transient
    val builder: OkHttpConfig.() -> Unit = {
        config {
            this@HttpClientConfig.proxy ?.let {
                proxy(
                    Proxy(
                        Proxy.Type.SOCKS,
                        InetSocketAddress(
                            it.host,
                            it.port
                        )
                    )
                )
                it.password ?.let { password ->
                    proxyAuthenticator (
                        object : Authenticator {
                            override fun authenticate(route: Route?, response: Response): Request? {
                                return response.request.newBuilder().apply {
                                    addHeader(
                                        "Proxy-Authorization",
                                        Credentials.basic(it.username ?: "", password)
                                    )
                                }.build()
                            }
                        }
                    )
                }
            }
            connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        }
    }
}
