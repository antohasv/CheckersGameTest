package frontend;

public enum Site {
    INDEX("/", "index.html"), REG("/reg", "reg.html"), ADMIN("/admin", "admin.html"), PROFILE("/profile", "profile.html"),
    GAME("/game", "game.html"), RULES("/rules", "rules.html"), WAIT("/wait", "wait.html"), ERROR("/error", "404.html"), DEFAULT("", "");

    private String url;
    private String htmlPath;

    Site(String url, String htmlPath) {
        this.url = url;
        this.htmlPath = htmlPath;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlPath() {
        return htmlPath;
    }

    public static Site getSite(String url) {
        for (Site site : Site.values()) {
            if (url.equals(site.getUrl())) {
                return site;
            }
        }
        return DEFAULT;
    }
}