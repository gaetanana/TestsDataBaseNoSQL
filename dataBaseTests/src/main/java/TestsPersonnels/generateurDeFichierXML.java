package TestsPersonnels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class generateurDeFichierXML {

    public static void main(String[] args) {

        //Chemin absolu du fichier XML source chez moi
        //C:\Users\Gaetan\OneDrive - umontpellier.fr\Stage-projets\data\Onvif_Metadata_C1000_2023-04-27_11-49-56.464.xml
        //Chemin absolu du dossier de destination chez moi
        //C:\Users\Gaetan\OneDrive - umontpellier.fr\Stage-projets\data\FichierXMLLourd

        //Chemin absolu du fichier XML source chez Actia
        //
        //Chemin absolu du dossier de destination chez Actia
        //C:\Users\g.gonfiantini\Desktop\data\FichiersXML\FichierXML400000

        String sourceFilePath = "C:\\Users\\g.gonfiantini\\Desktop\\data\\FichiersXML\\Onvif_Metadata_C1000_2023-04-21_16-49-19.073.xml"; // Modifier avec le chemin absolu du fichier XML source
        String destinationFolderPath = "C:\\Users\\g.gonfiantini\\Desktop\\data\\FichiersXML\\FichiersXML200000"; // Modifier avec le chemin absolu du dossier de destination
        int numberOfCopies = 200000; // Modifier avec le nombre de copies souhaité

        File sourceFile = new File(sourceFilePath);
        File destinationFolder = new File(destinationFolderPath);

        if (!sourceFile.isFile()) {
            System.out.println("Chemin du fichier XML source invalide. Veuillez fournir un chemin valide.");
            return;
        }

        if (!destinationFolder.isDirectory()) {
            System.out.println("Chemin du dossier de destination invalide. Veuillez fournir un chemin valide.");
            return;
        }

        int compteur = 2000000;
        for (int i = 1; i <= numberOfCopies; i++) {
            int tampon = compteur + i;
            File destinationFile = new File(destinationFolder, "copy_" + tampon + "_" + sourceFile.getName());
            try {
                copyFileUsingFileChannels(sourceFile, destinationFile);
                System.out.println("File " + destinationFile.getAbsolutePath() + " created successfully.");
            } catch (IOException e) {
                System.out.println("Failed to create file " + destinationFile.getAbsolutePath() + ": " + e.getMessage());
            }
        }
    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
}
