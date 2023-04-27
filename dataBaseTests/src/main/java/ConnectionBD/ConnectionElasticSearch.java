package ConnectionBD;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ConnectionElasticSearch {

    private static RestHighLevelClient instance;

    private ConnectionElasticSearch() {
    }
    public static RestHighLevelClient getInstance() {
        if (instance == null) {
            synchronized (ConnectionElasticSearch.class) {
                if (instance == null) {
                    instance = new RestHighLevelClient(
                            RestClient.builder(
                                    new HttpHost("localhost", 9200, "http")
                            )
                    );
                }
            }
        }
        return instance;
    }
}
