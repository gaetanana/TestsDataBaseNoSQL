package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import redis.clients.jedis.Jedis;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CREATERedis {

    /**
     * Cette fonction permet de créer une clé dans Redis et de lui associer un fichier XML qui
     * sera stocké sous forme JSON.
     *
     * @param nameKey
     * @param pathFileOfXML
     */
    public static void createOneKeyValue(String nameKey, String pathFileOfXML) {
        try {
            ConnectionRedis redisConnection = ConnectionRedis.getInstance();
            // Lis le contenu du fichier XML et le convertis en chaîne de caractères
            String fileContent = new String(Files.readAllBytes(Paths.get(pathFileOfXML)));
            // Convertis le contenu XML en objet JSON
            JSONObject jsonObject = XML.toJSONObject(fileContent);

            // Insère le contenu JSON dans Redis sous la clé spécifiée
            try (Jedis jedis = redisConnection.getConnection()) {
                jedis.set(nameKey, jsonObject.toString());

                // Vérifie que le contenu a bien été inséré
                String storedContent = jedis.get(nameKey);
                System.out.println("Contenu JSON stocké dans Redis : \n" + storedContent);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier XML : " + e.getMessage());
        }
    }

    /**
     * Cette fonction permet de prendre tous les fichier XML d'un dossier et les concerties en JSON puis
     * les insère dans Redis avec comme clé le nom du fichier.
     *
     * @param pathFolderOfXML
     */
    public static void createAllKeyValue(String pathFolderOfXML) throws InterruptedException {
        //--------------------- Initialisation ---------------------

        Instant startTime = Instant.now(); // Enregistre l'heure de début
        int compteurFichier = 0;
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


        //--------------------- Début création des clés ---------------------

        File folder = new File(pathFolderOfXML);
        File[] listOfFiles = folder.listFiles();

        // Crée un ThreadPool avec un nombre fixe de threads
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        ConnectionRedis redisConnection = ConnectionRedis.getInstance();

        System.out.println("Traitement des fichiers XML en cours...");
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".xml")) {
                compteurFichier++;
                //Thread.sleep(50); // 50 ms de pause
                executorService.submit(() -> {
                    try (Jedis jedis = redisConnection.getConnection()) {
                            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                        JSONObject jsonObject = XML.toJSONObject(content);
                        String key = file.getName().substring(0, file.getName().length() - 4);
                        jedis.set(key, jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        // Ferme le ThreadPool et attend la fin de toutes les tâches
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //--------------------- Fin création des clés ---------------------

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

        System.out.println("Nombre de clés créées : " + compteurFichier);
        System.out.println("Durée totale : " + duration.toSeconds() + " secondes");
        System.out.println("Durée totale : " + duration.toMillis() + " millisecondes");
        System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
        System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
        System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
        System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

        System.out.println("Documents créés avec succès");

    }
}