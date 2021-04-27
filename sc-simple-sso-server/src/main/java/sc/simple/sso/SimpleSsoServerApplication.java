package sc.simple.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@SpringBootApplication
public class SimpleSsoServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleSsoServerApplication.class, args);
    }
}
