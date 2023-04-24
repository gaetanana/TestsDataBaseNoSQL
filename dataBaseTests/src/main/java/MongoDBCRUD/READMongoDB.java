package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class READMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        getHuman("testCollection");
    }

    /**
     * Cette méthode permet de lire tous les documents dans une collection.
     *
     * @param collectionName
     */
    public static void readCollection(String collectionName) {
        MongoIterable<String> collectionsNames = instanceDeConnection.getDatabase().listCollectionNames();
        //Je vérifie si la collection existe
        for (String name : collectionsNames) {
            //
            if (name.equals(collectionName)) {
                MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
                //J'affiche tous les documents de la collection
                for (Document document : collection.find()) {
                    System.out.println(document);
                }
                return;
            }

        }
        System.err.println("Collection " + collectionName + " does not exist");
    }


    /**
     * Méthode qui renvoie les documents dans lequelle il y a un attribut NoBag = 1.00
     */
    public static void getHuman(String collectionName) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
        Document query = Document.parse("{\"metadata.MetadataStream.VideoAnalytics.Frame.Object.Appearance.Extension.HumanBody.Belonging.Bag.NoBag\": 1.00}");
        System.out.println(query.toJson());
        FindIterable<Document> documents = collection.find(query);

        for (Document document : documents) {
            System.out.println(document.toJson());
        }
    }

    /**
     * Cette méthode me permet de savoir si une collection existe
     *
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
