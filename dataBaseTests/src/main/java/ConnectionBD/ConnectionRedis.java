package ConnectionBD;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConnectionRedis {

    private static JedisPool jedisPool;

    private static final String HOST = "localhost";
    private static final int PORT = 6379;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMinIdle(2);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        int timeout = 60000;
        jedisPool = new JedisPool(jedisPoolConfig, HOST, PORT, timeout);
    }

    public static Jedis getConnection() {
        return jedisPool.getResource();
    }

    public static void closeConnection(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public static void closeJedisPool() {
        if (jedisPool != null) {
            jedisPool.close();
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
