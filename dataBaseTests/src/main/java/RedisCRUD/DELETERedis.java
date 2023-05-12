package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;

public class DELETERedis {

    public static void deleteOneKey(String nameKey) {
        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            jedis.del(nameKey);
            System.out.println("La clé a été supprimée");
        }
    }

    public static void deleteAllKey() {
        //--------------------- Initialisation ---------------------

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
        int cleSupprimee = 0;
        //--------------------- Suppression ---------------------


        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            long initialSize = jedis.dbSize();
            jedis.flushAll();
            System.out.println("Toutes les clés ont été supprimées");
            long finalSize = jedis.dbSize();
            cleSupprimee = (int) (initialSize - finalSize);
        }

        //--------------------- Fin suppression ---------------------

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

        System.out.println("Nombre de clés supprimés : " + cleSupprimee);
        System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
        System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
        System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
        System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
        System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

        System.out.println("Documents suppimés avec succès");

    }

    public static void delete50LastKey() {
        int numberOfKeysToDelete = 50;

        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            ScanParams scanParams = new ScanParams().count(numberOfKeysToDelete);
            ScanResult<String> scanResult = jedis.scan("0", scanParams);
            for (String key : scanResult.getResult()) {
                jedis.del(key);
            }
            System.out.println("Les 50 dernières clés ont été supprimées");
            System.out.println("Maintenant il reste " + jedis.dbSize() + " clés dans la base de données");
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression des clés : " + e.getMessage());
        }
    }

}
