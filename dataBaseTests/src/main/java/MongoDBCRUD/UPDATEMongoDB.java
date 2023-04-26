package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

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
            Document metadata = document.get("metadata", Document.class);
            Document metadataStream = metadata.get("tt:MetadataStream", Document.class);
            Document videoAnalytics = metadataStream.get("tt:VideoAnalytics", Document.class);
            Document frame = videoAnalytics.get("tt:Frame", Document.class);
            Document object = frame.get("tt:Object", Document.class);
            Document appearance = object.get("tt:Appearance", Document.class);
            Document clazz = appearance.get("tt:Class", Document.class);
            Document type = clazz.get("tt:Type", Document.class);
            String content = type.getString("content");

            if ("Human".equals(content)) {
                type.put("content", newContentValue);
                collection.replaceOne(filter, document);
                System.out.println("Document updated successfully");
            } else {
                System.out.println("Document not updated. The content is not 'Human'");
            }
        } else {
            System.out.println("Document not found");
        }
    }


    /**
     * Cette fonction permet de mettre à jour tous les documents qui ont un content type "Human" avec une nouvelle valeur qui remplace "Human".
     * Si il dans un document il y a plusieurs fois le même champ en paramètre, seulement le premier sera mis à jour.
     */
    public static void updateMultipleHumanDocuments(String nomCollection, String newContentValue) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(nomCollection);

        // Récupérer tous les documents
        FindIterable<Document> documents = collection.find();

        int updatedDocumentsCount = 0;

        for (Document document : documents) {
            Object metadataObject = document.get("metadata");
            if (metadataObject instanceof Document) {
                Document metadata = (Document) metadataObject;
                Object metadataStreamObject = metadata.get("tt:MetadataStream");
                if (metadataStreamObject instanceof Document) {
                    Document metadataStream = (Document) metadataStreamObject;
                    Object videoAnalyticsObject = metadataStream.get("tt:VideoAnalytics");
                    if (videoAnalyticsObject instanceof Document) {
                        Document videoAnalytics = (Document) videoAnalyticsObject;
                        Object frameObject = videoAnalytics.get("tt:Frame");
                        if (frameObject instanceof Document) {
                            Document frame = (Document) frameObject;
                            Object objectObject = frame.get("tt:Object");
                            if (objectObject instanceof Document) {
                                Document object = (Document) objectObject;
                                Object appearanceObject = object.get("tt:Appearance");
                                if (appearanceObject instanceof Document) {
                                    Document appearance = (Document) appearanceObject;
                                    Object clazzObject = appearance.get("tt:Class");
                                    if (clazzObject instanceof Document) {
                                        Document clazz = (Document) clazzObject;
                                        Object typeObject = clazz.get("tt:Type");

                                        if (typeObject instanceof Document) {
                                            Document type = (Document) typeObject;
                                            String content = type.getString("content");

                                            if ("Human".equals(content)) {
                                                type.put("content", newContentValue);
                                                collection.replaceOne(new Document("_id", document.getObjectId("_id")), document);
                                                updatedDocumentsCount++;
                                            }
                                        } else if (typeObject instanceof List) {
                                            List<Document> types = (List<Document>) typeObject;

                                            for (Document type : types) {
                                                String content = type.getString("content");

                                                if ("Human".equals(content)) {
                                                    type.put("content", newContentValue);
                                                    collection.replaceOne(new Document("_id", document.getObjectId("_id")), document);
                                                    updatedDocumentsCount++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Nombre de documents mis à jour: " + updatedDocumentsCount);
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
        System.out.println("Nombre de documents mis à jour: " + compteurDocument.get());
        System.out.println("Durée totale: " + duration.toMillis() + " ms");

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
