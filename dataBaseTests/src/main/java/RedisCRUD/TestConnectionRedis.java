package RedisCRUD;
import ConnectionBD.ConnectionRedis;
public class TestConnectionRedis {

    public static void main(String[] args) {
        ConnectionRedis connectionRedis = ConnectionRedis.getInstance();
        connectionRedis.getConnection();
    }
}
