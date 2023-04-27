package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.mongodb.client.result.DeleteResult;


public class DELETEMongoDB {

    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    /**
     * Le bout de code commenté ci-dessous permet de supprimer les documents plus vieux que 10 minutes
     * il faut le mettre dans une fonction main pour le tester.
     */

    /*
       ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable deleteOldDocuments = () -> deleteDocumentsOlderThanTenMinutes("testCollection");
        executor.scheduleAtFixedRate(deleteOldDocuments, 0, 10, TimeUnit.MINUTES);

        boolean keepRunning = true;
        while (keepRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                keepRunning = false;
            }
        }

        executor.shutdown();

     */

    /**
     * Cette méthode permet de supprimer une collection avec son nom.
     *
     * @param collectionName
     */
    public static void deleteCollection(String collectionName) {
        boolean exist = READMongoDB.collectionExists(collectionName);
        if (!exist) {
            System.out.println("Collection " + collectionName + " not exists");
            return;
        } else {
            instanceDeConnection.getDatabase().getCollection(collectionName).drop();
            System.out.println("Collection " + collectionName + " deleted successfully");
        }
    }

    /**
     * Cette méthode permet de supprimer un document avec son ID
     */
    public static void deleteDocument(String collectionName, String id) {
        boolean exist = READMongoDB.collectionExists(collectionName);
        if (!exist) {
            System.out.println("Collection " + collectionName + " not exists");
            return;
        } else {
            instanceDeConnection.getDatabase().getCollection(collectionName).deleteOne(new org.bson.Document("_id", id));
            System.out.println("Document " + id + " deleted successfully");
        }
    }

    /**
     * Cette méthode permet de supprimer tous les documents d'une collection spécifié
     */
    public static void deleteAllDocumentsInOneCollection(String collectionName) {
        //Temps initial
        long startTime = System.currentTimeMillis();
        boolean exist = READMongoDB.collectionExists(collectionName);
        if (!exist) {
            System.out.println("Collection " + collectionName + " not exists");
            return;
        } else {
            DeleteResult deleteResult = instanceDeConnection.getDatabase().getCollection(collectionName).deleteMany(new org.bson.Document());
            System.out.println("Tous les documents de  " + collectionName + " ont étaient supprimés avec succès");
            //Temps final
            long endTime = System.currentTimeMillis();
            //Temps d'exécution
            long timeElapsed = endTime - startTime;
            System.out.println("Temps d'exécution en millisecondes: " + timeElapsed);
            System.out.println("Nombre de documents supprimés: " + deleteResult.getDeletedCount());
        }
    }


    /**
     * Cette méthode permet de simuler la suppression de documents plus vieux que 10 minutes
     */
    public static void deleteDocumentsOlderThanTenMinutes(String collectionName) {
        MongoCollection<Document> collection = instanceDeConnection.getDatabase().getCollection(collectionName);
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String tenMinutesAgoString = tenMinutesAgo.format(formatter);
        Bson filter = Filters.lt("hour", tenMinutesAgoString);
        collection.deleteMany(filter);
        System.out.println("Documents older than " + tenMinutesAgoString + " deleted successfully");
    }


}
