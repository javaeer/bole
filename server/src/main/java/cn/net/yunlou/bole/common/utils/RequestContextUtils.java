package cn.net.yunlou.bole.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
/** RequestContextHolder 工具类 */
public class RequestContextUtils {

    /** 🌐 获取当前 HTTP 请求 */
    public static Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(attrs -> attrs instanceof ServletRequestAttributes)
                .map(attrs -> ((ServletRequestAttributes) attrs).getRequest());
    }

    /** 🌐 获取当前 HTTP 响应 */
    public static Optional<HttpServletResponse> getCurrentResponse() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(attrs -> attrs instanceof ServletRequestAttributes)
                .map(attrs -> ((ServletRequestAttributes) attrs).getResponse());
    }

    /** 💾 获取当前会话 */
    public static Optional<HttpSession> getCurrentSession() {
        return getCurrentRequest().map(HttpServletRequest::getSession);
    }

    /** 🔍 获取请求属性 */
    public static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /** 🔍 获取请求属性 */
    public static Optional<Object> getRequestAttribute(String name) {
        return getCurrentRequest().map(request -> request.getAttribute(name));
    }

    /** 🔍 获取请求参数 */
    public static Optional<String> getRequestParameter(String name) {
        return getCurrentRequest().map(request -> request.getParameter(name));
    }

    /** 🌍 获取客户端 IP */
    public static String getClientIp() {
        return getCurrentRequest()
                .map(
                        request -> {
                            String ip = request.getHeader("X-Forwarded-For");
                            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                                ip = request.getHeader("Proxy-Client-IP");
                            }
                            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                                ip = request.getHeader("WL-Proxy-Client-IP");
                            }
                            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                                ip = request.getRemoteAddr();
                            }
                            return ip;
                        })
                .orElse("Unknown");
    }

    /** 🆔 获取会话 ID */
    public static String getSessionId() {
        return getCurrentSession().map(HttpSession::getId).orElse("");
    }
}
