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
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class READMongoDB {
    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    /**
     * Cette méthode permet de lire tous les documents dans une collection.
     * @param collectionName
     */
    public static void readCollection(String collectionName) {
        //------------------------ Initialisation --------------------------------
        Instant startTime = Instant.now(); // Enregistre l'heure de début

        //Partie processeur
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long cpuBefore = threadBean.getCurrentThreadCpuTime();

        //Partie mémoire vive
        long beforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        //Partie disque physique
        oshi.SystemInfo systemInfo = new oshi.SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        OSProcess currentProcess = os.getProcess(os.getProcessId());

        long bytesReadBefore = currentProcess.getBytesRead();
        long bytesWrittenBefore = currentProcess.getBytesWritten();
        long startTimeDisque = System.nanoTime();

        //---------------------------- Lecture -----------------------------------
        MongoIterable<String> collectionsNames = instanceDeConnection.getDatabase().listCollectionNames();
        int nbFichiers = 0;
        //Je vérifie si la collection existe
        for (String name : collectionsNames) {

            if (name.equals(collectionName)) {
                MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
                //J'affiche tous les documents de la collection
                for (Document document : collection.find()) {
                    nbFichiers++;
                    System.out.println(document);
                }
                //--------------------------- Fin de lecture --------------------------

                Instant endTime = Instant.now(); // Enregistre l'heure de fin
                Duration duration = Duration.between(startTime, endTime); // Calcule la durée totale


                //Partie processeur
                long cpuAfter = threadBean.getCurrentThreadCpuTime();
                long cpuUsed = cpuAfter - cpuBefore;
                double cpuPercentage = (double) cpuUsed / (duration.toMillis() * 1000000) * 100;

                //Partie mémoire vive
                long afterUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long memoryUsed = afterUsedMemory - beforeUsedMemory;
                double memoryPercentage = (double) memoryUsed / Runtime.getRuntime().maxMemory() * 100;

                //Partie disque physique
                currentProcess = os.getProcess(os.getProcessId()); // Mettre à jour les informations du processus
                long bytesReadAfter = currentProcess.getBytesRead();
                long bytesWrittenAfter = currentProcess.getBytesWritten();
                long endTimeDisque = System.nanoTime();

                // Calculez la différence d'utilisation du disque
                long bytesReadDifference = bytesReadAfter - bytesReadBefore;
                long bytesWrittenDifference = bytesWrittenAfter - bytesWrittenBefore;
                long elapsedTime = endTimeDisque - startTimeDisque;

                double bytesReadPerSecond = (double) bytesReadDifference / (elapsedTime / 1_000_000_000.0);
                double bytesWrittenPerSecond = (double) bytesWrittenDifference / (elapsedTime / 1_000_000_000.0);

                System.out.println("Nombre de fichiers lus : " + nbFichiers);
                System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
                System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
                System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
                System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
                System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");
                return;
            }
        }
        System.err.println("Collection " + collectionName + " n'existe pas");
    }

    /**
     * Cette fonction permet de trouver combien il y a de documents dans lequel la valeur de la clé "content" sous "tt:Type" est égal à "Human"
     * @param collectionName
     */
    public static void getHuman(String collectionName) {
        //------------------------ Initialisation --------------------------------
        Instant startTime = Instant.now(); // Enregistre l'heure de début
        LocalDateTime now = LocalDateTime.now(); // Récupère l'heure actuelle
        String date = now.toString().substring(0, 10); // Récupère la date actuelle

        //Partie processeur
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long cpuBefore = threadBean.getCurrentThreadCpuTime();

        //Partie mémoire vive
        long beforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        //Partie disque physique
        oshi.SystemInfo systemInfo = new oshi.SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        OSProcess currentProcess = os.getProcess(os.getProcessId());

        long bytesReadBefore = currentProcess.getBytesRead();
        long bytesWrittenBefore = currentProcess.getBytesWritten();
        long startTimeDisque = System.nanoTime();
        int nbResult = 0;
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);

        //--------------------------- Début de la recherche --------------------------

        // Créez le filtre pour rechercher uniquement les documents où le champ "content" sous "tt:Type" est égal à "Human"
        Bson filter = Filters.eq("metadata.tt:MetadataStream.tt:VideoAnalytics.tt:Frame.tt:Object.tt:Appearance.tt:Class.tt:Type.content", "Human");

        // Récupère seulement la colonne "metadata" des documents présents dans la collection qui correspondent au filtre
        FindIterable<Document> documents = collection.find(filter).projection(Projections.include("metadata"));

        //-------------------------  Affichage des résultats -------------------------

        for (Document d : documents) {
            Document colonne = (Document) d.get("metadata");
            String jsoncolonne = colonne.toJson();
            //System.out.println(jsoncolonne);
            nbResult++;
        }
        //--------------------------- Fin de lecture --------------------------

        Instant endTime = Instant.now(); // Enregistre l'heure de fin
        Duration duration = Duration.between(startTime, endTime); // Calcule la durée totale


        //Partie processeur
        long cpuAfter = threadBean.getCurrentThreadCpuTime();
        long cpuUsed = cpuAfter - cpuBefore;
        double cpuPercentage = (double) cpuUsed / (duration.toMillis() * 1000000) * 100;

        //Partie mémoire vive
        long afterUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = afterUsedMemory - beforeUsedMemory;
        double memoryPercentage = (double) memoryUsed / Runtime.getRuntime().maxMemory() * 100;

        //Partie disque physique
        currentProcess = os.getProcess(os.getProcessId()); // Mettre à jour les informations du processus
        long bytesReadAfter = currentProcess.getBytesRead();
        long bytesWrittenAfter = currentProcess.getBytesWritten();
        long endTimeDisque = System.nanoTime();

        // Calculez la différence d'utilisation du disque
        long bytesReadDifference = bytesReadAfter - bytesReadBefore;
        long bytesWrittenDifference = bytesWrittenAfter - bytesWrittenBefore;
        long elapsedTime = endTimeDisque - startTimeDisque;

        double bytesReadPerSecond = (double) bytesReadDifference / (elapsedTime / 1_000_000_000.0);
        double bytesWrittenPerSecond = (double) bytesWrittenDifference / (elapsedTime / 1_000_000_000.0);

        System.out.println("Nombre de résultats: " + nbResult);
        System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
        System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
        System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
        System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
        System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

    }

    /**
     * Cette fonction permet de trouver combien il y a de documents dans lequelle la valeur de la clé "content" sous "tt:Type" est égal à "Human"
     * avec une probabilité supérieur à 0.5
     */
    public static void getHumanWithProbability(String collectionName) {
        //------------------------ Initialisation --------------------------------
        Instant startTime = Instant.now(); // Enregistre l'heure de début
        LocalDateTime now = LocalDateTime.now(); // Récupère l'heure actuelle
        String date = now.toString().substring(0, 10); // Récupère la date actuelle
        //Partie processeur
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long cpuBefore = threadBean.getCurrentThreadCpuTime();
        //Partie mémoire vive
        long beforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        //Partie disque physique
        oshi.SystemInfo systemInfo = new oshi.SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        OSProcess currentProcess = os.getProcess(os.getProcessId());
        long bytesReadBefore = currentProcess.getBytesRead();
        long bytesWrittenBefore = currentProcess.getBytesWritten();
        long startTimeDisque = System.nanoTime();
        int nbResult = 0;
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);

        //--------------------------- Début de la recherche --------------------------


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
        System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
        System.out.println("Nombre de résultats: " + nbResult);
    }

    /**
     * Cette fonction permet de retrouver tous les documents d'une collection en donnant une date.
     * Pas encore fonctionnelle
     */
    public static void getDocumentsWithDate(String nomCollection, String date) {

    }

    //En bas ce sont des méthodes auxiliaires
    //------------------------------------------------------------------------------------------------------------------
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

    /**
     * Cette fonction permet de savoir si un champ existe dans un document JSON
     * @param document
     * @param field
     * @return
     */
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

}
