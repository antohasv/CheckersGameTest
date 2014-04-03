package frontend;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpServletHelper {

    private static Cookie[] cookies;

    public static void setCookies(Cookie[] cookiesArray) {
        cookies = cookiesArray;
    }

    public static HttpServletRequest getRequestUnCorrectCookie() throws IOException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getCookies()).thenReturn(getUnCorrectCookies());
        when(servletRequest.getMethod()).thenReturn(FrontendImpl.HTTP_POST);
        return servletRequest;
    }

    public static HttpServletRequest getRequestWithCookie(String httpRequest) throws IOException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getCookies()).thenReturn(cookies);
        when(servletRequest.getMethod()).thenReturn(httpRequest);
        return servletRequest;
    }

    public static HttpServletRequest getRequestWithCookie() throws IOException {
        return getRequestWithCookie(FrontendImpl.HTTP_POST);
    }

    public static HttpServletRequest getRequest(String httpRequest) throws IOException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getCookies()).thenReturn(getEmptyCookie());
        when(servletRequest.getMethod()).thenReturn(httpRequest);
        return servletRequest;
    }

    public static HttpServletRequest getRequest(Cookie[] cookies, String httpRequest) throws IOException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getCookies()).thenReturn(cookies);
        when(servletRequest.getMethod()).thenReturn(httpRequest);
        return servletRequest;
    }

    public static HttpServletRequest getRequest() throws IOException {
        return getRequest(FrontendImpl.HTTP_POST);
    }

    private static Cookie[] getEmptyCookie() {
        return new Cookie[0];
    }

    private static Cookie[] getUnCorrectCookies() {
        Cookie cookies[] = new Cookie[2];
        cookies[0] = new Cookie(FrontendImpl.SESSION_ID, "0");
        cookies[1] = new Cookie(FrontendImpl.SERVER_TIME, "0");
        return cookies;
    }


}
