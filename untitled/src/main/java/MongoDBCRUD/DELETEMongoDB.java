package MongoDBCRUD;

import ConnectionBD.ConnectionMongoDB;

public class DELETEMongoDB {

    private static final ConnectionMongoDB instanceDeConnection = ConnectionMongoDB.getInstance();

    public static void main(String[] args) {
        deleteCollection("testCollection");
    }

    /**
     * Cette méthode permet de supprimer une collection avec son nom.
     * @param collectionName
     */
    public static void deleteCollection(String collectionName) {
        instanceDeConnection.getDatabase().getCollection(collectionName).drop();
        System.out.println("Collection " + collectionName + " deleted successfully");
    }



}
