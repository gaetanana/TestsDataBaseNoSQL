package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class DELETERedis {

    private static final ConnectionRedis instanceDeConnection = ConnectionRedis.getInstance();


    /**
     * Cette fonction supprime une clé dans Redis avec sa valeur
     */
    public static void deleteOneKey(String nameKey) {
        try {
            //Vérifie que la clé existe
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            // Supprime la clé spécifiée
            ConnectionRedis.getInstance().getConnection().del(nameKey);
            System.out.println("La clé a été supprimée");
        } finally {
            // Ferme la connexion à Redis
            if (ConnectionRedis.getInstance().getConnection() != null) {
                ConnectionRedis.getInstance().getConnection().close();
            }
        }
    }

    /**
     * Cette fonction supprime toutes les clés dans Redis avec leur valeur
     */

    public static void deleteAllKey() {
        try {
            // Supprime toutes les clés
            ConnectionRedis.getInstance().getConnection().flushAll();
            System.out.println("Toutes les clés ont été supprimées");
        } finally {
            // Ferme la connexion à Redis
            if (ConnectionRedis.getInstance().getConnection() != null) {
                ConnectionRedis.getInstance().getConnection().close();
            }
        }
    }

    /**
     * Cette fonction permet de supprimer les 50 dernières clés ajoutées dans Redis
     */
    public static void delete50LastKey() {

        int numberOfKeysToDelete = 50;

        try (Jedis jedis = ConnectionRedis.getInstance().getConnection()) {
            // Récupère les 50 dernières clés
            ScanParams scanParams = new ScanParams().count(numberOfKeysToDelete);
            ScanResult<String> scanResult = jedis.scan("0", scanParams);

            // Supprime les clés récupérées
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
