package ConnectionBD;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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
        jedisPool = new JedisPool(jedisPoolConfig, HOST, PORT);
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
}
