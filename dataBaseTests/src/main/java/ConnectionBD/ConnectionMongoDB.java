package ConnectionBD;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.Objects;

/**
 * Cette classe permet de se connecter à la base de données actiaDataBase MongoDB
 * Elle est un singleton ce qui permet d'avoir une seule instance de la classe.
 */
public class ConnectionMongoDB {

    private static ConnectionMongoDB instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private ConnectionMongoDB() {
        try {
            ConnectionString connectionString = new ConnectionString("mongodb://root:examplepassword@localhost:27017/actiaDataBase?retryWrites=true&w=majority&authSource=admin");
            mongoClient = MongoClients.create(connectionString);
            mongoDatabase = mongoClient.getDatabase("actiaDataBase");

            System.out.println("Connected to the database successfully : " + mongoDatabase.getName());
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
