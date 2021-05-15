package de.martincremer.magictoolbox.appservice;

import de.martincremer.magictoolbox.exception.LoginFailedException;
import de.martincremer.magictoolbox.exception.UnexpectedDataScrapeException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class TestDeckboxUtil {

    DeckboxUtil deckboxUtil;

    @Before
    public void setup() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/deckbox.properties"));
        deckboxUtil = new DeckboxUtil(properties.getProperty("username"), properties.getProperty("password"));
        deckboxUtil.init();
    }

    @Test
    public void test() {
        try {
            deckboxUtil.downloadInventory();
        } catch (Exception | LoginFailedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetSetIdFromUrl() {
        String setId = "2476175";
        String url = "https://deckbox.org/sets/" + setId;
        try {
            Assert.assertEquals(setId, deckboxUtil.getSetIdFromUrl(url));
        } catch (UnexpectedDataScrapeException e) {
            Assert.fail(e.getMessage());
        }
    }
}
