package utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

public class StoreCookie {
    private Map<String, String> storeCookie =
            new HashMap<String, String>();

    public StoreCookie(Cookie cookie[]) {
        for (int i = 0; i < cookie.length; i++) {
            storeCookie.put(cookie[i].getName(), cookie[i].getValue());
        }
    }

    public String getCookieByName(String name) {
        if (storeCookie.containsKey(name))
            return storeCookie.get(name);
        return null;
    }
}