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
        //readCollection("testCollection");
        //readCollectionWithHuman("testCollection");
        readCollectionWithAge("testCollection");
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
     * Cette méthode me permet d'avoir les documents ou il y a la valeur "Human" dedans
     */
    public static void readCollectionWithHuman(String collectionName) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
        Bson filter = Filters.exists("metadata");
        FindIterable<Document> documents = collection.find(filter);
        for (Document document : documents) {
            if (document.get("metadata").toString().contains("Human")) {
                System.out.println(document);
            }
        }
    }

    /**
     * Cette méthode d'avoir les documents ou il y a la valeur Age > 30
     */
    public static void readCollectionWithAge(String collectionName) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
        JsonWriterSettings settings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();

        for (Document document : collection.find()) {
            String jsonString = document.get("metadata", Document.class).toJson(settings);
            JSONObject jsonObject = new JSONObject(jsonString);
            List<Object> values = searchInJson(jsonObject, "content");
            for (Object value : values) {
                if(value.toString().equals("Human")){
                    System.out.println(document);
                }
            }
        }
    }

    public static List<Object> searchInJson(JSONObject jsonObject, String targetKey) {
        List<Object> values = new ArrayList<>();
        searchInJsonRecursively(jsonObject, targetKey, values);
        return values;
    }

    private static void searchInJsonRecursively(Object object, String targetKey, List<Object> values) {
        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            for (String key : jsonObject.keySet()) {
                if (key.equals(targetKey)) {
                    values.add(jsonObject.get(key));
                } else {
                    searchInJsonRecursively(jsonObject.get(key), targetKey, values);
                }
            }
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0; i < jsonArray.length(); i++) {
                searchInJsonRecursively(jsonArray.get(i), targetKey, values);
            }
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
