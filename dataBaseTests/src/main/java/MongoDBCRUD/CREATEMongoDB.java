package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

import java.time.Instant;
import java.time.Duration;

import static ConnectionBD.ConnectionMongoDB.getInstance;


public class CREATEMongoDB {

    private static final ConnectionMongoDB instanceDeConnection = getInstance();

    public static void main(String[] args) throws URISyntaxException {
        //Créer une collection
        createCollection("testCollection");

        //Récupère le chemin du fichier JSON
        //ClassLoader classLoader = CREATEMongoDB.class.getClassLoader();
        //File file = new File(Objects.requireNonNull(classLoader.getResource("FichersJSON/MetadataJSON.json")).getFile());
        //String path = file.getAbsolutePath();
        //Créer un document dans la collection testCollection
        //createOneDocument("testCollection", path);

        //Récupère le dossier ou se trouve les XML
        ClassLoader classLoader = CREATEMongoDB.class.getClassLoader();

        URL url = classLoader.getResource("FichiersXML");
        if (url == null) {
            System.err.println("Le dossier 'FichiersXML' est introuvable");
            return;
        }
        Path path = Paths.get(url.toURI());
        String folderPath = path.toString();
        creationPlusieursDocuments("testCollection", folderPath);

    }

    /**
     * Cette méthode permet de créer une collection dans la base de données
     *
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
     * Cette méthode permet de créer UN document dans une collection.
     *
     * @param collectionName le nom de la collection
     * @param filePath       le chemin du fichier XML.
     */
    public static void createOneDocument(String collectionName, String filePath) {
        //Récupère l'heure actuelle
        LocalDateTime now = LocalDateTime.now();
        //Récupère la date actuelle
        String date = now.toString().substring(0, 10);

        try {
            boolean exist = READMongoDB.collectionExists(collectionName);
            if (!exist) {
                System.err.println("Collection " + collectionName + " does not exist");
                return;
            } else {
                System.out.println("Collection " + collectionName + " exists");
                // Read the file content and create a JSON object from XML content
                String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
                JSONObject jsonObject = XML.toJSONObject(fileContent);
                // Convert the JSON object to a BSON Document
                Document document = Document.parse(jsonObject.toString());
                // Insert the document into the collection with the current date
                instanceDeConnection.getDatabase().getCollection(collectionName).insertOne(new Document("date", date)
                        .append("hour", now.toString().substring(11, 19))
                        .append("metadata", document)
                );
            }
            System.out.println("Document created successfully");

        } catch (Exception e) {
            System.err.println("Error creating document: " + e.getMessage());
        }

    }

    /**
     * Cette méthode permet de prendre tous les fichiers XML présents dans un dossiers
     * il les convertit en JSON et les insère dans la collection avec l'heure actuelle ainsie que la date.
     */
    public static void creationPlusieursDocuments(String collectionName, String chemingDuDossier) {
        // Enregistre l'heure de début
        Instant startTime = Instant.now();
        //Récupère l'heure actuelle
        LocalDateTime now = LocalDateTime.now();
        //Récupère la date actuelle
        String date = now.toString().substring(0, 10);
        try {
            boolean exist = READMongoDB.collectionExists(collectionName);
            if (!exist) {
                System.err.println("Collection " + collectionName + " does not exist");
                return;
            } else {
                //System.out.println("Collection " + collectionName + " exists");

                // Récupérer les fichiers XML du dossier
                File folder = new File(chemingDuDossier);
                File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

                //Affiche tous les fichiers récupérés
                int compteur = 0;
                for (File file : listOfFiles) {
                    //System.out.println(file.getName());
                    compteur++;
                }
                // Vérifier si des fichiers XML ont été trouvés
                if (listOfFiles == null || listOfFiles.length == 0) {
                    System.err.println("No XML files found in the folder");
                    return;
                }

                int compteur2 = 0;
                // Parcourir chaque fichier XML
                for (File file : listOfFiles) {
                    compteur2++;
                    // Lire le contenu du fichier
                    String xmlContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

                    // Convertir le contenu XML en JSON
                    JSONObject jsonObject = XML.toJSONObject(xmlContent);

                    // Convertir l'objet JSON en un document BSON
                    Document document = Document.parse(jsonObject.toString());

                    // Insérer le document dans la collection
                    instanceDeConnection.getDatabase().getCollection(collectionName).insertOne(new Document("date", date)
                            .append("hour", now.toString().substring(11, 19))
                            .append("metadata", document)
                    );
                }
                // Enregistre l'heure de fin
                Instant endTime = Instant.now();
                // Calcule la durée totale
                Duration duration = Duration.between(startTime, endTime);

                System.out.println("Nombre de fichier: " + compteur);
                System.out.println("Durée totale : " + duration.toMillis() + " secondes");
                System.out.println("Documents created successfully");
            }
        } catch (Exception e) {
            System.out.println("Error creating document: " + e.getMessage());
        }
    }


}
