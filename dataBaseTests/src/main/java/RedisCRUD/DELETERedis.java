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
            ConnectionRedis.getRedisConnection().del(nameKey);
            System.out.println("La clé a été supprimée");
        } finally {
            ConnectionRedis.getRedisConnection();
            ConnectionRedis.getRedisConnection().close();

                    }
    }

    /**
     * Cette fonction supprime toutes les clés dans Redis avec leur valeur
     */

    public static void deleteAllKey() {
        // Temps de début de la suppression
        long start = System.currentTimeMillis();
        int cleSupprimee = 0;

        try (Jedis jedis = ConnectionRedis.getInstance().getConnection()) {
            // Obtient la taille de la base de données avant la suppression
            long initialSize = jedis.dbSize();

            // Supprime toutes les clés
            jedis.flushAll();
            System.out.println("Toutes les clés ont été supprimées");

            // Obtient la taille de la base de données après la suppression
            long finalSize = jedis.dbSize();

            // Calcule le nombre de clés supprimées
            cleSupprimee = (int) (initialSize - finalSize);
        }

        // Temps de fin de la suppression
        long end = System.currentTimeMillis();
        // Temps total de la suppression
        long time = end - start;
        System.out.println("Temps de suppression : " + time + " ms");
        System.out.println("Nombre de clés supprimées : " + cleSupprimee);
    }

    /**
     * Cette fonction permet de supprimer les 50 dernières clés ajoutées dans Redis
     */
    public static void delete50LastKey() {

        int numberOfKeysToDelete = 50;

        try (Jedis jedis = ConnectionRedis.getRedisConnection()) {
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
        ConnectionRedis.getRedisConnection();
        ConnectionRedis.getRedisConnection().close();
    }

}
