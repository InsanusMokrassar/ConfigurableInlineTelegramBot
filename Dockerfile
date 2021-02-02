FROM java:8

USER 1000

ADD ./build/distributions/configurable_inline_telegram_bot.tar /

VOLUME /config.json

ENTRYPOINT ["/configurable_inline_telegram_bot/bin/configurable_inline_telegram_bot", "/config.json"]
