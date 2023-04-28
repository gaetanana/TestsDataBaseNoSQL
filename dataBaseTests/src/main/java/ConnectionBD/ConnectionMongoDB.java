package ConnectionBD;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            //System.out.println("Connected to the database successfully : " + mongoDatabase.getName());
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

    public static boolean isDockerRunning() {
        try {
            Process process = Runtime.getRuntime().exec("docker ps");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("mongodb")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void fermetConnexion() {
        if (Objects.nonNull(instance)) {
            instance.mongoClient.close();
        }
    }



}
