package RedisCRUD;
import ConnectionBD.ConnectionRedis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.IOException;
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
        //Temps de traitement
        Instant start = Instant.now();

        int compteurCle = 0;
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
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("Temps de traitement : " + timeElapsed + " ms");
            System.out.println("Nombre de clés dans la base de données  : " + compteurCle);

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
        //Temps de traitement
        Instant start = Instant.now();

        int compteurCle = 0;
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
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("Temps de traitement : " + timeElapsed + " ms");
            System.out.println("Nombre de clés avec le contenu 'Human' : " + compteurCle);
        }
    }

    /**
     * Cette fonction permet de retrouver toutes les clés dans Redis qui contiennent un objet de type "Human"
     * et un Likelihood supérieur à 0.5
     */
    public static void getHumanWithProbability() {
        //Temps de traitement
        Instant start = Instant.now();
        int compteurCle = 0;
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
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("Temps de traitement : " + timeElapsed + " ms");
            System.out.println("Nombre de clés avec le contenu 'Human' : " + compteurCle);
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
