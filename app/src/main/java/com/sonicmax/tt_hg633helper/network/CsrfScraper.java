package com.sonicmax.tt_hg633helper.network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CsrfScraper {
    private static String CSRF_PARAM = "csrf_param";
    private static String CSRF_TOKEN = "csrf_token";

    public static CsrfHolder scrapeCsrfFields(String html) {
        CsrfHolder csrf = new CsrfHolder();
        Document document = Jsoup.parse(html);
        Elements metaTags = document.getElementsByTag("meta");

        for (Element metaTag : metaTags) {
            if (metaTag.attr("name").equals(CSRF_PARAM)) {
                csrf.setParam(metaTag.attr("content"));
            }
            else if (metaTag.attr("name").equals(CSRF_TOKEN)) {
                csrf.setToken(metaTag.attr("content"));
            }
        }

        return csrf;
    }
}
