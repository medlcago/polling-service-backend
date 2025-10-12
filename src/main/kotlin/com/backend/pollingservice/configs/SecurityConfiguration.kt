package com.backend.pollingservice.configs

import com.backend.pollingservice.dto.ApiResponse
import com.backend.pollingservice.extensions.sendJsonResponse
import com.backend.pollingservice.services.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
                it.requestMatchers(HttpMethod.POST, "/api/v1/polls/**").authenticated()
                    .anyRequest().permitAll()
            }.exceptionHandling {
                it.authenticationEntryPoint { _, response, authException ->
                    val apiResponse = ApiResponse.error(
                        message = authException.message,
                        errorCode = HttpStatus.UNAUTHORIZED.name
                    )

                    response.sendJsonResponse(
                        status = HttpStatus.UNAUTHORIZED,
                        contentType = MediaType.APPLICATION_JSON_VALUE,
                        data = apiResponse
                    )
                }

                it.accessDeniedHandler { _, response, authException ->
                    val apiResponse = ApiResponse.error(
                        message = authException.message,
                        errorCode = HttpStatus.FORBIDDEN.name
                    )

                    response.sendJsonResponse(
                        status = HttpStatus.FORBIDDEN,
                        contentType = MediaType.APPLICATION_JSON_VALUE,
                        data = apiResponse
                    )
                }
            }.formLogin { it.disable() }.httpBasic {
                it.authenticationEntryPoint { _, response, authExp ->
                    val apiResponse = ApiResponse.error(
                        message = authExp.message,
                        errorCode = HttpStatus.UNAUTHORIZED.name
                    )

                    response.sendJsonResponse(
                        status = HttpStatus.UNAUTHORIZED,
                        contentType = MediaType.APPLICATION_JSON_VALUE,
                        data = apiResponse
                    )
                }
            }

        return http.build()
    }
}