package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import org.json.JSONObject;
import org.json.XML;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class UPDATERedis {

    private static final ConnectionRedis instanceDeConnection = ConnectionRedis.getInstance();

    public static void main(String[] args){
        //updateOneKeyValue("Onvif_Metadata_C1000_2023-04-25_16-25-24.861", "value1");
        updateOneKeyJSON("Onvif_Metadata_C1000_2023-04-25_16-25-24.861","C:\\Users\\g.gonfiantini\\Desktop\\TestsDataBaseNoSQL\\dataBaseTests\\src\\main\\resources\\FichiersXML\\Onvif_Metadata_C1000_2023-04-21_16-48-59.648.xml");
    }

    /**
     * Cette fonction permet de modifier une valeur d'une clé dans Redis
     */
    public static void updateOneKeyValue(String nameKey, String newValue) {
        try {
            //Vérifie que la clé existe
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            // Modifie la valeur de la clé spécifiée
            ConnectionRedis.getInstance().getConnection().set(nameKey, newValue);
            System.out.println("La valeur de la clé a été modifiée");
        } finally {
            // Ferme la connexion à Redis
            if (ConnectionRedis.getInstance().getConnection() != null) {
                ConnectionRedis.getInstance().getConnection().close();
            }
        }
    }

    /**
     * Cette fonction permet de modifier une valeur (qui est un fichier JSON) par un autre fichier XML.
     * Conversion du fichier XML en JSON puis modification de la valeur de la clé.
     */
    public static void updateOneKeyJSON(String nameKey, String pathFileXML) {
        try {
            // Vérifie que la clé existe
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            // Convertit le fichier XML en JSON
            File file = new File(pathFileXML);
            JSONObject jsonValue = XML.toJSONObject(String.valueOf(file));

            // Modifie la valeur de la clé spécifiée avec le JSON
            instanceDeConnection.getConnection().set(nameKey, jsonValue.toString());
            System.out.println("La valeur de la clé a été modifiée");
        } finally {
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }
    }


    /**
     * Cette fonction permet de modifier toutes les valeurs des clés Redis.
     * En fonction du contenu dans le fichier JSON, si un "Human" est présent dans le fichier JSON, on le remplace par
     * la valeur en paramètre.
     */
    public static void updateAllKeyJSONWithValueHuman(String newValue) {
        try {
            // Récupère toutes les clés de la base de données Redis
            Set<String> keys = instanceDeConnection.getConnection().keys("*");

            // Parcourt chaque clé de la base de données
            for (String key : keys) {
                // Récupère la valeur de la clé en tant que JSON
                String jsonValue = instanceDeConnection.getConnection().get(key);
                JSONObject jsonObject = new JSONObject(jsonValue);

                // Vérifie si le JSON contient la clé "Human"
                if (jsonObject.has("Human")) {
                    // Remplace la valeur de "Human" par la nouvelle valeur et met à jour la clé dans Redis
                    jsonObject.put("Human", newValue);
                    instanceDeConnection.getConnection().set(key, jsonObject.toString());
                }
            }
            System.out.println("Les valeurs des clés ont été modifiées");
        } finally {
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }
    }

}
