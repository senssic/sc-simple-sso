package sc.simple.sso.biz.service.session;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.hutool.json.JSONUtil;
import sc.simple.sso.biz.common.AuthContent;

/**
 * 分布式授权码管理
 */
@Component
public class CodeManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generate(String tgt, boolean sendLogoutRequest, String redirectUri) {
        String code = "code-" + UUID.randomUUID().toString().replaceAll("-", "");
        create(code, new AuthContent(tgt, sendLogoutRequest, redirectUri));
        return code;
    }

    public void create(String code, AuthContent authContent) {
        redisTemplate.opsForValue().set(code, JSONUtil.toJsonStr(authContent), getExpiresIn(), TimeUnit.SECONDS);
    }

    public AuthContent validate(String code) {
        String cc = redisTemplate.opsForValue().get(code);
        if (!StringUtils.isEmpty(cc)) {
            redisTemplate.delete(code);
        }
        return JSONUtil.toBean(cc, AuthContent.class);
    }

    public int getExpiresIn() {
        return 600;
    }
}
