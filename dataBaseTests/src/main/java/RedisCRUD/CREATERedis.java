package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import org.json.JSONObject;
import org.json.XML;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static void createOneKeyValue(String nameKey, String pathFileOfXML) {
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
        // Enregistre l'heure de début
        Instant startTime = Instant.now();
        AtomicInteger compteur = new AtomicInteger();
        int batchSize = 10; // Définit la taille du lot

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(pathFolderOfXML), "*.xml")) {
            List<Path> paths = new ArrayList<>();

            for (Path path : directoryStream) {
                paths.add(path);
                if (paths.size() >= batchSize) {
                    processBatch(paths, compteur);
                    paths.clear();
                }
            }

            // Traite le dernier lot s'il reste des éléments
            if (!paths.isEmpty()) {
                processBatch(paths, compteur);
            }

            // Enregistre l'heure de fin
            Instant endTime = Instant.now();
            // Calcule la durée totale
            Duration duration = Duration.between(startTime, endTime);

            System.out.println("Nombre de fichier: " + compteur);
            System.out.println("Durée totale : " + duration.toMillis() + " secondes");
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du dossier : " + e.getMessage());
        }
    }

    private static void processBatch(List<Path> paths, AtomicInteger compteur) {
        try (Jedis jedis = instanceDeConnection.getConnection()) {
            paths.forEach(path -> {
                compteur.getAndIncrement();
                String fileName = path.getFileName().toString();
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
                String key = fileNameWithoutExtension + fileExtension;

                String value = null;
                try {
                    value = new String(Files.readAllBytes(path));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                jedis.set(key, value);
            });
        }
    }


}
