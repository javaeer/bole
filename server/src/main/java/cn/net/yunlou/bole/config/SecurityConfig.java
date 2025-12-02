package cn.net.yunlou.bole.config;

import cn.net.yunlou.bole.common.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityWhitelistConfig securityWhitelistConfig;

    @Value("${app.config.cors.allowed-origins:http://localhost:8080,http://localhost:3000}")
    private String allowedOrigins;

    @Value("${app.config.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${app.config.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.config.cors.exposed-headers:*}")
    private String exposedHeaders;

    @Value("${app.config.cors.allow-credentials:false}")
    private boolean allowCredentials;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String[] whiteList = securityWhitelistConfig.getWhiteList().toArray(String[]::new);

        log.info("跳过验证的路径: {}", Arrays.toString(whiteList));

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authz ->
                                authz.requestMatchers(whiteList)
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                // 添加请求日志过滤器
                .addFilterBefore(
                        new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if (allowCredentials) {
            // 必须明确指定源，不能有 *
            List<String> origins =
                    Arrays.stream(allowedOrigins.split(","))
                            .map(String::trim)
                            .filter(origin -> !"*".equals(origin))
                            .collect(Collectors.toList());
            if (origins.isEmpty()) {
                throw new IllegalStateException(
                        "allowCredentials=true 时，allowedOrigins 不能为空或仅包含 *");
            }
            configuration.setAllowedOriginPatterns(origins);
            configuration.setAllowCredentials(true);
            log.info("CORS 允许的源: {}", origins);
        } else {
            // 不需要凭证 → 可安全使用 *
            configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
            configuration.setAllowCredentials(false);
        }

        // 允许的请求方法
        List<String> methods =
                Arrays.stream(allowedMethods.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
        configuration.setAllowedMethods(methods);

        // 允许的请求头
        List<String> headers =
                Arrays.stream(allowedHeaders.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
        configuration.setAllowedHeaders(headers);

        // 允许的响应头
        List<String> exposeHeaders =
                Arrays.stream(exposedHeaders.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
        configuration.setExposedHeaders(exposeHeaders);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 请求日志过滤器
    private static class RequestLoggingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(
                HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String requestURI = request.getRequestURI();
            log.debug("请求路径: {} - 方法: {}", requestURI, request.getMethod());

            // 如果是 Knife4j 相关路径，记录详细信息
            if (requestURI.contains("doc.html")
                    || requestURI.contains("api-docs")
                    || requestURI.contains("swagger")) {
                log.info(
                        "Knife4j 请求: {} {}?{}",
                        request.getMethod(),
                        requestURI,
                        request.getQueryString());
            }

            filterChain.doFilter(request, response);
        }
    }
}
