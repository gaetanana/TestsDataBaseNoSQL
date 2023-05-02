package ConnectionBD;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;

//Cette classe permet de se connecter à la base de données actiaDataBase CouchDB
public class ConnectionCouchDB {

    private static ConnectionCouchDB instance;
    private CloudantClient cloudantClient;

    private ConnectionCouchDB() {
        try {
            cloudantClient = ClientBuilder.account("admin")
                    .username("admin")
                    .password("password")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConnectionCouchDB getInstance() {
        if (instance == null) {
            synchronized (ConnectionCouchDB.class) {
                if (instance == null) {
                    instance = new ConnectionCouchDB();
                }
            }
        }
        return instance;
    }

    public CloudantClient getCloudantClient() {
        return cloudantClient;
    }
}
