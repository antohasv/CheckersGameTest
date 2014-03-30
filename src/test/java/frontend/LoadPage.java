package frontend;

import dbService.UserDataSet;
import utils.TemplateHelper;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class LoadPage {
    private static Site[] baseHtml = {Site.INDEX, Site.REG};
    private static Map<Site, String> urlToSite = new HashMap<Site, String>();

    static {
        for (Site site : baseHtml) {
            urlToSite.put(site, getPage(site.getHtmlPath(), null));
        }
    }

    public static String getPage(Site site) {
        return urlToSite.get(site);
    }

    private static String getPage(String page, UserDataSet userSession) {
        StringWriter writer = new StringWriter();
        Map<String, String> data = new HashMap<String, String>();

        data.put(FrontendImpl.PAGE, page);
        createUserSession(userSession, data);

        TemplateHelper.renderTemplate(FrontendImpl.TEMPLATE_HTML, data, writer);
        return writer.toString();
    }

    private static void createUserSession(UserDataSet userSession, Map<String, String> data) {
        if (userSession != null) {
            data.put(FrontendImpl.USER_ID, String.valueOf(userSession.getId()));
            data.put(FrontendImpl.USER_NICKNAME, String.valueOf(userSession.getNickName()));
            data.put(FrontendImpl.USER_RATING, String.valueOf(userSession.getRating()));
        } else {
            data.put(FrontendImpl.USER_ID, FrontendImpl.DEFAULT_USER_ID);
            data.put(FrontendImpl.USER_NICKNAME, FrontendImpl.DEFAULT_USER_NICKNAME);
            data.put(FrontendImpl.USER_RATING, FrontendImpl.DEFAULT_USER_RATING);
        }
    }
}
