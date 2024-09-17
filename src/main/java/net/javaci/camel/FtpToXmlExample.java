package net.javaci.camel;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FtpToXmlExample {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Apache Camel rotalarını ekle
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // NOAA FTP sunucusundan dosyayı indir ve "data/input" klasörüne kaydet
                from("ftp://ftp.ncdc.noaa.gov/pub/data/noaa?fileName=isd-history.csv&passiveMode=true")
                        .log("Dosya indiriliyor: ${header.CamelFileName}")
                        .to("file:data/input");

                // "data/input" klasöründeki dosyayı işle
                from("file:data/input?noop=true&fileName=isd-history.csv")
                        .split(body().tokenize("\n"))
                        // Satırları filtrele (örneğin, 'TURKEY' içeren satırlar)
                        .filter(simple("${body} contains 'TURKEY'"))
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

        // Uygulamayı belirli bir süre çalışır halde tut
        Thread.sleep(20000);

        // Camel Context'i durdur
        context.stop();
    }
}

