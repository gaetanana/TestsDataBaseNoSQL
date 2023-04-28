import java.io.IOException;
import java.util.Scanner;

import ConnectionBD.ConnectionMongoDB;
import ConnectionBD.ConnectionRedis;
import MongoDBCRUD.*;

public class mainMongoDB {
    public static void main(String[] args) throws IOException {
        System.out.println("=========================================================");
        System.out.println("|| Tentative de connexion à la base de données MongoDB ||");
        System.out.println("=========================================================\n");
        //Vérifie si la connexion à la base de données est réussie
        if (ConnectionMongoDB.getInstance().getDatabase() != null && ConnectionMongoDB.isDockerRunning()) {
            System.out.println("Connexion à la base de données réussie");
        } else {
            System.err.println("Erreur lors de la connexion à la base de données, veuillez vérifier les paramètres de connexion ou que docker est bien lancé");
            return;
        }

        while (true) {
            System.out.println("\n========================================================");
            System.out.println("|| Programme de test de la base de données MongoDB    ||");
            System.out.println("========================================================\n");

            System.out.println("==========================================================");
            System.out.println("||Dans ce programme vous pouvez effectuer les opérations||");
            System.out.println("||CRUD sur une base de données MongoDB.                 ||");
            System.out.println("==========================================================\n");
            System.out.println("============== Veuillez choisir une option : ===========");
            System.out.println("||                                                    ||");
            System.out.println("|| 1 - Create                                         ||");
            System.out.println("|| 2 - Read                                           ||");
            System.out.println("|| 3 - Update                                         ||");
            System.out.println("|| 4 - Delete                                         ||");
            System.out.println("|| 5 - Quitter le programme                           ||");
            System.out.println("||                                                    ||");
            System.out.println("========================================================");
            Scanner sc = new Scanner(System.in);
            boolean validInput = false;
            int choix = 0;
            System.out.println();
            do {
                System.out.println("Votre choix : ");
                try {
                    choix = sc.nextInt();
                    validInput = true;
                } catch (Exception e) {
                    System.out.println("Veuillez entrer un choix valide : ");
                    sc.next();
                }
            }while (!validInput);

            //Vérifie que l'utilisateur ne rentre pas une chaines de caractères

            if (choix == 1) {
                System.out.println("\n=======================================================================");
                System.out.println("||                          Menu Create                              ||");
                System.out.println("=======================================================================");
                System.out.println("=================== Veuillez choisir une option : =====================");
                System.out.println("||                                                                   ||");
                System.out.println("|| 1 - Créer une collection                                          ||");
                System.out.println("|| 2 - Créer un document (Un fichier XML).                           ||");
                System.out.println("|| 3 - Créer plusieurs documents (Tous les fichiers XML d'un dossier)||");
                System.out.println("|| 4 - Quitter le menu Create                                        ||");
                System.out.println("||                                                                   ||");
                System.out.println("========================================================================");
                boolean validInputCreate = false;
                int choixCreate = 0;
                do {
                    System.out.println("Votre choix : ");
                    try {
                        choixCreate = sc.nextInt();
                        validInputCreate = true;
                    } catch (Exception e) {
                        System.out.println("Veuillez entrer un choix valide : ");
                        sc.next();
                    }
                }while (!validInputCreate);

                //CREATE
                if (choixCreate == 1) {
                    System.out.println("\n========================================================================");
                    System.out.println("||       Vous avez choisi de créer une collection                     ||");
                    System.out.println("========================================================================\n");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();

                    while (READMongoDB.collectionExists(nomCollection) == true) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection existe déjà, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();

                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    CREATEMongoDB.createCollection(nomCollection);
                } else if (choixCreate == 2) {
                    System.out.println("\n==========================================================================");
                    System.out.println("||       Vous avez choisi de créer un document                         ||");
                    System.out.println("==========================================================================\n");

                    System.out.println("Veuillez entrer le nom de la collection dans laquelle pour voulez créer le document ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();

                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide ou tapez ECHAP pour revenir au menu principal : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer le chemin absolue du fichier JSON ou tapez ECHAP pour revenir au menu principal : ");
                    String cheminFichierJSON = sc.next();
                    if (cheminFichierJSON.equals("ECHAP")) {
                        continue;
                    }
                    CREATEMongoDB.createOneDocument(nomCollection, cheminFichierJSON);
                } else if (choixCreate == 3) {
                    System.out.println("\n==========================================================================");
                    System.out.println("||       Vous avez choisi de créer plusieurs documents                  ||");
                    System.out.println("==========================================================================\n");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal  : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide ou tapez ECHAP pour revenir au menu principal : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer le chemin absolue du dossier ou tapez ECHAP pour revenir au menu principal : ");
                    String cheminDossier = sc.next();
                    if (cheminDossier.equals("ECHAP")) {
                        continue;
                    }
                    CREATEMongoDB.creationPlusieursDocuments(nomCollection, cheminDossier);
                } else if (choixCreate == 4) {
                    System.out.println("==============================================================================");
                    System.out.println("||           Vous avez choisi de quitter le menu Create                     ||");
                    System.out.println("==============================================================================");

                    continue;
                } else {
                    System.out.println("Veuillez entrer un nombre entre 1 et 3");
                }
                //READ
            } else if (choix == 2) {
                System.out.println("==============================================================================");
                System.out.println("||                               Menu Read                                  ||");
                System.out.println("==============================================================================");
                System.out.println("===================== Veuillez choisir une option : ==========================");
                System.out.println("||                                                                          ||");
                System.out.println("|| 1 - Lire tous les documents d'une collection                             ||");
                System.out.println("|| 2 - Lire tous les documents d'une collection qui possèdent un Type Human ||");
                System.out.println("|| 3 - Lire un documents qui possèdent un Type Human                        ||");
                System.out.println("||    avec une probabilité supérieur à 0.5                                  ||");
                System.out.println("|| 4 - Quitter le menu Read                                                 ||");
                System.out.println("||                                                                          ||");
                System.out.println("===============================================================================");
                boolean validInputRead = false;
                int choixRead = 0;
                do {
                    System.out.println("Votre choix : ");
                    try {
                        choixRead = sc.nextInt();
                        validInputRead = true;
                    } catch (Exception e) {
                        System.out.println("Veuillez entrer un choix valide : ");
                        sc.next();
                    }
                }while (!validInputRead);

                if (choixRead == 1) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de lire tous les documents       ||");
                    System.out.println("=============================================================");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    READMongoDB.readCollection(nomCollection);

                } else if (choixRead == 2) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de lire tous les documents       ||");
                    System.out.println("||       d'une collection qui possèdent un Type Human      ||");
                    System.out.println("=============================================================");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    READMongoDB.getHuman(nomCollection);
                } else if (choixRead == 3) {
                    System.out.println("============================================================");
                    System.out.println("||    Vous avez choisi de lire un document qui possèdent  ||");
                    System.out.println("||    un Type Human avec une probabilitésupérieur à 0.5   ||");
                    System.out.println("============================================================");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    READMongoDB.getHumanWithProbability(nomCollection);
                } else if (choixRead == 4) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de quitter le menu Read          ||");
                    System.out.println("=============================================================");

                    continue;
                } else {
                    System.out.println("Veuillez entrer un nombre entre 1 et 4");
                }


            } else if (choix == 3) {
                System.out.println("\n=====================================================================");
                System.out.println("==                        Menu Update                              ==");
                System.out.println("=====================================================================\n");
                System.out.println("================ Veuillez choisir une option : =====================");
                System.out.println("||                                                                ||");
                System.out.println("|| 1 - Modifier un document qui a la valeur 'Human'               ||");
                System.out.println("|| 2 - Modifier tous les documents qui possèdent la valeur 'Human'||");
                System.out.println("|| 3 - Modifier un champ de tous les documents d'une collection   ||");
                System.out.println("|| 4 - Quitter le menu UPDATE                                     ||");
                System.out.println("||                                                                ||");
                System.out.println("====================================================================");
                System.out.print("\nVotre choix : ");

                boolean validInputUpdate = false;
                int choixUpdate = 0;
                do {
                    System.out.println("Votre choix : ");
                    try {
                        choixUpdate = sc.nextInt();
                        validInputUpdate = true;
                    } catch (Exception e) {
                        System.out.println("Veuillez entrer un choix valide : ");
                        sc.next();
                    }
                }while (!validInputUpdate);

                if (choixUpdate == 1) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de modifier un document           ||");
                    System.out.println("||       qui a la valeur 'Human'                            ||");
                    System.out.println("=============================================================");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer l'id du document à modifier ou tapez ECHAP pour revenir au menu principal : ");
                    String id = sc.next();
                    while (READMongoDB.idExists(nomCollection, id) == false) {
                        if (id.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("L'id n'existe pas, veuillez entrer un id valide : ");
                        id = sc.next();
                    }
                    if (id.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer la nouvelle valeur ou tapez ECHAP pour revenir au menu principal : ");
                    String newValue = sc.next();
                    UPDATEMongoDB.updateOneHumanDocument(nomCollection, id, newValue);
                } else if (choix == 2) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de modifier tous les documents    ||");
                    System.out.println("||       qui possèdent la valeur 'Human'                    ||");
                    System.out.println("=============================================================");
                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide ou tapez ECHAP pour revenir au menu principal : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer la nouvelle valeur : ");
                    String newValue = sc.next();
                    UPDATEMongoDB.updateMultipleHumanDocuments(nomCollection, newValue);

                } else if (choix == 3) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de modifier un champ de tous      ||");
                    System.out.println("||       les documents d'une collection                     ||");
                    System.out.println("=============================================================");

                    System.out.println();
                    System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------");
                    System.out.println("|!!! Attention si dans la collection il y a des documents qui possèdent plusieurs champs avec le même nom, seulement le premier champ sera modifié !!!|");
                    System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------");


                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }

                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }

                    System.out.println("Veuillez entrer le nom du champ à modifier ou tapez ECHAP pour revenir au menu principal : ");
                    String nomChamp = sc.next();
                    while (READMongoDB.fieldExists(nomCollection, nomChamp) == false) {
                        if (nomChamp.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("Le champ n'existe pas, veuillez entrer un nom de champ valide : ");
                        nomChamp = sc.next();
                    }
                    if (nomChamp.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer la nouvelle valeur : ");
                    String newValue = sc.next();
                    UPDATEMongoDB.updateFieldContent(nomCollection, nomChamp, newValue);

                } else if (choix == 4) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de quitter le menu Update         ||");
                    System.out.println("=============================================================");

                    continue;
                } else {
                    System.out.println("Veuillez entrer un nombre entre 1 et 4");
                }


            } else if (choix == 4) {
                System.out.println("\n========================================================");
                System.out.println("||                  Menu Delete                       ||");
                System.out.println("========================================================");
                System.out.println("========= Veuillez choisir une option : ===============");
                System.out.println("||                                                    ||");
                System.out.println("|| 1 - Supprimer une collection                       ||");
                System.out.println("|| 2 - Supprimer un document d'une collection         ||");
                System.out.println("|| 3 - Supprimer tous les documents d'une collection  ||");
                System.out.println("|| 4 - Quitter le menu Delete                         ||");
                System.out.println("||                                                    ||");
                System.out.println("========================================================");

                boolean validInputDelete = false;
                int choixDelete = 0;
                do {
                    System.out.println("Votre choix : ");
                    try {
                        choixDelete = sc.nextInt();
                        validInputDelete = true;
                    } catch (Exception e) {
                        System.out.println("Veuillez entrer un choix valide : ");
                        sc.next();
                    }
                }while (!validInputDelete);

                if (choixDelete == 1) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de supprimer une collection       ||");
                    System.out.println("=============================================================");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }

                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }

                    DELETEMongoDB.deleteCollection(nomCollection);
                } else if (choixDelete == 2) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de supprimer un document         ||");
                    System.out.println("=============================================================");

                    System.out.println("Veuillez entrer le nom de la collection : ");
                    String nomCollection = sc.next();
                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide ou tapez ECHAP pour revenir au menu principal : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer l'id du document ou tapez ECHAP pour revenir au menu principal : ");
                    String idDocument = sc.next();
                    while (READMongoDB.idExists(nomCollection, idDocument) == false) {
                        if (idDocument.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("L'id n'existe pas, veuillez entrer un id valide ou tapez ECHAP pour revenir au menu principal : ");
                        idDocument = sc.next();
                    }
                    if (idDocument.equals("ECHAP")) {
                        continue;
                    }
                    DELETEMongoDB.deleteDocument(nomCollection, idDocument);

                } else if (choixDelete == 3) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de supprimer tous les documents  ||");
                    System.out.println("||       d'une collection                                  ||");
                    System.out.println("=============================================================");

                    System.out.println("Veuillez entrer le nom de la collection ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCollection = sc.next();

                    while (READMongoDB.collectionExists(nomCollection) == false) {
                        if (nomCollection.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La collection n'existe pas, veuillez entrer un nom de collection valide ou tapez ECHAP pour revenir au menu principal : ");
                        nomCollection = sc.next();
                    }
                    if (nomCollection.equals("ECHAP")) {
                        continue;
                    }
                    DELETEMongoDB.deleteAllDocumentsInOneCollection(nomCollection);
                } else if (choixDelete == 4) {
                    System.out.println("=============================================================");
                    System.out.println("||       Vous avez choisi de quitter le menu Delete        ||");
                    System.out.println("=============================================================");
                    continue;
                }
            } else if (choix == 5) {
                System.out.println("\n========================================================");
                System.out.println("|| Vous avez choisi de quitter le programme. Au revoir!||");
                System.out.println("========================================================");
                ConnectionMongoDB.fermetConnexion();
                ConnectionRedis.fermetConnexion();
                System.exit(0);
            } else {
                System.out.println("\n============================================");
                System.out.println("|| Veuillez entrer un nombre entre 1 et 5 ||");
                System.out.println("============================================");
            }
        }


    }




}
