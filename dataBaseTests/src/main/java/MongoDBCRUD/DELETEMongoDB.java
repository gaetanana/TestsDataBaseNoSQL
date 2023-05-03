package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mongodb.client.result.DeleteResult;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;


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

        //------------------------ Début de la suppression -----------------------

        boolean exist = READMongoDB.collectionExists(collectionName);
        if (!exist) {
            System.out.println("Collection " + collectionName + " not exists");
            return;
        } else {
            System.out.println("Suppression de tous les documents de " + collectionName + " en cours...");
            DeleteResult deleteResult = instanceDeConnection.getDatabase().getCollection(collectionName).deleteMany(new org.bson.Document());

            //------------------------ Fin de la suppression -----------------------

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

            System.out.println("Nombre de documents supprimés: " + deleteResult.getDeletedCount());
            System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
            System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
            System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
            System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
            System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

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
