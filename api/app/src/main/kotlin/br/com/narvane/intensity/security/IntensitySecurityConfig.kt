package br.com.narvane.intensity.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class IntensitySecurityConfig(
    private val intensityJwtAuthenticationFilter: IntensityJwtAuthenticationFilter
) {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun intensitySecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/intensity/**")
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.OPTIONS, "/intensity/**").permitAll()
                it.requestMatchers(HttpMethod.POST, "/intensity/api/v1/auth/session/select-group").authenticated()
                it.requestMatchers(HttpMethod.POST, "/intensity/api/v1/auth/session/select-experience-box").authenticated()
                it.requestMatchers("/intensity/api/v1/auth/**").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(intensityJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
