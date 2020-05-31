package one.yezii.peanut.core.http.route;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UriRoute {
    private String routeUri;
    private String method;
    private Map<String, String> uriParam;
    private int hash;

    private UriRoute() {
    }

    public static UriRoute of(String uri, String method) {
        UriRoute uriRoute = new UriRoute();
        uriRoute.routeUri = uriRoute.getUriRoute(uri);
        uriRoute.method = method;
        uriRoute.hash = Objects.hash(uriRoute.routeUri, uriRoute.method);
        uriRoute.uriParam = uriRoute.getUriParam(uri);
        return uriRoute;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UriRoute && this.hashCode() == obj.hashCode();
    }

    public Map<String, String> uriParam() {
        return uriParam;
    }

    public String routeUri() {
        return routeUri;
    }

    private Map<String, String> getUriParam(String uri) {
        int questionMarkIndex = uri.indexOf("?");
        if (questionMarkIndex < 0) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        String sub = uri.substring(questionMarkIndex + 1);
        for (String split : sub.split("&")) {
            int equalIndex = split.indexOf("=");
            if (equalIndex < 0) {
                continue;
            }
            map.put(split.substring(0, equalIndex), split.substring(equalIndex + 1));
        }
        return map;
    }

    private String getUriRoute(String uri) {
        String routeUri = uri;
        int firstQuestionMarkIndex = routeUri.indexOf("?");
        if (firstQuestionMarkIndex != -1) {
            routeUri = routeUri.substring(1, firstQuestionMarkIndex);
        }
        //remove all the slash at the last of uri
        while (routeUri.endsWith("/")) {
            routeUri = routeUri.substring(0, routeUri.length() - 1);
        }
        if (routeUri.startsWith("//")) {
            routeUri = routeUri.substring(1);
        }
        if (!routeUri.startsWith("/")) {
            routeUri = "/" + routeUri;
        }

        return routeUri;
    }
}