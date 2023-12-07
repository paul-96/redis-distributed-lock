package com.paul.demo.config.redis

import com.paul.demo.config.logger.logger
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer

@Profile("local")
@Configuration
class LocalRedisConfig(
    @Value("\${spring.redis.port:6380}") private val redisPort: Int
) {
    private val redisServer: RedisServer = RedisServer(redisPort)

    @PostConstruct
    private fun startRedis() {
        logger.info { "========== Start Redis ==========" }
        redisServer.start()
    }

    @PreDestroy
    private fun stopRedis() {
        logger.info { "========== Stop Redis ==========" }
        redisServer.stop()
    }
}