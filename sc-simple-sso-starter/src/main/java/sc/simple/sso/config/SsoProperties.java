package sc.simple.sso.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * SSO单点登录相关配置
 *
 * @author sc
 */
@Data
@ConfigurationProperties(prefix = "sc.sso", ignoreInvalidFields = true)
public class SsoProperties {
    /**
     * 是否SSO认证
     */
    private Boolean enabled = false;

    /**
     * 权限白名单
     */
    private List<String> whiteUrlList = new ArrayList<>();

    /**
     * 单点登录服务器
     */
    private String serverUrl;

    /**
     *appid
     */
    private String appId;


    /**
     * secret
     */
    private String appSecret;
}
