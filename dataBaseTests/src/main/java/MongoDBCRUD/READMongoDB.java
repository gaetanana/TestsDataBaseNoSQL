package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;

import org.bson.Document;

import java.util.Objects;

public class READMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        readCollection("testCollection");
    }


    /**
     * Cette méthode permet de lire une collection avec son nom.
     * @param collectionName
     */
    public static void readCollection(String collectionName) {
        MongoIterable<String> collectionsNames = instanceDeConnection.getDatabase().listCollectionNames();
        //Je vérifie si la collection existe
        for (String name:collectionsNames) {
            //
            if(name.equals(collectionName)){
                MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
                //J'affiche tous les documents de la collection
                for (Document document:collection.find()) {
                    System.out.println(document);
                }
                return;
            }

        }
        System.err.println("Collection " + collectionName + " does not exist");
    }




    /**
     * Cette méthode me permet de savoir si une collection existe
     * @param collectionName
     * @return
     */
    public static boolean collectionExists(String collectionName) {
        MongoIterable<String> collectionNames = instanceDeConnection.getDatabase().listCollectionNames();
        for (String name : collectionNames) {
            if (name.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }


}
