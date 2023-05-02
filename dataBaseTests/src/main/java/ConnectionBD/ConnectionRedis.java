package ConnectionBD;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConnectionRedis {

    // Instance unique de la classe
    private static ConnectionRedis instance;

    // Pool de connexion Jedis
    private static JedisPool jedisPool;

    // Informations de connexion
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final int TIMEOUT = 6000;

    // Constructeur privé pour empêcher la création d'instances multiples
    private ConnectionRedis() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(256);
        poolConfig.setMaxIdle(64);
        poolConfig.setMinIdle(32);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, TIMEOUT);
    }

    // Méthode pour obtenir l'instance unique de la classe
    public static synchronized ConnectionRedis getInstance() {
        if (instance == null) {
            instance = new ConnectionRedis();
        }
        return instance;
    }

    // Méthode pour obtenir une connexion Jedis
    public Jedis getConnection() {
        return jedisPool.getResource();
    }

    // Méthode pour fermer une connexion Jedis
    public void closeConnection(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public static boolean isDockerRunning() {
        try {
            Process process = Runtime.getRuntime().exec("docker ps");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("redis")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
