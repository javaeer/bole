package cn.net.yunlou.bole.config;

import cn.net.yunlou.bole.common.security.JwtAuthenticationFilter;
import cn.net.yunlou.bole.common.security.RequestLoggingFilter;
import cn.net.yunlou.bole.common.security.YunlouAccessDeniedHandler;
import cn.net.yunlou.bole.common.security.YunlouAuthenticationEntryPoint;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

@Slf4j
@Configuration
@EnableConfigurationProperties({AppConfigProperties.class})
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RequestLoggingFilter requestLoggingFilter;

    private final AppConfigProperties appConfigProperties;

    private final YunlouAccessDeniedHandler yunlouAccessDeniedHandler;

    private final YunlouAuthenticationEntryPoint yunlouAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String[] whiteList =
                appConfigProperties.getSecurity().getWhiteList().toArray(String[]::new);

        log.info("跳过验证的路径: {}", Arrays.toString(whiteList));

        http
                // 1. 配置CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 禁用CSRF（API项目通常禁用）
                .csrf(csrf -> csrf.disable())

                // 3. 使用无状态Session（JWT场景）
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 配置请求授权
                .authorizeHttpRequests(
                        registry ->
                                registry.requestMatchers(whiteList)
                                        .permitAll() // 白名单放行
                                        .anyRequest()
                                        .authenticated() // 其他需要认证
                        )

                // 5. 配置异常处理器
                .exceptionHandling(
                        handling ->
                                handling.authenticationEntryPoint(
                                                yunlouAuthenticationEntryPoint) // 401处理
                                        .accessDeniedHandler(yunlouAccessDeniedHandler) // 403处理
                        )

                // 6. 添加自定义过滤器
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
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

        if (appConfigProperties.getCors().getAllowCredentials()) {
            // 必须明确指定源，不能有 *
            List<String> origins =
                    Arrays.stream(appConfigProperties.getCors().getAllowedOrigins().split(","))
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
                Arrays.stream(appConfigProperties.getCors().getAllowedMethods().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
        configuration.setAllowedMethods(methods);

        // 允许的请求头
        List<String> headers =
                Arrays.stream(appConfigProperties.getCors().getAllowedHeaders().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
        configuration.setAllowedHeaders(headers);

        // 允许的响应头
        List<String> exposeHeaders =
                Arrays.stream(appConfigProperties.getCors().getExposedHeaders().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
        configuration.setExposedHeaders(exposeHeaders);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
