package net.javaci.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Processing2 {

    public void start() throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Dosyayı işlemek için rota
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // "data/input" klasöründeki dosyayı işle
                from("file:data/input?noop=true&fileName=isd-history.csv")
                        .split(body().tokenize("\n"))
                        // Satırları filtrele (örneğin, 'TURKEY' içeren satırlar)
                        .filter(simple("${body} contains '99999'"))
                        // Satırları XML'e dönüştür
                        .process(exchange -> {
                            String line = exchange.getIn().getBody(String.class);
                            // CSV satırını XML formatına dönüştürme
                            String[] columns = line.split(",");
                            StringBuilder xmlBuilder = new StringBuilder();
                            xmlBuilder.append("<record>");
                            for (int i = 0; i < columns.length; i++) {
                                xmlBuilder.append("<field" + i + ">" + columns[i].trim() + "</field" + i + ">");
                            }
                            xmlBuilder.append("</record>");
                            exchange.getIn().setBody(xmlBuilder.toString());
                        })
                        // XML'leri "data/output/output.xml" dosyasına yaz
                        .to("file:data/output?fileName=output.xml&fileExist=Append");
            }
        });

        // Camel Context'i başlat
        context.start();

        // İşlemin tamamlanması için kısa bir süre bekle
        Thread.sleep(5000);

        // Camel Context'i durdur
        context.stop();
    }

    public static void main(String[] args) throws Exception {
        Processing2 processing = new Processing2();
        processing.start();
    }
}
