package sc.simple.sso.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sc.simple.sso.biz.common.AppConstant;
import sc.simple.sso.constant.SsoAuthConstant;
import sc.simple.sso.constant.SsoConstant;
import sc.simple.sso.rpc.Result;
import sc.simple.sso.rpc.SsoUser;
import sc.simple.sso.biz.service.AppService;
import sc.simple.sso.biz.service.UserService;

import sc.simple.sso.biz.service.session.CodeManager;
import sc.simple.sso.biz.service.session.TicketGrantingTicketManager;
import sc.simple.sso.biz.util.CookieUtils;

/**
 * 单点登录管理
 *
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private CodeManager codeManager;
    @Autowired
    private TicketGrantingTicketManager ticketGrantingTicketManager;
    @Autowired
    private UserService userService;
    @Autowired
    private AppService appService;

    /**
     * 登录页
     *
     * @param redirectUri
     * @param appId
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String login(
            @RequestParam(value = SsoConstant.REDIRECT_URI, required = true) String redirectUri,
            @RequestParam(value = SsoAuthConstant.APP_ID, required = true) String appId,
            HttpServletRequest request) throws UnsupportedEncodingException {
        String tgt = CookieUtils.getCookie(request, AppConstant.TGC);
        if (StringUtils.isEmpty(tgt) || ticketGrantingTicketManager.get(tgt) == null) {
            //初始登录跳转到SSO统一登录页面
            return goLoginPath(redirectUri, appId, request);
        }
        //有TGC 跳转到客户端重定向页面(生成授权码，把授权码带过去)
        return generateCodeAndRedirect(redirectUri, tgt);
    }

    /**
     * 登录提交
     *
     * @param redirectUri
     * @param appId
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public String login(
            @RequestParam(value = SsoConstant.REDIRECT_URI, required = true) String redirectUri,
            @RequestParam(value = SsoAuthConstant.APP_ID, required = true) String appId,
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        if (!appService.exists(appId)) {
            request.setAttribute("errorMessage", "非法应用");
            return goLoginPath(redirectUri, appId, request);
        }

        Result<SsoUser> result = userService.login(username, password);
        if (!result.isSuccess()) {
            request.setAttribute("errorMessage", result.getMessage());
            return goLoginPath(redirectUri, appId, request);
        }

        String tgt = CookieUtils.getCookie(request, AppConstant.TGC);
        if (StringUtils.isEmpty(tgt) || ticketGrantingTicketManager.get(tgt) == null) {
            tgt = ticketGrantingTicketManager.generate(result.getData());

            // TGT存cookie，和Cas登录保存cookie中名称一致为：TGC
            CookieUtils.addCookie(AppConstant.TGC, tgt, "/", request, response);
        }
        return generateCodeAndRedirect(redirectUri, tgt);
    }

    /**
     * 设置request的redirectUri和appId参数，跳转到登录页
     *
     * @param redirectUri
     * @param request
     * @return
     */
    private String goLoginPath(String redirectUri, String appId, HttpServletRequest request) {
        request.setAttribute(SsoConstant.REDIRECT_URI, redirectUri);
        request.setAttribute(SsoAuthConstant.APP_ID, appId);
        return AppConstant.LOGIN_PATH;
    }

    /**
     * 生成授权码，跳转到redirectUri
     *
     * @param redirectUri
     * @param tgt
     * @return
     */
    private String generateCodeAndRedirect(String redirectUri, String tgt) throws UnsupportedEncodingException {
        // 生成授权码
        String code = codeManager.generate(tgt, true, redirectUri);
        return "redirect:" + authRedirectUri(redirectUri, code);
    }

    /**
     * 将授权码拼接到回调redirectUri中
     *
     * @param redirectUri
     * @param code
     * @return
     */
    private String authRedirectUri(String redirectUri, String code) throws UnsupportedEncodingException {
        StringBuilder sbf = new StringBuilder(redirectUri);
        if (redirectUri.indexOf("?") > -1) {
            sbf.append("&");
        } else {
            sbf.append("?");
        }
        sbf.append(SsoAuthConstant.AUTH_CODE).append("=").append(code);
        return URLDecoder.decode(sbf.toString(), "utf-8");
    }

}