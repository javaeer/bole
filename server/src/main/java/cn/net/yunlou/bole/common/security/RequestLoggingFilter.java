package cn.net.yunlou.bole.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 请求日志过滤器
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
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