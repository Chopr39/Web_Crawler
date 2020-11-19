package crawler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserWorker implements Runnable {
    private Parser parser;

    public ParserWorker(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void run() {
        if (parser.queueIsEmpty()) {
            return;
        }
        String task = parser.queuePoll();

        String pageCode = null;
        try {
            pageCode = getPageCode(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, String> links = new HashMap<>();
        try {
            links = getLinks(pageCode, task);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String link : links.keySet()) {
            if (!parser.getLinkTable().contains(link)) {
                parser.queueAdd(link);
                parser.getLinkTable().put(link, links.get(link));
            }
        }
        parser.decrementCounter();
        if (parser.counterIsZero()) {
            parser.setCounter(parser.getQueueSize());
            parser.incrementDepth();
        }

    }

    public void run(String url) {

        String pageCode = null;
        try {
            pageCode = getPageCode(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        parser.getLinkTable().put(url, getTitle(pageCode));
    }

    public static String getPageCode(String url) throws IOException {
        if (url.matches("Error at link compilation")) {
            return "Invalid url";
        }
        URL link = new URL(url);
        URLConnection connection = link.openConnection();
        String page = "";
        if (connection.getContentType().matches("text/html.*")) {
            try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
                page = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                page += "Error occurred:\n";
                page += e.getMessage();
            }
        }
        return page;
    }

    public static HashMap<String, String> getLinks(String pageCode, String url) throws Exception {
        HashMap<String, String> links = new HashMap<>();
        Matcher matcher = Pattern.compile("(<a[^>]*href=[\"'])([\\w\\d:./]*)([\"'][^>]*>)").matcher(pageCode);
        while (true) {
            if (matcher.find()) {
                String link = generateLink(matcher.group(2), url);
                if (!link.matches("Error at link compilation")) {
                    links.putIfAbsent(link, getTitle(getPageCode(link)));
                }
            } else {
                break;
            }
        }

        return links;
    }

    public static String generateLink(String rowLink, String url) {
        String regex = "https?://.*";
        if (rowLink.matches(regex)) {
            return rowLink;
        }
        regex = "//[a-zA-Z\\.]*\\....?/?";
        if (rowLink.matches(regex)) {
            return "https:" + rowLink;
        }
        regex = "[a-zA-Z]*\\.^(html)";
        if (rowLink.matches(regex)) {
            return "https://" + rowLink;
        }
        regex = "[\\w\\d]+";
        if (rowLink.matches(regex)) {
            Matcher matcher = Pattern.compile("(https?://[^/]*/)(.*)").matcher(url);
            if (matcher.find()) {
                return matcher.group(1) + rowLink;
            } else {
                return "Error! (" + rowLink + ").";
            }

        }
        return "Error at link compilation";
    }

    public static boolean isWebPage(String link) throws Exception {
        URL url = new URL(link);
        URLConnection connection = url.openConnection();
        return connection.getContentType().matches("text/html.*");
    }

    public static String getTitle(String pageCode) {
        Matcher matcher = Pattern.compile("(<title>)(.*)(</title>)").matcher(pageCode);
        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return "Not title found";
        }
    }


}
