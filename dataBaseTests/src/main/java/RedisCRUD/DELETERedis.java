package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class DELETERedis {

    public static void deleteOneKey(String nameKey) {
        try (Jedis jedis = ConnectionRedis.getConnection()) {
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            jedis.del(nameKey);
            System.out.println("La clé a été supprimée");
        }
    }

    public static void deleteAllKey() {
        long start = System.currentTimeMillis();
        int cleSupprimee = 0;

        try (Jedis jedis = ConnectionRedis.getConnection()) {
            long initialSize = jedis.dbSize();
            jedis.flushAll();
            System.out.println("Toutes les clés ont été supprimées");
            long finalSize = jedis.dbSize();
            cleSupprimee = (int) (initialSize - finalSize);
        }

        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Temps de suppression : " + time + " ms");
        System.out.println("Nombre de clés supprimées : " + cleSupprimee);
    }

    public static void delete50LastKey() {
        int numberOfKeysToDelete = 50;

        try (Jedis jedis = ConnectionRedis.getConnection()) {
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
