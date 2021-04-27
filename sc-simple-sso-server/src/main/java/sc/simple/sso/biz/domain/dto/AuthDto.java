package sc.simple.sso.biz.domain.dto;


import java.io.Serializable;

import sc.simple.sso.biz.common.AuthContent;
import sc.simple.sso.rpc.SsoUser;

public class AuthDto implements Serializable {

    private AuthContent authContent;
    private SsoUser user;

    public AuthDto(AuthContent authContent, SsoUser user) {
        this.authContent = authContent;
        this.user = user;
    }

    public AuthContent getAuthContent() {
        return authContent;
    }

    public void setAuthContent(AuthContent authContent) {
        this.authContent = authContent;
    }

    public SsoUser getUser() {
        return user;
    }

    public void setUser(SsoUser user) {
        this.user = user;
    }
}