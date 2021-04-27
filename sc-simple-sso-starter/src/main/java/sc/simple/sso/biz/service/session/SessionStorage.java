package sc.simple.sso.biz.service.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.SessionRepository;

import javax.servlet.http.HttpSession;

/**
 * 借鉴CAS
 *
 */
public final class SessionStorage {
	
	private static final String SESSION_TOKEN_KEY = "session_token_key_";
	private static final String TOKEN_SESSION_KEY = "token_session_key_";

	@Autowired
	private SessionRepository<?> sessionRepository;

	@Autowired
	private StringRedisTemplate redisTemplate;

    public synchronized void addSessionById(final String accessToken, final HttpSession session) {
		redisTemplate.opsForValue().set(SESSION_TOKEN_KEY + session.getId(), accessToken);
		
		redisTemplate.opsForValue().set(TOKEN_SESSION_KEY + accessToken, session.getId());
    }


    public synchronized void removeBySessionById(final String sessionId) {
		final String accessToken = redisTemplate.opsForValue().get(SESSION_TOKEN_KEY + sessionId);
		if (accessToken != null) {
			redisTemplate.delete(TOKEN_SESSION_KEY + accessToken);
			redisTemplate.delete(SESSION_TOKEN_KEY + sessionId);
			
			sessionRepository.deleteById(sessionId);
		}
    }


    public synchronized HttpSession removeSessionByMappingId(final String accessToken) {
        final String sessionId = redisTemplate.opsForValue().get(TOKEN_SESSION_KEY + accessToken);
        if (sessionId != null) {
            removeBySessionById(sessionId);
        }
        return null;
    }
}
