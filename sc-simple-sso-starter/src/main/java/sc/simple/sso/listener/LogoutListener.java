package sc.simple.sso.listener;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import sc.simple.sso.biz.service.session.SessionStorage;

/**
 * 单点登出Listener
 *
 * 注：用于本地session过期，删除accessToken和session的映射关系
 */
public final class LogoutListener implements HttpSessionListener {

    private static SessionStorage sessionMappingStorage;

    @Override
    public void sessionCreated(final HttpSessionEvent event) {
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        sessionMappingStorage.removeBySessionById(session.getId());
    }

    public void setSessionMappingStorage(SessionStorage sms) {
        sessionMappingStorage = sms;
    }

    public static SessionStorage getSessionMappingStorage() {
        return sessionMappingStorage;
    }
}
