package sc.simple.sso.check;

import java.io.Serializable;

/**
 * 基础参数
 */
public class BaseParam implements Serializable {

    private String appId;
    private String appSecret;
    private String serverUrl;

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

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}