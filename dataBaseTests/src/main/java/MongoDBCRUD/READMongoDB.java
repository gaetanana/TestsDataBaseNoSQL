package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class READMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

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
     * Cette fonction permet de trouver combien il y a de documents dans lequelle la valeur de la clé "content" sous "tt:Type" est égal à "Human"
     *
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
     * Cette fonction permet de trouver combien il y a de documents dans lequelle la valeur de la clé "content" sous "tt:Type" est égal à "Human"
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
     * Cette fonction permet de retrouver tous les documents d'une collection en donnant une date.
     */
    public static void getDocumentsWithDate(String nomCollection, String date) {

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

    /**
     * Cette méthode permet de vérifier si il existe un id dans une collection
     */
    public static boolean idExists(String collectionName, String id) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
        Document document = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return document != null;
    }

    /**
     * Cette fonction permet de savoir il existe un champ dans un document JSON présent dans tous les documents d'une collection
     */
    public static boolean fieldExists(String collectionName, String field) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);

        // Trouver un document avec le champ spécifié à l'intérieur de "metadata"
        Document document = collection.find(Filters.exists("metadata." + field)).first();

        // Si le champ est trouvé dans le document, retourner 'true'
        if (document != null) {
            return true;
        }

        // Sinon, vérifier si le champ existe en tant que champ imbriqué dans les documents
        FindIterable<Document> documents = collection.find();

        for (Document doc : documents) {
            Document metadata = doc.get("metadata", Document.class);
            if (metadata != null && fieldExistsInDocument(metadata, field)) {
                return true;
            }
        }

        // Si le champ n'est pas trouvé, retourner 'false'
        return false;
    }

    private static boolean fieldExistsInDocument(Document document, String field) {
        for (String key : document.keySet()) {
            if (key.equals(field)) {
                return true;
            } else if (document.get(key) instanceof Document) {
                if (fieldExistsInDocument(document.get(key, Document.class), field)) {
                    return true;
                }
            } else if (document.get(key) instanceof List) {
                for (Object item : (List<?>) document.get(key)) {
                    if (item instanceof Document) {
                        if (fieldExistsInDocument((Document) item, field)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args){
        System.out.println(fieldExists("testCollection","xmlns"));
    }
}
