package RedisCRUD;

import ConnectionBD.ConnectionRedis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
     * Cette fonction permet de prendre tous les fichier XML d'un dossier et les concerties en JSON puis
     * les insère dans Redis avec comme clé le nom du fichier.
     *
     * @param pathFolderOfXML
     */
    public static void createAllKeyValue(String pathFolderOfXML) {

        // Enregistre l'heure de début
        Instant startTime = Instant.now();

        int compteurFichier = 0;

        File folder = new File(pathFolderOfXML);
        File[] listOfFiles = folder.listFiles();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Jedis jedis = new Jedis();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".xml")) {
                compteurFichier++;
                try {
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.parse(file);
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                    JSONObject jsonObject = XML.toJSONObject(content);
                    String key = file.getName().substring(0, file.getName().length() - 4);

                    jedis.set(key, jsonObject.toString());
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Enregistre l'heure de fin
        Instant endTime = Instant.now();
        // Calcule la durée d'exécution
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Durée d'exécution : " + duration.toMillis() + " ms");
        System.out.println("Nombre de fichier traité : " + compteurFichier);

    }


}
