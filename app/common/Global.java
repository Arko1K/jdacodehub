package common;


import com.typesafe.config.Config;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import play.Logger;

import java.net.InetAddress;

public class Global {

    private static TransportClient elasticTransportClient;
    private static String esIndexSubmission, esTypeSubmission;


    public static void setup(Config configuration) {
        try {
            elasticTransportClient = TransportClient.builder()
                    .settings(Settings.builder()
                            .put("cluster.name", configuration.getString("es.cluster"))
                            .build())
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(configuration.getString("es.host")),
                            configuration.getInt("es.port")));

            esIndexSubmission = configuration.getString("es.indexSubmission");
            esTypeSubmission = configuration.getString("es.typeSubmission");
        } catch (Exception ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }


    public static TransportClient getElasticTransportClient() {
        return elasticTransportClient;
    }

    public static String getEsIndexSubmission() {
        return esIndexSubmission;
    }

    public static String getEsTypeSubmission() {
        return esTypeSubmission;
    }
}