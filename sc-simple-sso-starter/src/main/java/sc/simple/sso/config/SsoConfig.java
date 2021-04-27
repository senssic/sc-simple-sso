package sc.simple.sso.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.events.AbstractSessionEvent;
import org.springframework.session.web.http.SessionEventHttpSessionListenerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionListener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import sc.simple.sso.SsoContainer;
import sc.simple.sso.biz.service.session.SessionStorage;
import sc.simple.sso.check.WebLoginCheck;
import sc.simple.sso.check.WebLogoutCheck;
import sc.simple.sso.listener.LogoutListener;

@Configuration
@ConditionalOnProperty(value = "sc.sso.enabled", havingValue = "true")
@EnableConfigurationProperties({SsoProperties.class})
public class SsoConfig {
    @Autowired
    private SsoProperties ssoProperties;
    @Autowired
    private SessionStorage sessionMappingStorage;

    @Bean
    public SessionStorage sessionMappingStorage() {
        return new SessionStorage();
    }

    //注册监听器,监听用户登出事件，清除redis中的session缓存
    @Bean
    public ApplicationListener<AbstractSessionEvent> LogoutListener() {
        List<HttpSessionListener> httpSessionListeners = new ArrayList<>();
        LogoutListener logoutListener = new LogoutListener();
        logoutListener.setSessionMappingStorage(sessionMappingStorage);
        httpSessionListeners.add(logoutListener);
        return new SessionEventHttpSessionListenerAdapter(httpSessionListeners);
    }

    /**
     * Web支持单点登录Filter容器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<SsoContainer> simpleContainer() {
        SsoContainer ssoContainer = new SsoContainer();
        ssoContainer.setServerUrl(ssoProperties.getServerUrl());
        ssoContainer.setAppId(ssoProperties.getAppId());
        ssoContainer.setAppSecret(ssoProperties.getAppSecret());
        // 忽略拦截URL,多个逗号分隔
        ssoContainer.setExcludeUrls(CollectionUtil.join(ssoProperties.getWhiteUrlList(), StrUtil.COMMA));
        ssoContainer.setFilters(new WebLogoutCheck(), new WebLoginCheck());
        FilterRegistrationBean<SsoContainer> registration = new FilterRegistrationBean<>();
        registration.setFilter(ssoContainer);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("ssoContainer");
        return registration;
    }


}
