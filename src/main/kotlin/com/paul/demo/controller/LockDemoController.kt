package com.paul.demo.controller

import com.paul.demo.config.lock.LockRegistryKey
import com.paul.demo.config.logger.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.integration.support.locks.LockRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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