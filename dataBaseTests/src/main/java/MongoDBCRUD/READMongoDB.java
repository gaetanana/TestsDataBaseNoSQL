package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class READMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        //getHuman("testCollection");
        getHumanWithProbability("testCollection");
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
     * Cette méthode permet de trouver combien il y a de documents dans lequelle la valeur de la clé "content" sous "tt:Type" est égal à "Human"
     * @param collectionName
     */
    public static void getHuman(String collectionName) {
        // Enregistre l'heure de début
        Instant startTime = Instant.now();

        int nbResult = 0;
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);

        // Créez le filtre pour rechercher uniquement les documents où le champ "content" sous "tt:Type" est égal à "Human"
        Bson filter = Filters.eq("metadata.tt:MetadataStream.tt:VideoAnalytics.tt:Frame.tt:Object.tt:Appearance.tt:Class.tt:Type.content", "Human");

        // Récupère seulement la colonne "metadata" des documents présents dans la collection qui correspondent au filtre
        FindIterable<Document> documents = collection.find(filter).projection(Projections.include("metadata"));

        for (Document d : documents) {
            Document colonne = (Document) d.get("metadata");
            String jsoncolonne = colonne.toJson();
            //System.out.println(jsoncolonne);
            nbResult++;
        }
        // Enregistre l'heure de fin
        Instant endTime = Instant.now();
        // Calcule la durée totale
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Requête du nombre de documents dans la collection " + collectionName + " dont le type est Human");
        System.out.println("Durée totale : " + duration.toMillis() + " secondes");
        System.out.println("Nombre de résultats: " + nbResult);
    }

    /**
     * Cette méthode permet de trouver combien il y a de documents dans lequelle la valeur de la clé "content" sous "tt:Type" est égal à "Human"
     * avec une probabilité supérieur à 0.5
     */
    public static void getHumanWithProbability(String collectionName) {
        // Enregistre l'heure de début
        Instant startTime = Instant.now();
        int nbResult = 0;
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);

        // Créez le filtre pour rechercher uniquement les documents où le champ "content" sous "tt:Type" est égal à "Human"
        Bson humanFilter = Filters.eq("metadata.tt:MetadataStream.tt:VideoAnalytics.tt:Frame.tt:Object.tt:Appearance.tt:Class.tt:Type.content", "Human");

        // Créez un autre filtre pour rechercher uniquement les documents où le champ "Likelihood" est supérieur à 0.5
        Bson likelihoodFilter = Filters.gt("metadata.tt:MetadataStream.tt:VideoAnalytics.tt:Frame.tt:Object.tt:Appearance.tt:Class.tt:Type.Likelihood", 0.5);

        // Combine les filtres avec l'opérateur AND
        Bson combinedFilter = Filters.and(humanFilter, likelihoodFilter);

        // Récupère seulement la colonne "metadata" des documents présents dans la collection qui correspondent au filtre combiné
        FindIterable<Document> documents = collection.find(combinedFilter).projection(Projections.include("metadata"));
        for (Document d : documents) {
            Document colonne = (Document) d.get("metadata");
            String jsoncolonne = colonne.toJson();
            System.out.println(jsoncolonne);
            nbResult++;
        }
        // Enregistre l'heure de fin
        Instant endTime = Instant.now();
        // Calcule la durée totale
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Requête du nombre de documents dans la collection " + collectionName + " avec une probabilité supérieur à 0.5 et dont le type est Human");
        System.out.println("Durée totale : " + duration.toMillis() + " secondes");
        System.out.println("Nombre de résultats: " + nbResult);
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
