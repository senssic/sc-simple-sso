package sc.simple.sso.biz.service.session;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.hutool.json.JSONUtil;
import sc.simple.sso.rpc.SsoUser;

/**
 * 分布式登录凭证管理
 */
@Component
public class TicketGrantingTicketManager {

    @Value("${sc.sso.timeout:7200}")
    private int timeout;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generate(SsoUser user) {
        String tgt = "TGT-" + UUID.randomUUID().toString().replaceAll("-", "");
        create(tgt, user);
        return tgt;
    }

    public void create(String tgt, SsoUser user) {
        redisTemplate.opsForValue().set(tgt, JSONUtil.toJsonStr(user), getExpiresIn(),
                TimeUnit.SECONDS);
    }


    public SsoUser get(String tgt) {
        String user = redisTemplate.opsForValue().get(tgt);
        if (StringUtils.isEmpty(user)) {
            return null;
        }
        redisTemplate.expire(tgt, timeout, TimeUnit.SECONDS);
        return JSONUtil.toBean(user, SsoUser.class);
    }


    public void remove(String tgt) {
        redisTemplate.delete(tgt);
    }


    public int getExpiresIn() {
        return timeout;
    }
}