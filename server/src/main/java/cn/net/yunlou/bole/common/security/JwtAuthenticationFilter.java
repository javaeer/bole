package cn.net.yunlou.bole.common.security;

import cn.net.yunlou.bole.config.SecurityWhitelistConfig;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationService authenticationService;

    private final SecurityWhitelistConfig securityWhitelistConfig;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return securityWhitelistConfig.getWhiteList().stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String token = SecurityContextUtils.getCurrentToken();

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            UsernamePasswordAuthenticationToken authentication = authenticationService.getAuthentication(token, request);
            if (authentication != null) {
                SecurityContextUtils.setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

}