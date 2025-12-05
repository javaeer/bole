package cn.net.yunlou.bole.common.security;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.utils.JsonUtils;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 1.自定义未授权处理器
 *
 * <p>2.注入
 */
@Slf4j
@Component
public class YunlouAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        String username =
                SecurityContextUtils.getCurrentUsername() != null
                        ? SecurityContextUtils.getCurrentUsername()
                        : "unknown";

        log.warn(
                "权限拒绝: 用户[{}]尝试访问 {} {} - 权限不足",
                username,
                request.getMethod(),
                request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String body =
                JsonUtils.toJson(BusinessResponse.error(BusinessStatus.FORBIDDEN_NO_PERMISSION));
        response.getWriter().write(body);
    }
}
