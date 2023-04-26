package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class UPDATEMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        //updateOneHumanDocument("testCollection", "6448d2b5ad24b95318868a55","Gros chien");
        //updateMultipleHumanDocuments("testCollection", "Chien");

        //updateFieldContent("testCollection", "content", "test");
    }

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
     */




}
