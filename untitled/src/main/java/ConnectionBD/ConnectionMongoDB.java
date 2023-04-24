package ConnectionBD;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.Objects;

/**
 * Cette classe permet de se connecter à la base de données MongoDB
 * Elle est un singleton ce qui permet d'avoir une seule instance de la classe.
 */
public class ConnectionMongoDB {

    private static ConnectionMongoDB instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private static final String CONNECTION_STRING = "mongodb://root:examplepassword@sample.host:27017/database?retryWrites=true&w=majority";

    private ConnectionMongoDB() {
        try {
            ConnectionString connectionString = new ConnectionString(CONNECTION_STRING);
            mongoClient = MongoClients.create(connectionString);

            mongoDatabase = mongoClient.getDatabase(Objects.requireNonNull(connectionString.getDatabase()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConnectionMongoDB getInstance() {
        if (instance == null) {
            synchronized (ConnectionMongoDB.class) {
                if (instance == null) {
                    instance = new ConnectionMongoDB();
                }
            }
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

}
