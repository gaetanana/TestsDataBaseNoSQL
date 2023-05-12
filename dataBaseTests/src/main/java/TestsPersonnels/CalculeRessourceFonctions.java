package TestsPersonnels;

import ch.qos.logback.core.util.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;

public class CalculeRessourceFonctions {


    /**
     * Cette fonction me permet de calculer l'utilisation du processeur lors de l'exécution d'une opération CRUD
     */
    public static void exempleUtilisationProcesseur() {
        // Avant l'opération CRUD
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long cpuBefore = threadBean.getCurrentThreadCpuTime();
        Instant startTime = Instant.now();


        // Exécution de l'opération CRUD
        // ...

        // Après l'opération CRUD
        long cpuAfter = threadBean.getCurrentThreadCpuTime();
        long cpuUsed = cpuAfter - cpuBefore;
        Instant endTime = Instant.now();

        // Calcul du pourcentage d'utilisation du processeur
        Duration duration = Duration.between(startTime, endTime);
        double cpuPercentage = (double) cpuUsed / (duration.toMillis() * 1000000) * 100;
        System.out.println("Utilisation moyenne du processeur : " + cpuPercentage + "%");
    }

    /**
     * Cette fonction me permet de calculer l'utilisation de la mémoire vive lors de l'exécution d'une opération CRUD
     */
    public static void exempleUtilisationMemoire() {
        // Avant l'opération CRUD
        long beforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Exécution de l'opération CRUD
        // ...

        // Après l'opération CRUD
        long afterUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = afterUsedMemory - beforeUsedMemory;

        // Calcul du pourcentage d'utilisation de la mémoire
        double memoryPercentage = (double) memoryUsed / Runtime.getRuntime().maxMemory() * 100;
        System.out.println("Mémoire utilisée : " + memoryUsed + " octets");
        System.out.println("Pourcentage de la mémoire utilisée : " + memoryPercentage + "%");
    }

    /**
     * Cette fonction me permet de calculer le pourcentage du disque dur utilisé lors de l'exécution d'une opération CRUD
     */
    public static void exempleUtilisationDisquePhysique(){
        oshi.SystemInfo systemInfo = new oshi.SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        OSProcess currentProcess = os.getProcess(os.getProcessId());

        // Avant l'exécution de la fonction
        long bytesReadBefore = currentProcess.getBytesRead();
        long bytesWrittenBefore = currentProcess.getBytesWritten();
        long startTime = System.nanoTime();

        // Exécutez votre fonction ici
        // ...

        // Après l'exécution de la fonction
        currentProcess = os.getProcess(os.getProcessId()); // Mettre à jour les informations du processus
        long bytesReadAfter = currentProcess.getBytesRead();
        long bytesWrittenAfter = currentProcess.getBytesWritten();
        long endTime = System.nanoTime();

        // Calculez la différence d'utilisation du disque
        long bytesReadDifference = bytesReadAfter - bytesReadBefore;
        long bytesWrittenDifference = bytesWrittenAfter - bytesWrittenBefore;
        long elapsedTime = endTime - startTime;

        double bytesReadPerSecond = (double) bytesReadDifference / (elapsedTime / 1_000_000_000.0);
        double bytesWrittenPerSecond = (double) bytesWrittenDifference / (elapsedTime / 1_000_000_000.0);

        System.out.println("Taux de lecture : " + bytesReadPerSecond + " octets/s");
        System.out.println("Taux d'écriture : " + bytesWrittenPerSecond + " octets/s");
    }

}
