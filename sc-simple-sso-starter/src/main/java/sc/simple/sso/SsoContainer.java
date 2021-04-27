package sc.simple.sso;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sc.simple.sso.constant.SsoConstant;
import sc.simple.sso.check.AbstractClient;
import sc.simple.sso.check.BaseParam;

/**
 * sso容器中心
 */
public class SsoContainer extends BaseParam implements Filter {

    /**
     * 排除URL
     */
    protected String excludeUrls;

    private AbstractClient[] filters;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (filters == null || filters.length == 0) {
            throw new IllegalArgumentException("filters不能为空");
        }
        for (AbstractClient filter : filters) {
            filter.setAppId(getAppId());
            filter.setAppSecret(getAppSecret());
            filter.setServerUrl(getServerUrl());
            filter.init(filterConfig);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (isExcludeUrl(httpRequest.getServletPath())) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        for (AbstractClient filter : filters) {
            if (!filter.isAccessAllowed(httpRequest, httpResponse)) {
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isExcludeUrl(String url) {
        if (excludeUrls == null || excludeUrls.isEmpty()) {
            return false;
        }
        Map<Boolean, List<String>> map = Arrays.stream(excludeUrls.split(","))
                .collect(Collectors.partitioningBy(u -> u.endsWith(SsoConstant.URL_FUZZY_MATCH)));
        List<String> urlList = map.get(false);
        if (urlList.contains(url)) {
            return true;
        }
        urlList = map.get(true);
        for (String matchUrl : urlList) {
            if (url.startsWith(matchUrl.replace(SsoConstant.URL_FUZZY_MATCH, ""))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        if (filters == null || filters.length == 0) {
            return;
        }
        for (AbstractClient filter : filters) {
            filter.destroy();
        }
    }

    public void setExcludeUrls(String excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public void setFilters(AbstractClient... filters) {
        this.filters = filters;
    }
}