package sc.simple.sso.check;


import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sc.simple.sso.listener.LogoutListener;
import sc.simple.sso.biz.service.session.SessionStorage;

/**
 * Filter基类
 *
 * @author sc
 */
public abstract class AbstractClient extends BaseParam {

    private SessionStorage sessionMappingStorage;

    public abstract boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response)
            throws IOException;


    public void init(FilterConfig filterConfig) throws ServletException {
    }
	
    public void destroy() {
    }

    protected SessionStorage getSessionMappingStorage() {
        if (sessionMappingStorage == null) {
            sessionMappingStorage = LogoutListener.getSessionMappingStorage();
        }
        return sessionMappingStorage;
    }
}