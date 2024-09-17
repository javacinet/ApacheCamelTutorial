package net.javaci.camel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Instantiate and start the download process
            Download download = new Download();
            download.start();

            // Instantiate and start the processing of the downloaded file
            Processing processing = new Processing();
            processing.start();

            // Log a message when the entire process has completed
            logger.info("The entire process has completed successfully.");
        } catch (Exception e) {
            // Log an error message if an exception occurs
            logger.error("An error occurred during the process: ", e);
        }
    }
}
