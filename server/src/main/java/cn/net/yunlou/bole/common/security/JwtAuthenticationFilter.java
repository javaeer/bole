package cn.net.yunlou.bole.common.security;

import cn.net.yunlou.bole.common.constant.BaseConstant;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.config.AppConfigProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationService authenticationService;

    private final AppConfigProperties appConfigProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return appConfigProperties.getSecurity().getWhiteList().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader(BaseConstant.TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(bearerToken)) {
            String token = null;
            if (bearerToken.startsWith(BaseConstant.TOKEN_PREFIX)) {
                token = bearerToken.substring(7);
            }

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                UsernamePasswordAuthenticationToken authentication =
                        authenticationService.getAuthentication(token, request);
                if (authentication != null) {
                    SecurityContextUtils.setAuthentication(authentication);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
