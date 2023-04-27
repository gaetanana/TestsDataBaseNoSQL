import ConnectionBD.ConnectionElasticSearch;
import org.elasticsearch.client.RestHighLevelClient;

public class mainElasticSearch {
    public static void main(String[] args) {
        RestHighLevelClient client = ConnectionElasticSearch.getInstance();
        System.out.println(client);
    }
}
