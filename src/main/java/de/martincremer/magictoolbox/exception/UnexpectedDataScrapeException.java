package de.martincremer.magictoolbox.exception;

/**
 * Exception if unexpected data is provided during scraping.
 */
public class UnexpectedDataScrapeException extends Exception {

    public UnexpectedDataScrapeException(String msg) {
        super(msg);
    }
}
