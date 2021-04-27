package sc.simple.sso.biz.domain.model;

import java.io.Serializable;

/**
 * 应用
 */
public class AppInfo implements Serializable {

    /**
     * 名称
     */
    private String name;
    /**
     * 应用唯一标识
     */
    private String appId;
    /**
     * 应用密钥
     */
    private String appSecret;

    public AppInfo() {
        super();
    }

    public AppInfo(String name, String appId, String appSecret) {
        super();
        this.name = name;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
