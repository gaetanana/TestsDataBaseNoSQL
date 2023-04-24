import ConnectionBD.ConnectionMongoDB;

public class maintTestConnexion {
    public static void main(String[] args) {
        connectionMongoDB();
    }

    public static void connectionMongoDB() {
        ConnectionMongoDB connectionMongoDB = ConnectionMongoDB.getInstance();
        System.out.println(connectionMongoDB.getDatabase());
    }
}
