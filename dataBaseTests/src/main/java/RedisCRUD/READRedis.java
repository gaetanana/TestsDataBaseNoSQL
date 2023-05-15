package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class READRedis {


    /**
     * Cette fonction permet de retrouver la valeur d'une clé dans Redis
     */
    public static void readOneKeyValue(String nameKey) {
        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            String storedContent = jedis.get(nameKey);
            System.out.println("Contenu JSON stocké dans Redis : \n" + storedContent);
        }
    }

    public static void readAllKey() {
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

        int compteurCle = 0;

        //--------------------- Lecture ---------------------

        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {

            // Récupère toutes les clés
            ScanParams scanParams = new ScanParams();
            scanParams.match("*");
            scanParams.count(1000);
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanResult<String> scanResult;
            do {
                scanResult = jedis.scan(cursor, scanParams);
                for (String key : scanResult.getResult()) {
                    compteurCle++;
                    System.out.println(key);
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } finally {
            //--------------------- Fin lecture ---------------------

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

            System.out.println("Nombre de clés lues : " + compteurCle);
            System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
            System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
            System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
            System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
            System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

            // Fermez la connexion
            if (redisConnection != null) {
                redisConnection.getConnection().close();
            }
        }
    }

    /**
     * Cette fonction permet de retrouver toutes les clés dans Redis qui contiennent un objet de type "Human"
     */
    public static void readAllKeyWithHuman() {
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

        int compteurCle = 0;

        //--------------------- Lecture ---------------------

        ObjectMapper objectMapper = new ObjectMapper();

        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            // Récupère toutes les clés
            ScanParams scanParams = new ScanParams();
            scanParams.match("*");
            scanParams.count(1000);
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanResult<String> scanResult;
            do {
                scanResult = jedis.scan(cursor, scanParams);
                for (String key : scanResult.getResult()) {
                    String value = jedis.get(key);
                    try {
                        JsonNode jsonNode = objectMapper.readTree(value);
                        List<JsonNode> parents = jsonNode.findParents("content");
                        for (JsonNode parent : parents) {
                            JsonNode contentNode = parent.get("content");
                            if (contentNode != null && contentNode.asText().equals("Human")) {
                                compteurCle++;
                                System.out.println(key);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la conversion en JSON : " + e.getMessage());
                    }
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } finally {
            //--------------------- Fin lecture ---------------------

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

            System.out.println("Nombre de clés lues : " + compteurCle);
            System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
            System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
            System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
            System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
            System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

            // Fermez la connexion
            if (redisConnection != null) {
                redisConnection.getConnection().close();
            }
        }
    }

    /**
     * Cette fonction permet de retrouver toutes les clés dans Redis qui contiennent un objet de type "Human"
     * et un Likelihood supérieur à 0.5
     */
    public static void readAllKeyWithHumanProbability() {
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

        int compteurCle = 0;

        //--------------------- Lecture ---------------------

        ObjectMapper objectMapper = new ObjectMapper();


        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            // Récupère toutes les clés
            ScanParams scanParams = new ScanParams();
            scanParams.match("*");
            scanParams.count(1000);
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanResult<String> scanResult;

            do {
                scanResult = jedis.scan(cursor, scanParams);
                for (String key : scanResult.getResult()) {
                    String value = jedis.get(key);
                    try {
                        JsonNode jsonNode = objectMapper.readTree(value);
                        List<JsonNode> parents = jsonNode.findParents("content");
                        for (JsonNode parent : parents) {
                            JsonNode contentNode = parent.get("content");
                            JsonNode likelihoodNode = parent.get("Likelihood");
                            if (contentNode != null && contentNode.asText().equals("Human") && likelihoodNode != null && likelihoodNode.asDouble() > 0.5) {
                                compteurCle++;
                                System.out.println(key);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la conversion en JSON : " + e.getMessage());
                    }
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } finally {
            //--------------------- Fin lecture ---------------------

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

            System.out.println("Nombre de clés lues : " + compteurCle);
            System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
            System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
            System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
            System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
            System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

            // Fermez la connexion
            if (redisConnection != null) {
                redisConnection.getConnection().close();
            }
        }
    }

    /**
     * Cette fonction permet de savoir si une clé existe dans la base de données Redis
     */
    /**
     * Cette fonction permet de savoir si une clé existe dans la base de données Redis
     */
    public static boolean readOneKeyExist(String nameKey) {
        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            // Récupère la valeur de la clé spécifiée
            String storedContent = jedis.get(nameKey);
            if (storedContent == null) {
                return false;
            } else {
                return true;
            }
        }
    }


}
