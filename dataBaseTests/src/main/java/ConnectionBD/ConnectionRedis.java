package ConnectionBD;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConnectionRedis {

    private static ConnectionRedis instance;
    private JedisPool jedisPool;

    private static final String HOST = "localhost";
    private static final int PORT = 6379;

    private ConnectionRedis() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMinIdle(2);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        int timeout = 10000; // Augmenter cette valeur si nécessaire
        jedisPool = new JedisPool(jedisPoolConfig, HOST, PORT, timeout);
    }


    public static ConnectionRedis getInstance() {
        if (instance == null) {
            synchronized (ConnectionRedis.class) {
                if (instance == null) {
                    instance = new ConnectionRedis();
                    //System.out.println("ConnectionRedis instance created");
                }
            }
        }
        return instance;
    }

    public Jedis getConnection() {
        return jedisPool.getResource();
    }

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

    public static Jedis getRedisConnection() {
        String host = "localhost";
        int port = 6379;

        return new Jedis(host, port);
    }

    public static void fermetConnexion() {
        if (instance != null) {
            instance.jedisPool.close();
        }
    }
}
