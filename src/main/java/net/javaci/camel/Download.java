package net.javaci.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Download {

    private static final Logger logger = LogManager.getLogger(Download.class);

    public void start() throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Add a route for downloading the file via FTP
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // Download the file via FTP and save it to the "data/input" directory
                from("ftp://ftp.ncdc.noaa.gov/pub/data/noaa?fileName=isd-history.csv&passiveMode=true")
                        .process(exchange -> {
                            logger.info("Starting FTP download of isd-history.csv");
                        })
                        .log("Downloading file via FTP: ${header.CamelFileName}")
                        .to("file:data/input")
                        .process(exchange -> {
                            logger.info("File downloaded via FTP and saved to data/input/isd-history.csv");
                        });
            }
        });

        // Start the Camel context
        context.start();

        // Wait for the process to complete
        Thread.sleep(5000);

        // Stop the Camel context
        context.stop();
        logger.info("Ended downloading the file.");
    }

    public static void main(String[] args) throws Exception {
        Download download = new Download();
        download.start();
    }
}
