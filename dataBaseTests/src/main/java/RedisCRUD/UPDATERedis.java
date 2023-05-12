package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public class UPDATERedis {


    /**
     * Cette fonction permet de modifier une valeur d'une clé dans Redis
     */

    public static void updateOneKeyValue(String nameKey, String newValue) {
        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            jedis.set(nameKey, newValue);
            System.out.println("La valeur de la clé a été modifiée");
        }
    }

    /**
     * Cette fonction permet de modifier une valeur (qui est un fichier JSON) par un autre fichier XML.
     * Conversion du fichier XML en JSON puis modification de la valeur de la clé.
     */
    public static void updateOneKeyJSON(String nameKey, String pathFileXML) {


        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            // Vérifie que la clé existe
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            // Lit le contenu du fichier XML
            File file = new File(pathFileXML);
            String content = Files.readString(Path.of(file.toURI()));

            // Convertit le contenu XML en JSON
            JSONObject jsonValue = XML.toJSONObject(content);

            // Modifie la valeur de la clé spécifiée avec le JSON
            jedis.set(nameKey, jsonValue.toString());
            System.out.println("La valeur de la clé a été modifiée");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier XML : " + e.getMessage());
        } finally {
            // Ferme la connexion à Redis
            // redisConnection.closeConnection();
        }
    }

    /**
     * Cette fonction permet de modifier toutes les valeurs des clés Redis.
     * En fonction du contenu dans le fichier JSON, si un "Human" est présent dans le fichier JSON, on le remplace par
     * la valeur en paramètre.
     */
    public static void updateAllKeyJSONWithValueHuman(String newValue) {
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

        int keysModified = 0;
        int valuesModified = 0;

        System.out.println("Traitement en cours...");
        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            for (String key : jedis.keys("*")) {
                String jsonString = jedis.get(key);
                if (jsonString != null) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    int previousValuesModified = valuesModified;
                    valuesModified += replaceHumanRecursive(jsonObject, newValue);

                    if (valuesModified > previousValuesModified) {
                        keysModified++;
                    }

                    jedis.set(key, jsonObject.toString(2));
                }
            }
        }

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

        System.out.println("Nombre de clés lues : " + keysModified);
        System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
        System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
        System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
        System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
        System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

        // Ferme la connexion à Redis
        redisConnection.closeConnection(redisConnection.getConnection());
    }


    private static int replaceHumanRecursive(Object obj, String newValue) {
        int valuesModified = 0;

        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    valuesModified += replaceHumanRecursive(value, newValue);
                } else if (value instanceof String && value.equals("Human")) {
                    jsonObject.put(key, newValue);
                    valuesModified++;
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    valuesModified += replaceHumanRecursive(value, newValue);
                } else if (value instanceof String && value.equals("Human")) {
                    jsonArray.put(i, newValue);
                    valuesModified++;
                }
            }
        }
        return valuesModified;
    }


    public static void updateAllKeyJSONWithValue(String newValue) {
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
        int keysModified = 0;
        int valuesModified = 0;

        //-------------------- Mise à jour --------------------

        System.out.println("Traitement en cours...");

        ConnectionRedis redisConnection = ConnectionRedis.getInstance();
        try (Jedis jedis = redisConnection.getConnection()) {
            for (String key : jedis.keys("*")) {
                String jsonString = jedis.get(key);
                if (jsonString != null) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    int previousValuesModified = valuesModified;
                    valuesModified += updateContentRecursive(jsonObject, newValue);

                    if (valuesModified > previousValuesModified) {
                        keysModified++;
                    }

                    jedis.set(key, jsonObject.toString(2));
                }
            }
        }

        //--------------------- Fin mise à jour ---------------------

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

        System.out.println("Nombre de clés lues : " + keysModified);
        System.out.println("Durée totale : " + duration.toMillis() + " milisecondes");
        System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
        System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
        System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
        System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");

        // Ferme la connexion à Redis
        redisConnection.closeConnection(redisConnection.getConnection());
    }


    /**
     * Permet de modifier la valeur de la clé spécifiée par la valeur en paramètre.
     * @param obj
     * @param newValue
     * @return
     */

    private static int updateContentRecursive(Object obj, String newValue) {
        int valuesModified = 0;
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("tt:Class")) {
                JSONObject classObj = jsonObject.getJSONObject("tt:Class");
                if (classObj.has("tt:Type")) {
                    JSONObject typeObj = classObj.getJSONObject("tt:Type");
                    if (typeObj.has("content")) {
                        typeObj.put("content", newValue);
                        valuesModified++;
                    }
                }
            }
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    valuesModified += updateContentRecursive(value, newValue);
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    valuesModified += updateContentRecursive(value, newValue);
                }
            }
        }
        return valuesModified;
    }
}



