package net.javaci.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Download1 {

    public void start() throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Dosyayı indirmek için rota
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // NOAA FTP sunucusundan dosyayı indir ve "data/input" klasörüne kaydet
                from("ftp://ftp.ncdc.noaa.gov/pub/data/noaa?fileName=isd-history.csv&passiveMode=true")
                        .log("Dosya indiriliyor: ${header.CamelFileName}")
                        .to("file:data/input");
//
//                // HTTP üzerinden dosyayı indir ve "data/input" klasörüne kaydet
//                from("timer:fetch?repeatCount=1")
//                        .setHeader("CamelHttpMethod", constant("GET"))
//                        .to("https://www1.ncdc.noaa.gov/pub/data/noaa/isd-history.csv")
//                        .log("Dosya indirildi")
//                        .to("file:data/input2?fileName=isd-history.csv");
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
        Download1 download = new Download1();
        download.start();
    }
}
