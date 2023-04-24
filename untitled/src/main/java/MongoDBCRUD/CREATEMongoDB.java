package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;


public class CREATEMongoDB {

    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        //Créer une collection
        createCollection("testCollection");

        //Récupère le chemin du fichier JSON
        ClassLoader classLoader = CREATEMongoDB.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("FichersJSON/MetadataJSON.json")).getFile());
        String path = file.getAbsolutePath();
        //Créer un document dans la collection testCollection
        createDocument("testCollection",path);
    }

    /**
     * Cette méthode permet de créer une collection dans la base de données
     * @param collectionName
     */
    public static void createCollection(String collectionName) {
        try {
            instanceDeConnection.getDatabase().createCollection(collectionName);
            System.out.println("Collection " + collectionName + " created successfully");
        } catch (Exception e) {
            System.err.println("Error creating collection: " + e.getMessage());
        }
    }

    /**
     * Cette méthode permet de créer un document dans une collection.
     * @param collectionName le nom de la collection
     * @param filePath le chemin du fichier JSON
     */
    public static void createDocument(String collectionName,String filePath) {
        //Récupère l'heure actuelle
        LocalDateTime now = LocalDateTime.now();
        //Récupère la date actuelle
        String date = now.toString().substring(0,10);

        try {
            MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
            if(Objects.isNull(collection)) {
                System.err.println("Collection " + collectionName + " does not exist");
                return;
            }
            else{
                System.out.println("Collection " + collectionName + " exists");
                // Read the file content and create a JSON object from it
                String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
                JSONObject jsonObject = new JSONObject(fileContent);

                // Convert the JSON object to a BSON Document
                Document document = Document.parse(jsonObject.toString());

                // Insert the document into the collection
                instanceDeConnection.getDatabase().getCollection(collectionName).insertOne(document);

            }
            System.out.println("Document created successfully");
        } catch (Exception e) {
            System.err.println("Error creating document: " + e.getMessage());
        }

    }


}
