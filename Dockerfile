FROM java:9

USER 1000

VOLUME /config.json

ENTRYPOINT ["/configurable_inline_telegram_bot/bin/configurable_inline_telegram_bot", "/config.json"]

ADD ./build/distributions/configurable_inline_telegram_bot.tar /
