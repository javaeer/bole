package cn.net.yunlou.bole.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    @Value("${app.config.security.jwt-secret:mySecretKey}")
    private String jwtSecret;
    @Value("${app.config.security.jwt-access-expiration:86400000}")
    private long jwtAccessExpiration;
    @Value("${app.config.security.jwt-refresh-expiration:604800000}")
    private long jwtRefreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 从token中提取用户名
     *
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从token中提取到期时间
     *
     * @param token
     * @return
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从token提取令牌类型
     *
     * @param token
     * @return
     */
    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    /**
     * 从token中提取Claim
     *
     * @param token
     * @return
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析 token
     *
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * 获取访问令牌剩余时间（毫秒）
     */
    public Long getAccessTokenRemainingTime(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 获取刷新令牌剩余时间（毫秒）
     */
    public Long getRefreshTokenRemainingTime(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 判断 token 是否有效
     *
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 检查令牌是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 验证token与用户是否匹配
     *
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 验证token是否有效
     *
     * @param token
     * @return
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证访问令牌
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class);
            return TOKEN_TYPE_ACCESS.equals(tokenType) && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证刷新令牌
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class);
            return TOKEN_TYPE_REFRESH.equals(tokenType) && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String username) {
        return generateToken(username, TOKEN_TYPE_ACCESS, jwtAccessExpiration);
    }


    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, TOKEN_TYPE_REFRESH, jwtRefreshExpiration);
    }

    private String generateToken(String username, String tokenType, long expiration) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", tokenType);
        return createToken(claims, username, expiration);
    }


    /**
     * 生成token
     *
     * @param username 用户名
     * @return string
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, jwtAccessExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(now).setExpiration(expiryDate).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }


}