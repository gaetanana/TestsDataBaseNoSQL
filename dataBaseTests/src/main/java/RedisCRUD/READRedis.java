package RedisCRUD;
import ConnectionBD.ConnectionRedis;
public class READRedis {

    private static final ConnectionRedis instanceDeConnection = ConnectionRedis.getInstance();

    public static void main(String[] args) {
        readOneKeyValue("Onvif_Metadata_C1000_2023-04-21_16-44-55.650.xml");
    }

    /**
     * Cette fonction permet de retrouver la valeur d'une clé dans Redis
     */
    public static void readOneKeyValue(String nameKey) {
        try {
            // Récupère la valeur de la clé spécifiée
            String storedContent = instanceDeConnection.getConnection().get(nameKey);
            System.out.println("Contenu JSON stocké dans Redis : \n" + storedContent);
        } finally {
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }
    }
}
