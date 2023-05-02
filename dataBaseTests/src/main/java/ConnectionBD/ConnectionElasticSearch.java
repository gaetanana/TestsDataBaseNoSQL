package ConnectionBD;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class ConnectionElasticSearch {

    private static RestHighLevelClient client;
    public static void connectToLocalElasticsearch() {
        if (client == null) {
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")));
            System.out.println("Connecté à Elasticsearch");
        } else {
            System.out.println("Le client Elasticsearch est déjà connecté");
        }
    }

    public static void closeConnection() {
        if (client != null) {
            try {
                client.close();
                client = null;
                System.out.println("Le client Elasticsearch a été fermé");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Le client Elasticsearch est déjà fermé");
        }
    }

    public static RestHighLevelClient getClient() {
        return client;
    }
}
