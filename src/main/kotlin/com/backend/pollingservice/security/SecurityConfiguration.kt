package com.backend.pollingservice.security

import com.backend.pollingservice.extensions.forbidden
import com.backend.pollingservice.extensions.unauthorized
import com.backend.pollingservice.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val userService: UserService,
) {
    private val logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean
    fun authenticationManager(
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {
        val authenticationProvider = DaoAuthenticationProvider(userService).apply {
            setPasswordEncoder(passwordEncoder)
        }
        return ProviderManager(authenticationProvider)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/register").permitAll()
                    .anyRequest().authenticated()
            }.exceptionHandling {
                it.authenticationEntryPoint { _, response, authException ->
                    logger.info("Authentication error: ${authException.message}")
                    response.unauthorized(authException.message)
                }

                it.accessDeniedHandler { _, response, authException ->
                    logger.info("Access denied: ${authException.message}")
                    response.forbidden(authException.message)
                }
            }.formLogin { it.disable() }.httpBasic {
                it.authenticationEntryPoint { _, response, authException ->
                    logger.info("HttpBasic | Authentication error: ${authException.message}")
                    response.unauthorized(authException.message)
                }
            }

        return http.build()
    }
}