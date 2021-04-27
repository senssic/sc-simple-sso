package sc.simple.sso.biz.service.session;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.hutool.json.JSONUtil;
import sc.simple.sso.biz.common.AuthContent;
import sc.simple.sso.biz.common.RefreshTokenContent;

/**
 * 分布式刷新凭证管理
 */
@Component
public class RefreshTokenManager {

    @Value("${sc.sso.timeout:7200}")
    private int timeout;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generate(AuthContent authContent, String accessToken, String appId) {
        String resfreshToken = "RT-" + UUID.randomUUID().toString().replaceAll("-", "");
        create(resfreshToken, new RefreshTokenContent(authContent.getTgt(), authContent.isSendLogoutRequest(),
                authContent.getRedirectUri(), accessToken, appId));
        return resfreshToken;
    }


    public void create(String refreshToken, RefreshTokenContent refreshTokenContent) {
        redisTemplate.opsForValue().set(refreshToken, JSONUtil.toJsonStr(refreshTokenContent), getExpiresIn(),
                TimeUnit.SECONDS);
    }


    public RefreshTokenContent validate(String refreshToken) {
        String rtc = redisTemplate.opsForValue().get(refreshToken);
        if (!StringUtils.isEmpty(rtc)) {
            redisTemplate.delete(refreshToken);
        }
        return JSONUtil.toBean(rtc, RefreshTokenContent.class);
    }

    /*
     * refreshToken时效和登录session时效一致
     */
    public int getExpiresIn() {
        return timeout;
    }
}
