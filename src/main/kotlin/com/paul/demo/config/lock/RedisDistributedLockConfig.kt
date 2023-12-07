package com.paul.demo.config.lock

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.integration.support.locks.LockRegistry

@Configuration
class RedisDistributedLockConfig {
    companion object {
        const val RELEASE_TIME_DURATION = 1000 * 10L
    }

    @Bean
    fun lockRegistry(redisConnectionFactory: RedisConnectionFactory): LockRegistry {
        return RedisLockRegistry(redisConnectionFactory, LockRegistryKey.PAUL_REGISTRY_KEY.value, RELEASE_TIME_DURATION)
    }
}