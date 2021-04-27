package sc.simple.sso.biz.common;

public class AccessTokenContent extends AuthContent {

    public AccessTokenContent(String tgt, boolean sendLogoutRequest, String redirectUri) {
        super(tgt, sendLogoutRequest, redirectUri);
    }
}