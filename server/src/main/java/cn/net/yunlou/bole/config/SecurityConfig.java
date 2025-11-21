package cn.net.yunlou.bole.config;

import cn.net.yunlou.bole.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.config.cors.allowed-origins:http://localhost:8080,http://localhost:3000}")
    private String allowedOrigins;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Knife4j 相关路径白名单
    private static final String[] KNIFE4J_WHITELIST = {
            "/doc.html",
            "/webjars/**",
            "/favicon.ico",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/v3/api-docs/default",  // 添加默认分组
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**"
    };

    // 应用公开接口路径
    private static final String[] APP_WHITELIST = {
            "/api/auth/**",
            "/api/debug/**",  // 添加调试接口
            "/api/health/**",  // 添加调试接口
            "/api/uploads/**",
            "/error",
            "/actuator/health",
            "/actuator/info"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(KNIFE4J_WHITELIST).permitAll()
                        .requestMatchers(APP_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                // 添加请求日志过滤器
                .addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 请求日志过滤器
    private static class RequestLoggingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String requestURI = request.getRequestURI();
            log.debug("请求路径: {} - 方法: {}", requestURI, request.getMethod());

            // 如果是 Knife4j 相关路径，记录详细信息
            if (requestURI.contains("doc.html") || requestURI.contains("api-docs") || requestURI.contains("swagger")) {
                log.info("Knife4j 请求: {} {}?{}", request.getMethod(), requestURI, request.getQueryString());
            }

            filterChain.doFilter(request, response);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOriginPatterns(origins);
        log.info("CORS 允许的源: {}", origins);

        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));

        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers",
                "X-Api-Key", "Cache-Control"
        ));

        // 允许的响应头
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
                "X-Total-Count", "Content-Disposition"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}