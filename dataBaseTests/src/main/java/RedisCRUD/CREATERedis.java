package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import org.json.JSONObject;
import org.json.XML;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CREATERedis {

    /**
     * Cette fonction permet de créer une clé dans Redis et de lui associer un fichier XML qui
     * sera stocké sous forme JSON.
     *
     * @param nameKey
     * @param pathFileOfXML
     */
    public static void createOneKeyValue(String nameKey, String pathFileOfXML) {
        try {
            ConnectionRedis connectionRedis = new ConnectionRedis();
            // Lis le contenu du fichier XML et le convertis en chaîne de caractères
            String fileContent = new String(Files.readAllBytes(Paths.get(pathFileOfXML)));
            // Convertis le contenu XML en objet JSON
            JSONObject jsonObject = XML.toJSONObject(fileContent);

            // Insère le contenu JSON dans Redis sous la clé spécifiée
            try (Jedis jedis = connectionRedis.getConnection()) {
                jedis.set(nameKey, jsonObject.toString());

                // Vérifie que le contenu a bien été inséré
                String storedContent = jedis.get(nameKey);
                System.out.println("Contenu JSON stocké dans Redis : \n" + storedContent);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier XML : " + e.getMessage());
        }
    }

    /**
     * Cette fonction permet de prendre tous les fichier XML d'un dossier et les concerties en JSON puis
     * les insère dans Redis avec comme clé le nom du fichier.
     *
     * @param pathFolderOfXML
     */
    public static void createAllKeyValue(String pathFolderOfXML) {
        Instant startTime = Instant.now();
        int compteurFichier = 0;

        File folder = new File(pathFolderOfXML);
        File[] listOfFiles = folder.listFiles();

        // Crée un ThreadPool avec un nombre fixe de threads
        int numberOfThreads = 10; // Ajustez cette valeur en fonction de vos besoins
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        ConnectionRedis connectionRedis = new ConnectionRedis();

        System.out.println("Traitement des fichiers XML en cours...");
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".xml")) {
                compteurFichier++;
                executorService.submit(() -> {
                    try (Jedis jedis = connectionRedis.getConnection()) {
                        String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                        JSONObject jsonObject = XML.toJSONObject(content);
                        String key = file.getName().substring(0, file.getName().length() - 4);
                        jedis.set(key, jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        // Ferme le ThreadPool et attend la fin de toutes les tâches
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Durée d'exécution : " + duration.toMillis() + " ms");
        System.out.println("Nombre de fichier traités : " + compteurFichier);
    }
}