package sc.simple.sso.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sc.simple.sso.biz.common.AuthContent;
import sc.simple.sso.biz.common.RefreshTokenContent;
import sc.simple.sso.constant.SsoAuthConstant;
import sc.simple.sso.biz.domain.dto.AuthDto;
import sc.simple.sso.enums.GrantTypeEnum;
import sc.simple.sso.rpc.Result;
import sc.simple.sso.rpc.RpcAccessToken;
import sc.simple.sso.rpc.SsoUser;
import sc.simple.sso.biz.service.AppService;
import sc.simple.sso.biz.service.UserService;
import sc.simple.sso.biz.service.session.AccessTokenManager;
import sc.simple.sso.biz.service.session.CodeManager;
import sc.simple.sso.biz.service.session.RefreshTokenManager;
import sc.simple.sso.biz.service.session.TicketGrantingTicketManager;

/**
 * SSO 的access_token和refresh_token服务管理
 *
 */
@RestController
@RequestMapping("/sso")
public class SsoController {

    @Autowired
    private AppService appService;
    @Autowired
    private UserService userService;

    @Autowired
    private CodeManager codeManager;
    @Autowired
    private AccessTokenManager accessTokenManager;
    @Autowired
    private RefreshTokenManager refreshTokenManager;
    @Autowired
    private TicketGrantingTicketManager ticketGrantingTicketManager;

    /**
     * 获取accessToken
     *
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    @RequestMapping(value = "/access_token", method = RequestMethod.GET)
    public Result getAccessToken(
            @RequestParam(value = SsoAuthConstant.GRANT_TYPE, required = true) String grantType,
            @RequestParam(value = SsoAuthConstant.APP_ID, required = true) String appId,
            @RequestParam(value = SsoAuthConstant.APP_SECRET, required = true) String appSecret,
            @RequestParam(value = SsoAuthConstant.AUTH_CODE, required = false) String code,
            @RequestParam(value = SsoAuthConstant.USERNAME, required = false) String username,
            @RequestParam(value = SsoAuthConstant.PASSWORD, required = false) String password) {

        // 校验基本参数
        Result<Void> result = validateParam(grantType, code, username, password);
        if (!result.isSuccess()) {
            return result;
        }

        // 校验应用
        Result<Void> appResult = appService.validate(appId, appSecret);
        if (!appResult.isSuccess()) {
            return appResult;
        }

        // 校验授权
        Result<AuthDto> authResult = validateAuth(grantType, code, username, password);
        if (!authResult.isSuccess()) {
            return authResult;
        }
        AuthDto authDto = authResult.getData();

        // 生成RpcAccessToken返回
        return Result.createSuccess(genereateRpcAccessToken(authDto.getAuthContent(), authDto.getUser(), appId, null));
    }

    private Result<Void> validateParam(String grantType, String code, String username, String password) {
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(code)) {
                return Result.createError("code不能为空");
            }
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return Result.createError("username和password不能为空");
            }
        } else {
            return Result.createError("授权方式不支持");
        }
        return Result.success();
    }

    private Result<AuthDto> validateAuth(String grantType, String code, String username, String password) {
        AuthDto authDto = null;
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            AuthContent authContent = codeManager.validate(code);
            if (authContent == null) {
                return Result.createError("code有误或已过期");
            }

            SsoUser user = ticketGrantingTicketManager.get(authContent.getTgt());
            if (user == null) {
                return Result.createError("服务端session已过期");
            }
            authDto = new AuthDto(authContent, user);
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            // app通过此方式由客户端代理转发http请求到服务端获取accessToken
            Result<SsoUser> loginResult = userService.login(username, password);
            if (!loginResult.isSuccess()) {
                return Result.createError(loginResult.getMessage());
            }
            SsoUser user = loginResult.getData();
            String tgt = ticketGrantingTicketManager.generate(loginResult.getData());
            AuthContent authContent = new AuthContent(tgt, false, null);

            authDto = new AuthDto(authContent, user);
        }
        return Result.createSuccess(authDto);
    }

    /**
     * 刷新accessToken，并延长TGT超时时间
     *
     * accessToken刷新结果有两种： 1. 若accessToken已超时，那么进行refreshToken会生成一个新的accessToken，新的超时时间； 2.
     * 若accessToken未超时，那么进行refreshToken不会改变accessToken，但超时时间会刷新，相当于续期accessToken。
     *
     * @param appId
     * @param refreshToken
     * @return
     */
    @RequestMapping(value = "/refresh_token", method = RequestMethod.GET)
    public Result refreshToken(
            @RequestParam(value = SsoAuthConstant.APP_ID, required = true) String appId,
            @RequestParam(value = SsoAuthConstant.REFRESH_TOKEN, required = true) String refreshToken) {
        if (!appService.exists(appId)) {
            return Result.createError("非法应用");
        }

        RefreshTokenContent refreshTokenContent = refreshTokenManager.validate(refreshToken);
        if (refreshTokenContent == null || !appId.equals(refreshTokenContent.getAppId())) {
            return Result.createError("refreshToken有误或已过期");
        }

        SsoUser user = ticketGrantingTicketManager.get(refreshTokenContent.getTgt());
        if (user == null) {
            return Result.createError("服务端session已过期");
        }

        return Result.createSuccess(
                genereateRpcAccessToken(refreshTokenContent, user, appId, refreshTokenContent.getAccessToken()));
    }

    private RpcAccessToken genereateRpcAccessToken(AuthContent authContent, SsoUser user, String appId,
                                                   String accessToken) {
        String newAccessToken = accessToken;
        if (newAccessToken == null || !accessTokenManager.refresh(newAccessToken)) {
            newAccessToken = accessTokenManager.generate(authContent);
        }

        String refreshToken = refreshTokenManager.generate(authContent, newAccessToken, appId);

        return new RpcAccessToken(newAccessToken, accessTokenManager.getExpiresIn(), refreshToken, user);
    }
}