import RedisCRUD.CREATERedis;
import RedisCRUD.DELETERedis;
import RedisCRUD.READRedis;

import java.util.Scanner;

public class mainRedis {

    public static void main(String[] args) {
        while (true) {
            System.out.println("Bienvenue dans le programme de test de la base de données Redis\n");
            System.out.println("Veuillez choisir une option :\n");

            System.out.println("1 - Create");
            System.out.println("2 - Read");
            System.out.println("3 - Update");
            System.out.println("4 - Delete");
            System.out.println("5 - Quitter le programme");
            System.out.println("\nVotre choix : ");
            Scanner sc = new Scanner(System.in);
            int choix = sc.nextInt();
            if (choix == 1) {
                System.out.println("Vous êtes dans le menu Create");
                System.out.println("Veuillez choisir une option :\n");
                System.out.println("1 - Créer une clé avec un fichier XML en valeur");
                System.out.println("2 - Stocker l'ensemble des fichiers XML d'un dossier dans Redis");
                System.out.println("3 - Quitter le menu Create");
                System.out.println("\nVotre choix : ");
                int choixCreate = sc.nextInt();
                if (choixCreate == 1) {
                    System.out.println("Vous avez choisi de créer une clé avec un fichier XML en valeur");
                    System.out.println("Veuillez entrer le nom de la clé : ");
                    String nomCle = sc.next();
                    while (READRedis.readOneKeyExist(nomCle) == true) {
                        System.out.println("La clé existe déjà, veuillez entrer un nom de clé valide : ");
                        nomCle = sc.next();
                    }
                    System.out.println("Veuillez entrer le chemin absolu du fichier XML : ");
                    String cheminFichierXML = sc.next();
                    CREATERedis.createOneKeyValue(nomCle, cheminFichierXML);
                } else if (choixCreate == 2) {
                    System.out.println("Vous avez choisi de stocker l'ensemble des fichiers XML d'un dossier dans Redis");
                    System.out.println("Veuillez entrer le chemin absolu du dossier : ");
                    String cheminDossier = sc.next();
                    CREATERedis.createAllKeyValue(cheminDossier);
                } else if (choixCreate == 3) {
                    System.out.println("Vous avez choisi de quitter le menu Create");
                    continue;
                }

            } else if (choix == 2) {
                System.out.println("Vous êtes dans le menu Read");
                System.out.println("Veuillez choisir une option :\n");
                System.out.println("1 - Lire la valeur d'une clé");
                System.out.println("2 - Lire toutes les clés présentes dans la base de données");
                System.out.println("3 - Lire toutes les clés qui a dans les valeurs un humain");
                System.out.println("4 - Lire toutes les clés qui a dans les valeurs un humain avec une probabilité de survie supérieure à 0.5");
                System.out.println("5 - Quitter le menu Read");

                System.out.println("\nVotre choix : ");

                int choixRead = sc.nextInt();
                if (choixRead == 1) {
                    System.out.println("Vous avez choisi de lire la valeur d'une clé");
                    System.out.println("Veuillez entrer le nom de la clé : ");
                    String nomCle = sc.next();
                    while (READRedis.readOneKeyExist(nomCle) == false) {
                        System.out.println("La clé n'existe pas, veuillez entrer un nom de clé valide : ");
                        nomCle = sc.next();
                    }
                    READRedis.readOneKeyValue(nomCle);
                } else if (choixRead == 2) {
                    System.out.println("Vous avez choisi de lire toutes les clés présentes dans la base de données");
                    READRedis.readAllKey();
                } else if (choixRead == 3) {
                    System.out.println("Vous avez choisi de lire toutes les clés qui a dans les valeurs un humain");
                    READRedis.readAllKeyWithHuman();
                } else if (choixRead == 4) {
                    System.out.println("Vous avez choisi de lire toutes les clés qui a dans les valeurs un humain avec une probabilité de survie supérieure à 0.5");
                    READRedis.getHumanWithProbability();
                } else if (choixRead == 5) {
                    System.out.println("Vous avez choisi de quitter le menu Read");
                    continue;
                }

            } else if (choix == 3) {
                System.out.println("Vous êtes dans le menu Update");
                System.out.println("Veuillez choisir une option :\n");

            } else if (choix == 4) {
                System.out.println("Vous êtes dans le menu Delete");
                System.out.println("Veuillez choisir une option :\n");
                System.out.println("1 - Supprimer une clé");
                System.out.println("2 - Supprimer toutes les clés");
                System.out.println("3 - Supprimer les 50 dernières clés");
                System.out.println("4 - Quitter le menu Delete");

                System.out.println("\nVotre choix : ");
                int choixDelete = sc.nextInt();
                if (choixDelete == 1) {
                    System.out.println("Vous avez choisi de supprimer une clé");
                    System.out.println("Veuillez entrer le nom de la clé : ");
                    String nomCle = sc.next();
                    while (READRedis.readOneKeyExist(nomCle) == false) {
                        System.out.println("La clé n'existe pas, veuillez entrer un nom de clé valide : ");
                        nomCle = sc.next();
                    }
                    DELETERedis.deleteOneKey(nomCle);
                } else if (choixDelete == 2) {
                    System.out.println("Vous avez choisi de supprimer toutes les clés");
                    DELETERedis.deleteAllKey();

                } else if (choixDelete == 3) {
                    System.out.println("Vous avez choisi de supprimer les 50 dernières clés");
                    DELETERedis.delete50LastKey();
                } else if (choixDelete == 4) {
                    System.out.println("Vous avez choisi de quitter le menu Delete");
                    continue;
                }

            } else if (choix == 5) {
                System.out.println("Vous avez choisi de quitter le programme");
                System.exit(0);
            }

        }
    }

}
