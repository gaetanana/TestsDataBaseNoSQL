import java.util.Scanner;

import MongoDBCRUD.*;

public class mainMongoDB {
    public static void main(String[] args) {

        while (true) {
            System.out.println("\n========================================================");
            System.out.println("== Programme de test de la base de données MongoDB    ==");
            System.out.println("========================================================\n");
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
                System.out.println("3 - Créer tous les documents d'un dossier");
                System.out.println("4 - Quitter le menu Create");
                int choixCreate = sc.nextInt();
                //CREATE
                if (choixCreate == 1) {
                    System.out.println("Vous avez choisi de créer une collection");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == true) {
                        System.out.println("La collection existe déjà, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    CREATEMongoDB.createCollection(nomCollection);
                } else if (choixCreate == 2) {
                    System.out.println("Vous avez choisi de créer un document");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    System.out.println("Veuillez entrer le chemin absolue du fichier JSON : ");
                    String cheminFichierJSON = sc.next();
                    CREATEMongoDB.createOneDocument(nomCollection, cheminFichierJSON);
                } else if (choixCreate == 3) {
                    System.out.println("Vous avez choisi de créer tous les documents d'un dossier");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    System.out.println("Veuillez entrer le chemin absolue du dossier : ");
                    String cheminDossier = sc.next();
                    CREATEMongoDB.creationPlusieursDocuments(nomCollection, cheminDossier);
                } else if (choixCreate == 4) {
                    System.out.println("Vous avez choisi de quitter le menu Create");
                    continue;
                } else {
                    System.out.println("Veuillez entrer un nombre entre 1 et 3");
                }
                //READ
            } else if (choix == 2) {
                System.out.println("Vous êtes dans le menu Read");
                System.out.println("Veuillez choisir une option :\n");
                System.out.println("1 - Lire une collection");
                System.out.println("2 - Lire les documents qui possèdent un Type Human");
                System.out.println("3 - Lire un documents qui possèdent un Type Human avec une probabilité supérieur à 0.5");
                System.out.println("4 - Quitter le menu Read");

                int choixRead = sc.nextInt();

                if (choixRead == 1) {
                    System.out.println("Vous avez choisi de lire une collection");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    READMongoDB.readCollection(nomCollection);
                } else if (choixRead == 2) {
                    System.out.println("Vous avez choisi de lire les documents qui possèdent un Type Human");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    READMongoDB.getHuman(nomCollection);
                } else if (choixRead == 3) {
                    System.out.println("Vous avez choisi de lire un documents qui possèdent un Type Human avec une probabilité supérieur à 0.5");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    READMongoDB.getHumanWithProbability(nomCollection);
                } else if (choixRead == 4) {
                    System.out.println("Vous avez choisi de quitter le menu Read");
                    continue;
                } else {
                    System.out.println("Veuillez entrer un nombre entre 1 et 4");
                }


            } else if (choix == 3) {
                System.out.println("Vous êtes dans le menu Update");
                System.out.println("Pas encore disponible");

            } else if (choix == 4) {
                System.out.println("Vous êtes dans le menu Delete");
                System.out.println("Veuillez choisir une option :\n");
                System.out.println("1 - Supprimer une collection");
                System.out.println("2 - Supprimer un document d'une collection");
                System.out.println("3 - Supprimer tous les documents d'une collection");
                System.out.println("4 - Quitter le menu Delete");
                int choixDelete = sc.nextInt();

                if (choixDelete == 1) {
                    System.out.println("Vous avez choisi de supprimer une collection");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    DELETEMongoDB.deleteCollection(nomCollection);
                } else if (choixDelete == 2) {
                    System.out.println("Vous avez choisi de supprimer un document d'une collection");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    System.out.println("Veuillez entrer l'id du document : ");
                    String idDocument = sc.next();
                    DELETEMongoDB.deleteDocument(nomCollection, idDocument);
                } else if (choixDelete == 3) {
                    System.out.println("Vous avez choisi de supprimer tous les documents d'une collection");
                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    DELETEMongoDB.deleteAllDocumentsInOneCollection(nomCollection);
                } else if (choixDelete == 4) {
                    System.out.println("Vous avez choisi de quitter le menu Delete");
                    continue;
                }

            } else if (choix == 5) {
                System.out.println("Vous avez choisi de quitter le programme");
                System.exit(0);

            } else {
                System.out.println("Veuillez entrer un nombre entre 1 et 5");
            }
        }


    }
}
