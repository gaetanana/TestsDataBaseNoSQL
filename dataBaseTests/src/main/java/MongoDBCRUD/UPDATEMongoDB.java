package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mongodb.client.model.Filters.eq;

public class UPDATEMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    /**
     * Cette fonction permet de mettre à jour un seul document avec son id dans une collection si le document a le content type "Human" avec une nouvelle valeur
     * qui remplace "Human".
     *
     * @param nomCollection
     * @param idDocument
     */

    public static void updateOneHumanDocument(String nomCollection, String idDocument, String newContentValue) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(nomCollection);

        Document filter = new Document("_id", new ObjectId(idDocument));
        Document document = collection.find(filter).first();

        if (document != null) {
            int valuesModified = updateOneHumanRecursively(document, newContentValue);
            if (valuesModified > 0) {
                collection.replaceOne(filter, document);
                System.out.println("Document mis à jour avec succès");
            } else {
                System.out.println("Document non mis à jour car le content type 'Human' n'a pas été trouvé");
            }
        } else {
            System.out.println("Document pas trouvé");
        }
    }

    private static int updateOneHumanRecursively(Object obj, String newValue) {
        int valuesModified = 0;

        if (obj instanceof Document) {
            Document document = (Document) obj;
            if (document.containsKey("tt:Type") && "Human".equals(document.get("tt:Type", Document.class).getString("content"))) {
                document.get("tt:Type", Document.class).put("content", newValue);
                valuesModified++;
            }
            for (String key : document.keySet()) {
                Object value = document.get(key);
                if (value instanceof Document || value instanceof List) {
                    valuesModified += updateOneHumanRecursively(value, newValue);
                }
            }
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            for (Object value : list) {
                if (value instanceof Document || value instanceof List) {
                    valuesModified += updateOneHumanRecursively(value, newValue);
                }
            }
        }

        return valuesModified;
    }





    /**
     * Cette fonction permet de mettre à jour tous les documents qui ont un content type "Human" avec une nouvelle valeur qui remplace "Human".
     * Si il dans un document il y a plusieurs fois le même champ en paramètre, seulement le premier sera mis à jour.
     */

    public static void updateMultipleHumanDocuments(String nomCollection, String newContentValue) {
        Instant start = Instant.now();

        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(nomCollection);

        FindIterable<Document> documents = collection.find();

        int updatedDocumentsCount = 0;
        System.out.println("Mise à jour des documents en cours...");
        for (Document document : documents) {
            String jsonString = document.toJson();
            JSONObject jsonObject = new JSONObject(jsonString);

            int valuesModified = updateHumanRecursively(jsonObject, newContentValue);

            if (valuesModified > 0) {
                Document updatedDocument = Document.parse(jsonObject.toString());
                collection.replaceOne(new Document("_id", document.getObjectId("_id")), updatedDocument);
                updatedDocumentsCount++;
            }
        }

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Temps écoulé: " + timeElapsed.toMillis() + " millisecondes");
        System.out.println("Nombre de documents mis à jour: " + updatedDocumentsCount);
    }

    private static int updateHumanRecursively(Object obj, String newContentValue) {
        int valuesModified = 0;

        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;

            if (jsonObject.has("content") && jsonObject.get("content") instanceof String && jsonObject.getString("content").equals("Human")) {
                jsonObject.put("content", newContentValue);
                valuesModified++;
            }

            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    valuesModified += updateHumanRecursively(value, newContentValue);
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    valuesModified += updateHumanRecursively(value, newContentValue);
                }
            }
        }

        return valuesModified;
    }






    /**
     * Cette fonction pourra changer la valeur d'un champ de tous les documents d'une collection avec une nouvelle valeur.
     * Mais si dans le document il y a plusieurs fois le même champ en paramètre, seulement le premier sera mis à jour.
     */
    public static void updateFieldContent(String nomCollection, String fieldName, Object newValue) {
        // Enregistre l'heure de début
        Instant startTime = Instant.now();

        AtomicInteger compteurDocument = new AtomicInteger();

        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(nomCollection);
        System.out.println("Traitement de la collection: " + nomCollection + " en cours...");

        collection.find().forEach((Block<? super Document>) document -> {
            Document metadata = document.get("metadata", Document.class);
            if (updateNestedField(metadata, fieldName, newValue)) {
                // Sauvegarder le document mis à jour
                collection.replaceOne(eq("_id", document.getObjectId("_id")), document);
                compteurDocument.getAndIncrement();
            }
        });

        // Enregistre l'heure de fin
        Instant endTime = Instant.now();
        // Calcule la durée totale
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Durée totale: " + duration.toMillis() + " ms");
        System.out.println("Nombre de documents mis à jour: " + compteurDocument.get());
        System.out.println("Champ mis à jour avec succès");
    }

    private static boolean updateNestedField(Document document, String fieldName, Object newValue) {
        for (String key : document.keySet()) {
            if (key.equals(fieldName)) {
                document.put(fieldName, newValue);
                return true;
            } else if (document.get(key) instanceof Document) {
                if (updateNestedField(document.get(key, Document.class), fieldName, newValue)) {
                    return true;
                }
            } else if (document.get(key) instanceof List) {
                for (Object item : (List<?>) document.get(key)) {
                    if (item instanceof Document) {
                        if (updateNestedField((Document) item, fieldName, newValue)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}
