package org.littleshoot.proxy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * Request filter that uses regular expressions on the request host and/or the 
 * request path. If there is a match, it then passes on the request to a 
 * second filter that's passed in.
 */
public class RegexHttpRequestFilter implements HttpRequestFilter {

    private final HttpRequestFilter filter;
    private final boolean filterPaths;
    private final Pattern hostPattern;
    private final Pattern pathPattern;
    private final boolean filterHosts;

    /**
     * Creates a new filter that filters on the request host.
     * 
     * @param hostRegex The regular expression for matching the host.
     * @param filter The filter to delegate to if the host matches.
     * @return The filter.
     */
    public static RegexHttpRequestFilter newHostFilter(final String hostRegex, 
        final HttpRequestFilter filter) {
        return new RegexHttpRequestFilter(hostRegex, "", filter);
    }
    
    /**
     * Creates a new filter that filters on the request host.
     * 
     * @param hostRegex The regular expression for matching the host.
     * @param filter The filter to delegate to if the host matches.
     * @return The filter.
     */
    public static RegexHttpRequestFilter newPathFilter(final String pathRegex,
        final HttpRequestFilter filter) {
        return new RegexHttpRequestFilter("", pathRegex, filter);
    }
    
    /**
     * Creates a new filter that filters on the request host.
     * 
     * @param hostRegex The regular expression for matching the host.
     * @param pathRegex The regular expression for matching the path.
     * @param filter The filter to delegate to if the host matches.
     * @return The filter.
     */
    public static RegexHttpRequestFilter newHostAndPathFilter(
        final String hostRegex, final String pathRegex,
        final HttpRequestFilter filter) {
        return new RegexHttpRequestFilter(hostRegex, pathRegex, filter);
    }
    
    /**
     * Creates a new filter that filters on the request host.
     * 
     * @param hostRegex The regular expression for matching the host.
     * @param pathRegex The regular expression for matching the path.
     * @param filter The filter to delegate to if the host matches.
     * @return The filter.
     */
    private RegexHttpRequestFilter(final String hostRegex, 
        final String pathRegex, final HttpRequestFilter filter) {
        this.hostPattern = Pattern.compile(hostRegex);
        this.pathPattern = Pattern.compile(pathRegex);
        if (StringUtils.isBlank(hostRegex)) {
            filterHosts = false;
        } else {
            filterHosts = true;
        }
        if (StringUtils.isBlank(pathRegex)) {
            filterPaths = false;
        } else {
            filterPaths = true;
        }
        
        this.filter = filter;
    }
    
    public void filter(final HttpRequest httpRequest, Channel channel) {
        if (filterHosts) {
            final List<String> hosts = httpRequest.getHeaders("Host");
            if (hosts != null) {
                if (!hosts.isEmpty()) {
                    final String host = hosts.get(0);
                    final Matcher hostMatch = hostPattern.matcher(host);
                    if (hostMatch.find()) {
                        if (filterPaths) {
                            filterPath(httpRequest, channel);
                        } else {
                            this.filter.filter(httpRequest, channel);
                        }
                    }
                }
            }
        } else if (filterPaths) {
            filterPath(httpRequest, channel);
        }
    }

    private void filterPath(final HttpRequest httpRequest, Channel channel) {
        final String path = httpRequest.getUri();
        final Matcher pathMatch = pathPattern.matcher(path);
        if (pathMatch.matches()) {
            this.filter.filter(httpRequest, channel);
        }
    }
}
