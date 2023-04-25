package RedisCRUD;
import ConnectionBD.ConnectionRedis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.IOException;

public class READRedis {

    private static final ConnectionRedis instanceDeConnection = ConnectionRedis.getInstance();

    public static void main(String[] args) {
        //readOneKeyValue("Onvif_Metadata_C1000_2023-04-21_16-44-55.650.xml");
        //readAllKey();
        readAllKeyWithHuman();
    }

    /**
     * Cette fonction permet de retrouver la valeur d'une clé dans Redis
     */
    public static void readOneKeyValue(String nameKey) {
        try {
            // Récupère la valeur de la clé spécifiée
            String storedContent = instanceDeConnection.getConnection().get(nameKey);
            System.out.println("Contenu JSON stocké dans Redis : \n" + storedContent);
        } finally {
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }
    }

    /**
     * Cette fonction permet de retrouver toutes les clés dans Redis
     */
    public static void readAllKey() {
        int compteurCle = 0;
        try {
            // Récupère toutes les clés
            ScanParams scanParams = new ScanParams();
            scanParams.match("*");
            scanParams.count(1000);
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanResult<String> scanResult;
            do {
                scanResult = instanceDeConnection.getConnection().scan(cursor, scanParams);
                for (String key : scanResult.getResult()) {
                    compteurCle++;
                    System.out.println(key);
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } finally {
            System.out.println("Nombre de clés dans la  : " + compteurCle);
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }
    }

    /**
     * Cette fonction me permet de me retourner les clé dont les valeurs possèdent dans leur valeur un fichier Json qui à le content : Human
     */
    /**
     * Cette fonction me permet de me retourner les clé dont les valeurs possèdent dans leur valeur un fichier Json qui à le content : Human
     */
    public static void readAllKeyWithHuman() {
        int compteurCle = 0;
        ObjectMapper objectMapper = new ObjectMapper();

        try (Jedis jedis = instanceDeConnection.getConnection()) {
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
                        if (jsonNode != null && jsonNode.get("content") != null && jsonNode.get("content").asText().equals("Human")) {
                            compteurCle++;
                            System.out.println(key);
                        }
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la conversion en JSON : " + e.getMessage());
                    }
                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } finally {
            System.out.println("Nombre de clés avec le contenu 'Human' : " + compteurCle);
        }
    }


    /**
     * Cette fonction permet de savoir si une clé existe dans la base de données Redis
     */
    public static boolean readOneKeyExist(String nameKey) {
        try {
            // Récupère la valeur de la clé spécifiée
            String storedContent = instanceDeConnection.getConnection().get(nameKey);
            if (storedContent == null) {
                return false;
            } else {
                return true;
            }
        } finally {
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }
    }
}
