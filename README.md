# redis-distributed-lock
### Environment

-   Project : Gradle - Kotlin
-   Language : Kotlin
-   Spring Boot : 3.2.0
-   Java : 17
-   Gradle : 8.4

### Dependency

```
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.springframework.integration:spring-integration-redis'
implementation 'it.ozimov:embedded-redis:0.7.2'	// Local 환경에서 실행을 위한 임베디드 레디스 추가
```

### Redis 및 Lock Registry 설정
```kotlin
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
```

```kotlin
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
```

### Usage
```kotlin
@RestController
@RequestMapping("api")
class LockDemoController(
    private val lockRegistry: LockRegistry
) {
    companion object {
        const val NAME = "paul"
    }
    @GetMapping("name")
    fun printMyName(): ResponseEntity<Any> {
        val lock = lockRegistry.obtain(LockRegistryKey.PAUL_REGISTRY_KEY.value)
        return if (lock.tryLock()) {
            val msg = "Hello My name is $NAME"
            logger.info { msg }
            ResponseEntity(msg, HttpStatus.OK)
        } else {
            val msg = "Another process attached lock."
            logger.warn { msg }
            ResponseEntity(msg, HttpStatus.LOCKED)
        }
    }
}
```
