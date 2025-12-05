package cn.net.yunlou.bole.common.security;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.utils.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 1.自定义处理JWT请求头过期或签名错误的结果
 *
 * <p>2.注入
 */
@Slf4j
@Component
public class YunlouAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        log.error(
                "未授权访问尝试: {} {} - 原因: {}",
                request.getMethod(),
                request.getRequestURI(),
                authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String body =
                JsonUtils.toJson(
                        BusinessResponse.error(BusinessStatus.UNAUTHORIZED_INVALID_EXPIRED));
        response.getWriter().write(body);
    }
}
