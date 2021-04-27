package sc.simple.sso.biz.util;


import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import sc.simple.sso.constant.SsoAuthConstant;
import sc.simple.sso.rpc.Result;
import sc.simple.sso.rpc.RpcAccessToken;

/**
 * oss rpc请求调用
 */
public class OssRpcUtils {


    /**
     * 获取accessToken（密码模式，app通过此方式由客户端代理转发http请求到服务端获取accessToken）
     *
     * @param serverUrl
     * @param appId
     * @param appSecret
     * @param username
     * @param password
     * @return
     */
    public static Result<RpcAccessToken> getAccessToken(String serverUrl, String appId, String appSecret, String username,
                                                        String password) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(SsoAuthConstant.APP_ID, appId);
        paramMap.put(SsoAuthConstant.APP_SECRET, appSecret);
        paramMap.put(SsoAuthConstant.USERNAME, username);
        paramMap.put(SsoAuthConstant.PASSWORD, password);
        return getHttpAccessToken(serverUrl + SsoAuthConstant.ACCESS_TOKEN_URL, paramMap);
    }

    /**
     * 获取accessToken（授权码模式）
     *
     * @param serverUrl
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    public static Result<RpcAccessToken> getAccessToken(String serverUrl, String appId, String appSecret, String code) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(SsoAuthConstant.APP_ID, appId);
        paramMap.put(SsoAuthConstant.APP_SECRET, appSecret);
        paramMap.put(SsoAuthConstant.AUTH_CODE, code);
        return getHttpAccessToken(serverUrl + SsoAuthConstant.ACCESS_TOKEN_URL, paramMap);
    }

    /**
     * 刷新accessToken
     *
     * @param serverUrl
     * @param appId
     * @param refreshToken
     * @return
     */
    public static Result<RpcAccessToken> refreshToken(String serverUrl, String appId, String refreshToken) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(SsoAuthConstant.APP_ID, appId);
        paramMap.put(SsoAuthConstant.REFRESH_TOKEN, refreshToken);
        return getHttpAccessToken(serverUrl + SsoAuthConstant.REFRESH_TOKEN_URL, paramMap);
    }

    private static Result<RpcAccessToken> getHttpAccessToken(String url, Map<String, Object> paramMap) {
        String jsonStr = HttpUtil.get(url, paramMap);
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        return JSONUtil.toBean(jsonStr, new TypeReference<Result<RpcAccessToken>>() {
        }, true);
    }
}