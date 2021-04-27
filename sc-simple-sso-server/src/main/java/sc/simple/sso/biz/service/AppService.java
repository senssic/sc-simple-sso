package sc.simple.sso.biz.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.simple.sso.biz.common.AppInfoProperties;
import sc.simple.sso.biz.domain.model.AppInfo;
import sc.simple.sso.rpc.Result;

/**
 * // TODO: 2021/4/26 0026  应用应该从数据库中查询
 */
@Service("appService")
public class AppService {

    @Autowired
    private AppInfoProperties appInfoProperties;


    public boolean exists(String appId) {
        return appInfoProperties.getAppInfoList().stream().anyMatch(app -> app.getAppId().equals(appId));
    }

    public Result<Void> validate(String appId, String appSecret) {
        for (AppInfo app : appInfoProperties.getAppInfoList()) {
            if (app.getAppId().equals(appId)) {
                if (app.getAppSecret().equals(appSecret)) {
                    return Result.success();
                } else {
                    return Result.createError("appSecret有误");
                }
            }
        }
        return Result.createError("appId不存在");
    }
}
