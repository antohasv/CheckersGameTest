package utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;

public class StoreCookieTest {

    public static final String COOKIE_FAKE_NAME = "cookieName";
    public static final String COOKIE_FAKE_PASSWORD = "cookiePassword";
    public static final int NUM_COOKIE = 1000;

    @Test
    public void testStore() throws Exception {
        StoreCookie storeCookie = new StoreCookie(getCookies(NUM_COOKIE));
        for (int i = 0; i < NUM_COOKIE; i++) {
            Assert.assertNotNull(storeCookie.getCookieByName(COOKIE_FAKE_NAME + i));
        }
    }

    private Cookie[] getCookies(int n) {
        Cookie cookies[] = new Cookie[n];
        for (int i = 0; i < n; i++) {
            cookies[i] = new Cookie(COOKIE_FAKE_NAME + i, COOKIE_FAKE_PASSWORD + i);
        }
        return cookies;
    }
}
