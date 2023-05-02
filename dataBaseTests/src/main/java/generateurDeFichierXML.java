import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class generateurDeFichierXML {

    public static void main(String[] args) {
        String sourceFilePath = "C:\\Users\\g.gonfiantini\\Desktop\\data\\FichiersXML\\FichiersXML104295\\Onvif_Metadata_C1000_2023-04-27_16-31-18.695.xml"; // Modifier avec le chemin absolu du fichier XML source
        String destinationFolderPath = "C:\\Users\\g.gonfiantini\\Desktop\\data\\FichiersXML\\FichierXMLLourd\\"; // Modifier avec le chemin absolu du dossier de destination
        int numberOfCopies = 10000; // Modifier avec le nombre de copies souhaité

        File sourceFile = new File(sourceFilePath);
        File destinationFolder = new File(destinationFolderPath);

        if (!sourceFile.isFile()) {
            System.out.println("Invalid source file path. Please provide a valid file path.");
            return;
        }

        if (!destinationFolder.isDirectory()) {
            System.out.println("Invalid destination folder path. Please provide a valid folder path.");
            return;
        }

        for (int i = 1; i <= numberOfCopies; i++) {
            File destinationFile = new File(destinationFolder, "copy_" + i + "_" + sourceFile.getName());
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
