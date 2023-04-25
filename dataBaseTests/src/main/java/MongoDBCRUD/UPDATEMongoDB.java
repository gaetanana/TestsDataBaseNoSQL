package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class UPDATEMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        updateOneHumanDocument("testCollection", "644839eeadc5085409efd488", new Document("MetadataStream.VideoAnalytics.Frame.Object.Appearance.Class.Type", "Chien"));
    }

    /**
     * Cette fonction permet de mettre à jour un seul document avec son id dans une collection si le document a le content type "Human" avec une nouvelle valeur
     * qui remplace "Human".
     * @param nomCollection
     * @param idDocument
     * @param updateDocument
     */
    public static void updateOneHumanDocument(String nomCollection, String idDocument, Document updateDocument) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(nomCollection);
        Document filter = new Document("_id", new ObjectId(idDocument));
        filter.append("MetadataStream.VideoAnalytics.Frame.Object.Appearance.Class.Type", "Human");
        collection.updateOne(filter, new Document("$set", updateDocument));
        System.out.println("Document updated successfully");
    }


    /**
     * Cette fonction permet de mettre à jour plusieurs documents "Human" dans une collection en fonction des modifications à apporter.
     */
    public static void updateMultipleHumanDocuments(String nomCollection, Document updateDocument) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(nomCollection);
        Document filter = new Document("MetadataStream.VideoAnalytics.Frame.Object.Appearance.Class.Type", "Human");
        collection.updateMany(filter, new Document("$set", updateDocument));
    }

    /**
     * Cette fonction permet de mettre à jour un champ spécifique dans un document "Human" en fonction de la nouvelle valeur du champ.
     */
    public static void updateFieldInHumanDocument(String nomCollection, String fieldName, Object newValue) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(nomCollection);
        Document filter = new Document("MetadataStream.VideoAnalytics.Frame.Object.Appearance.Class.Type", "Human");
        collection.updateOne(filter, set(fieldName, newValue));
    }
}
