package com.backend.pollingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PollingServiceBackendApplication

fun main(args: Array<String>) {
    runApplication<PollingServiceBackendApplication>(*args)
}
