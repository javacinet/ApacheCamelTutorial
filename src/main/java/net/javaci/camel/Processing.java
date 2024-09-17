package net.javaci.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Processing {

    private static final Logger logger = LogManager.getLogger(Processing.class);

    public void start() throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Add a route for processing the file
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // Process the file in "data/input" directory
                from("file:data/input?noop=true&fileName=isd-history.csv")
                        .process(exchange -> {
                            logger.info("Started processing the file.");
                        })
                        .split(body().tokenize("\n"))
                        // Filter lines containing '9999'
                        .filter(simple("${body} contains '9999'"))
                        // Convert each line to XML format
                        .process(exchange -> {
                            String line = exchange.getIn().getBody(String.class);
                            // Split the CSV line into columns
                            String[] columns = line.split(",");
                            StringBuilder xmlBuilder = new StringBuilder();
                            xmlBuilder.append("<record>");
                            for (int i = 0; i < columns.length; i++) {
                                xmlBuilder.append("<field" + i + ">" + columns[i].trim() + "</field" + i + ">");
                            }
                            xmlBuilder.append("</record>");
                            exchange.getIn().setBody(xmlBuilder.toString());

                            // Log the converted XML
                            // logger.info("Converted line to XML: " + xmlBuilder.toString());
                        })
                        // Write the XML to "data/output/output.xml"
                        .to("file:data/output?fileName=output.xml&fileExist=Append")
                        .process(exchange -> {
                            // logger.info("Appended XML to data/output/output.xml");
                        });


            }
        });

        // Start the Camel context
        context.start();

        // Wait for the process to complete
        Thread.sleep(5000);

        // Stop the Camel context
        context.stop();
        logger.info("Ended processing the file.");
    }

    public static void main(String[] args) throws Exception {
        Processing processing = new Processing();
        processing.start();
    }
}
