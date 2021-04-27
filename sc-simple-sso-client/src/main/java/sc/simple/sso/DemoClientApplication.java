package sc.simple.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * <一句话功能简述> <功能详细描述>
 *
 * @auth:qisensen
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@EnableRedisHttpSession
@SpringBootApplication
public class DemoClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoClientApplication.class, args);
    }

}
