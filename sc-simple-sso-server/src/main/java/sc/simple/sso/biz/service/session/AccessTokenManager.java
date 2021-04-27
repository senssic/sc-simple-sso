package sc.simple.sso.biz.service.session;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import sc.simple.sso.biz.common.AccessTokenContent;
import sc.simple.sso.biz.common.AuthContent;
import sc.simple.sso.constant.SsoConstant;

/**
 * 分布式调用凭证管理
 */
@Component
public class AccessTokenManager {

    @Value("${sc.sso.timeout:7200}")
    private int timeout;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generate(AuthContent authContent) {
        String accessToken = "AT-" + UUID.randomUUID().toString().replaceAll("-", "");
        create(accessToken, new AccessTokenContent(authContent.getTgt(), authContent.isSendLogoutRequest(),
                authContent.getRedirectUri()));
        return accessToken;
    }


    public void create(String accessToken, AccessTokenContent accessTokenContent) {
        redisTemplate.opsForValue().set(accessToken, JSONUtil.toJsonStr(accessTokenContent), getExpiresIn(),
                TimeUnit.SECONDS);

        redisTemplate.opsForSet().add(getKey(accessTokenContent.getTgt()), accessToken);
    }


    public boolean refresh(String accessToken) {
        if (redisTemplate.opsForValue().get(accessToken) == null) {
            return false;
        }
        redisTemplate.expire(accessToken, timeout, TimeUnit.SECONDS);
        return true;
    }


    public void remove(String tgt) {
        Set<String> accessTokenSet = redisTemplate.opsForSet().members(getKey(tgt));
        if (CollectionUtils.isEmpty(accessTokenSet)) {
            return;
        }
        redisTemplate.delete(getKey(tgt));

        accessTokenSet.forEach(accessToken -> {
            String atcStr = redisTemplate.opsForValue().get(accessToken);
            if (StringUtils.isEmpty(atcStr)) {
                return;
            }
            AccessTokenContent accessTokenContent = JSONUtil.toBean(atcStr, AccessTokenContent.class);
            if (accessTokenContent == null || !accessTokenContent.isSendLogoutRequest()) {
                return;
            }
            //设置标记位,标记为删除
            sendLogoutRequest(accessTokenContent.getRedirectUri(), accessToken);
        });
    }

    private String getKey(String tgt) {
        return tgt + "_access_token";
    }


    public int getExpiresIn() {
        return timeout / 2;
    }

    public void sendLogoutRequest(String redirectUri, String accessToken) {
        HttpUtil.createPost(redirectUri).header(SsoConstant.LOGOUT_PARAMETER_NAME, accessToken).execute();
    }
}
