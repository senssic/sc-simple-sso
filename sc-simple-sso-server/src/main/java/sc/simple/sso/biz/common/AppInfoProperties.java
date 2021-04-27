package sc.simple.sso.biz.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sc.simple.sso.biz.domain.model.AppInfo;

/**
 * <一句话功能简述> <功能详细描述>
 *
 * @auth:qisensen
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Data
@Component
@ConfigurationProperties(prefix = "sc.sso")
public class AppInfoProperties {
    private List<AppInfo> appInfoList = new ArrayList<>();

}
