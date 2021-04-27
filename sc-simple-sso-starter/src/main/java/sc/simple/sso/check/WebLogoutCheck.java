package sc.simple.sso.check;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sc.simple.sso.constant.SsoConstant;

/**
 * 单点登出
 *
 */
public class WebLogoutCheck extends AbstractClient {

    @Override
    public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = getLogoutParam(request);
        if (accessToken != null) {
            destroySession(accessToken);
            request.getSession().invalidate();
            return false;
        }
        return true;
    }
    
    protected String getLogoutParam(HttpServletRequest request) {
    	return request.getHeader(SsoConstant.LOGOUT_PARAMETER_NAME);
    }

    private void destroySession(String accessToken) {
        final HttpSession session = getSessionMappingStorage().removeSessionByMappingId(accessToken);
        if (session != null) {
            session.invalidate();
        }
    }
}