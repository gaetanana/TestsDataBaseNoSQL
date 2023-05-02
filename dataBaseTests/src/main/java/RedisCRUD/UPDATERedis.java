package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.File;
import java.io.IOException;
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
        Instant start = Instant.now();
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

        Instant end = Instant.now();
        System.out.println("Temps d'exécution : " + Duration.between(start, end).toMillis() + " ms");
        System.out.println("Nombre de clés modifiées : " + keysModified);
        System.out.println("Nombre de valeurs modifiées : " + valuesModified);
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
        Instant start = Instant.now();
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
                    valuesModified += updateContentRecursive(jsonObject, newValue);

                    if (valuesModified > previousValuesModified) {
                        keysModified++;
                    }

                    jedis.set(key, jsonObject.toString(2));
                }
            }
        }

        Instant end = Instant.now();
        System.out.println("Temps d'exécution : " + Duration.between(start, end).toMillis() + " ms");
        System.out.println("Nombre de clés modifiées : " + keysModified);
        System.out.println("Nombre de valeurs modifiées : " + valuesModified);
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



