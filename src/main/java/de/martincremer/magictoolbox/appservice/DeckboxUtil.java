package de.martincremer.magictoolbox.appservice;

import de.martincremer.magictoolbox.exception.LoginFailedException;
import de.martincremer.magictoolbox.exception.UnexpectedDataScrapeException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@RequiredArgsConstructor
public class DeckboxUtil {

    @NonNull
    private String username;
    @NonNull
    private String password;

    private WebDriver webDriver;

    private boolean loggedIn = false;

    public void init() {
        webDriver = new HtmlUnitDriver();
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, 10l);
    }

    public void login() throws LoginFailedException {
        webDriver.get("https://deckbox.org/accounts/login?return_to=/");
        webDriver.findElement(By.name("login")).sendKeys(username);
        webDriver.findElement(By.name("password")).sendKeys(password);
        webDriver.findElement(By.id("submit_button")).click();
        if (webDriver.getCurrentUrl().equals("https://deckbox.org/")) {
            loggedIn = true;
        } else {
            loggedIn = false;
            throw new LoginFailedException();
        }
        log.info("URL after Login: " + webDriver.getCurrentUrl());
    }

    private static final Pattern GET_SET_FROM_URL = Pattern.compile("^https://deckbox\\.org/sets/(\\d+)$");

    /**
     * Gets the set id out of a Set URL (e.g. https://deckbox.org/sets/12345)
     *
     * @param url the Set URL
     * @return the set
     * @throws UnexpectedDataScrapeException
     */
    protected String getSetIdFromUrl(String url) throws UnexpectedDataScrapeException {
        String set;
        Matcher setMatcher = GET_SET_FROM_URL.matcher(url);
        if (setMatcher.matches()) {
            set = setMatcher.group(1);
        } else {
            throw new UnexpectedDataScrapeException("Provided URL is not a Deckbox Set URL: " + url);
        }
        return set;
    }

    public void downloadInventory() throws IOException, UnexpectedDataScrapeException, LoginFailedException {
        if(!loggedIn) {
            login();
        }
        webDriver.get("https://deckbox.org/");
        webDriver.findElement(By.className("left_menu")).findElement(By.linkText("Inventory")).click();
        String setId = getSetIdFromUrl(webDriver.getCurrentUrl());
        URL url = new URL("https://deckbox.org/sets/export/" + setId + "?format=csv&f=&s=&o=&columns=");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(line);
            }
        }

    }

}
