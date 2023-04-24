package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;


public class CREATEMongoDB {

    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        createCollection("testCollection");
    }

    /**
     * Cette méthode permet de créer une collection dans la base de données
     * @param collectionName
     */
    public static void createCollection(String collectionName) {
        try {
            instanceDeConnection.getDatabase().createCollection(collectionName);
            System.out.println("Collection " + collectionName + " created successfully");
        } catch (Exception e) {
            System.err.println("Error creating collection: " + e.getMessage());
        }
    }


}
