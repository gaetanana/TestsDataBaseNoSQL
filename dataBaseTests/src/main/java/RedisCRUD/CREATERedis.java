package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CREATERedis {

    private static final ConnectionRedis instanceDeConnection = ConnectionRedis.getInstance();

    public static void main(String[] args) {
        //createOnKeyValue("key1", "C:\\Users\\g.gonfiantini\\Desktop\\TestsDataBaseNoSQL\\dataBaseTests\\src\\main\\resources\\FichiersXML\\Onvif_Metadata_C1000_2023-04-21_16-48-59.648.xml");
        createAllKeyValue("C:\\Users\\g.gonfiantini\\Desktop\\TestsDataBaseNoSQL\\dataBaseTests\\src\\main\\resources\\FichiersXML\\");

    }


    /**
     * Cette fonction permet de créer une clé dans Redis et de lui associer un fichier XML qui
     * sera stocké sous forme JSON.
     *
     * @param nameKey
     * @param pathFileOfXML
     */
    public static void createOnKeyValue(String nameKey, String pathFileOfXML) {
        try {
            // Lis le contenu du fichier XML et le convertis en chaîne de caractères
            String fileContent = new String(Files.readAllBytes(Paths.get(pathFileOfXML)));
            // Convertis le contenu XML en objet JSON
            JSONObject jsonObject = XML.toJSONObject(fileContent);

            // Insère le contenu JSON dans Redis sous la clé spécifiée
            instanceDeConnection.getConnection().set(nameKey, jsonObject.toString());

            // Vérifie que le contenu a bien été inséré
            String storedContent = instanceDeConnection.getConnection().get(nameKey);
            System.out.println("Contenu JSON stocké dans Redis : \n" + storedContent);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier XML : " + e.getMessage());
        } finally {
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }
    }

    /**
     * Cette fonction permet de prendre tous les fichiers XML dans un dossier il les convertit en JSON et les insère avec une clé
     * qui est le nom du fichier XML.
     */
    public static void createAllKeyValue(String pathFolderOfXML) {
        try {
            // Lis le contenu du dossier
            Files.list(Paths.get(pathFolderOfXML))
                    // Filtre les fichiers XML
                    .filter(path -> path.toString().endsWith(".xml"))
                    // Pour chaque fichier XML, on crée une clé/valeur dans Redis
                    .forEach(path -> {
                        // Récupère le nom du fichier XML
                        String fileName = path.getFileName().toString();
                        // Récupère l'extension du fichier XML
                        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                        // Récupère le nom du fichier sans l'extension
                        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
                        // Crée la clé
                        String key = fileNameWithoutExtension + fileExtension;
                        // Crée la valeur
                        String value = null;
                        try {
                            value = new String(Files.readAllBytes(path));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // Insère la clé/valeur dans Redis
                        instanceDeConnection.getConnection().set(key, value);
                    });
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du dossier : " + e.getMessage());
        } finally {
            // Ferme la connexion à Redis
            if (instanceDeConnection.getConnection() != null) {
                instanceDeConnection.getConnection().close();
            }
        }

    }

}