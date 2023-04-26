package RedisCRUD;

import ConnectionBD.ConnectionRedis;

public class UPDATERedis {

    private static final ConnectionRedis instanceDeConnection = ConnectionRedis.getInstance();

    public static void main(String[] args){
        updateOneKeyValue("key1", "value1");
    }

    /**
     * Cette fonction permet de modifier une valeur d'une clé dans Redis
     */
    public static void updateOneKeyValue(String nameKey, String newValue) {
        try {
            //Vérifie que la clé existe
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            // Modifie la valeur de la clé spécifiée
            ConnectionRedis.getInstance().getConnection().set(nameKey, newValue);
            System.out.println("La valeur de la clé a été modifiée");
        } finally {
            // Ferme la connexion à Redis
            if (ConnectionRedis.getInstance().getConnection() != null) {
                ConnectionRedis.getInstance().getConnection().close();
            }
        }
    }
    /**
     * Cette fonction permet de modifier une valeur (qui est un fichier JSON) par un autre fichier JSON
     * grâce à la clé
     */
    public static void updateOneKeyJSON(String nameKey, String newValue) {
        try {
            //Vérifie que la clé existe
            if (!READRedis.readOneKeyExist(nameKey)) {
                System.out.println("La clé n'existe pas");
                return;
            }
            // Modifie la valeur de la clé spécifiée
            ConnectionRedis.getInstance().getConnection().set(nameKey, newValue);
            System.out.println("La valeur de la clé a été modifiée");
        } finally {
            // Ferme la connexion à Redis
            if (ConnectionRedis.getInstance().getConnection() != null) {
                ConnectionRedis.getInstance().getConnection().close();
            }
        }
    }
    /**
     * Cette fonction permet de modifier toutes les valeurs des clé Redis
     * En fonction du contenue dans le fichier JSON si il y a un Human dans le fichier JSON on le remplace par
     * la valeur en paramètre
     */
    public static void updateAllKeyJSON(String newValue) {
        try {
            // Modifie la valeur de la clé spécifiée
            ConnectionRedis.getInstance().getConnection().set("Human", newValue);
            System.out.println("La valeur de la clé a été modifiée");
        } finally {
            // Ferme la connexion à Redis
            if (ConnectionRedis.getInstance().getConnection() != null) {
                ConnectionRedis.getInstance().getConnection().close();
            }
        }
    }

}
