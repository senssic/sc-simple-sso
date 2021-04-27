package sc.simple.sso.biz.service.session;


import sc.simple.sso.rpc.RpcAccessToken;
import sc.simple.sso.rpc.SsoUser;

/**
 * 存Session中AccessToken
 */
public class SessionAccessToken extends RpcAccessToken {


	/**
	 * AccessToken过期时间
	 */
	private long expirationTime;

	public SessionAccessToken(String accessToken, int expiresIn, String refreshToken, SsoUser user,
                              long expirationTime) {
		super(accessToken, expiresIn, refreshToken, user);
		this.expirationTime = expirationTime;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() > expirationTime;
	}
}