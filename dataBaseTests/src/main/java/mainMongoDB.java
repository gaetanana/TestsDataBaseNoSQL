import java.util.Scanner;

import MongoDBCRUD.*;

public class mainMongoDB {
    public static void main(String[] args) {
        System.out.println("Bienvenue dans le programme de test de la base de données MongoDB\n");
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
            System.out.println("1 - Créer une collection");
            System.out.println("2 - Créer un document");
            System.out.println("3 - Quitter le menu Create");

            int choixCreate = sc.nextInt();

            if(choixCreate == 1) {
                System.out.println("Vous avez choisi de créer une collection");
                System.out.println("Veuillez entrer le nom de la collection : ");
                String nomCollection = sc.next();
                CREATEMongoDB.createCollection(nomCollection);
            } else if (choixCreate == 2) {
                System.out.println("Vous avez choisi de créer un document");
                System.out.println("Veuillez entrer le nom de la collection : ");
                String nomCollection = sc.next();
                while (READMongoDB.collectionExists(nomCollection) == false) {
                    System.out.println("Veuillez entrer un nom de collection valide : ");
                    nomCollection = sc.next();
                }

                System.out.println("Veuillez entrer le chemin du fichier JSON : ");
                String cheminFichierJSON = sc.next();
                CREATEMongoDB.createOneDocument(nomCollection, cheminFichierJSON);
            } else if (choixCreate == 3) {
                System.out.println("Vous avez choisi de quitter le menu Create");

            }
            else {
                System.out.println("Veuillez entrer un nombre entre 1 et 3");
            }

        } else if (choix == 2) {

        } else if (choix == 3) {


        } else if (choix == 4) {

        } else if (choix == 5) {
            System.out.println("Vous avez choisi de quitter le programme");
            System.exit(0);

        } else {
            System.out.println("Veuillez entrer un nombre entre 1 et 5");
        }

    }
}
