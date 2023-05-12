package Applications;

import ConnectionBD.ConnectionRedis;
import RedisCRUD.CREATERedis;
import RedisCRUD.DELETERedis;
import RedisCRUD.READRedis;
import RedisCRUD.UPDATERedis;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class mainRedis {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=========================================================");
        System.out.println("|| Tentative de connexion à la base de données Redis   ||");
        System.out.println("=========================================================\n");

        //Vérifie que la connexion à Redis est bien établie
        if (ConnectionRedis.isDockerRunning()) {
            System.out.println("Connexion à la base de données Redis réussie");
        } else {
            System.out.println("Connexion à la base de données Redis échouée veuillez vérfier que docker est bien lancé");
            return;
        }


        while (true) {


            System.out.println("\n========================================================");
            System.out.println("|| Programme de test de la base de données Redis      ||");
            System.out.println("========================================================\n");
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
            int choix = 0;
            boolean validInput = false;
            do {
                System.out.println("Votre choix : ");
                try {
                    choix = sc.nextInt();
                    validInput = true;
                } catch (Exception e) {
                    System.out.println("Veuillez entrer un choix valide : ");
                    sc.next();
                }
            } while (!validInput);


            if (choix == 1) {
                System.out.println("\n=======================================================================");
                System.out.println("||                          Menu Create                              ||");
                System.out.println("=======================================================================");
                System.out.println("=================== Veuillez choisir une option : =====================");
                System.out.println("||                                                                   ||");
                System.out.println("|| 1 - Créer une clé avec un fichier XML en valeur                   ||");
                System.out.println("|| 2 - Stocker l'ensemble des fichiers XML d'un dossier dans Redis   ||");
                System.out.println("|| 3 - Quitter le menu Create                                        ||");
                System.out.println("||                                                                   ||");
                System.out.println("========================================================================");
                boolean isIntCreate = false;
                int choixCreate = 0;
                do {
                    System.out.print("\nVotre choix : ");
                    try {
                        choixCreate = sc.nextInt();
                        isIntCreate = true;
                    } catch (Exception e) {
                        System.out.println("Veuillez entrer un choix valide : ");
                        sc.next();
                    }
                } while (!isIntCreate);

                if (choixCreate == 1) {
                    System.out.println("\n=======================================================================");
                    System.out.println("|| Vous avez choisi de créer une clé avec un fichier XML en valeur   ||");
                    System.out.println("=======================================================================");
                    System.out.println();
                    System.out.println("Veuillez entrer le nom de votre clé ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCle = sc.next();
                    while (READRedis.readOneKeyExist(nomCle) == true) {
                        System.out.println("La clé existe déjà, veuillez entrer un autre nom de clé : ");
                        nomCle = sc.next();
                        if (nomCle.equals("ECHAP")) {
                            break;
                        }
                    }
                    if (nomCle.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer le chemin absolu du fichier XML ou tapez ECHAP pour quitter : ");
                    String cheminFichierXML = sc.next();
                    if (cheminFichierXML.equals("ECHAP")) {
                        continue;

                    }
                    CREATERedis.createOneKeyValue(nomCle, cheminFichierXML);
                } else if (choixCreate == 2) {
                    System.out.println("\n=======================================================================");
                    System.out.println("|| Vous avez choisi de stocker l'ensemble des fichiers XML d'un      ||");
                    System.out.println("|| dossier dans Redis                                                ||");
                    System.out.println("=======================================================================");
                    System.out.println("Veuillez entrer le chemin absolu du dossier ou tapez ECHAP pour revenir au menu principal : ");
                    //Demande le chemin du dossier et vérifie que le chemin est valide
                    String cheminDossier = sc.next();
                    File folder = new File(cheminDossier);


                    while (!folder.isDirectory()) {
                        System.out.println("Le chemin n'est pas valide, veuillez entrer un chemin valide : ");
                        cheminDossier = sc.next();
                        folder = new File(cheminDossier);
                        if (cheminDossier.equals("ECHAP")) {
                            break;
                        }
                    }
                    if (cheminDossier.equals("ECHAP")) {
                        continue;
                    }
                    CREATERedis.createAllKeyValue(cheminDossier);
                } else if (choixCreate == 3) {
                    System.out.println("=======================================================================");
                    System.out.println("||        Vous avez choisi de quitter le menu Create                 ||");
                    System.out.println("=======================================================================");
                }
            } else if (choix == 2) {
                System.out.println("\n=======================================================================");
                System.out.println("||                          Menu Read                                ||");
                System.out.println("=======================================================================");
                System.out.println("=================== Veuillez choisir une option : =====================");
                System.out.println("||                                                                   ||");
                System.out.println("|| 1 - Lire la valeur d'une clé                                      ||");
                System.out.println("|| 2 - Lire toutes les clés présentes dans la base de données        ||");
                System.out.println("|| 3 - Lire toutes les clés qui a dans les valeurs un humain         ||");
                System.out.println("|| 4 - Lire toutes les clés qui a dans les valeurs un humain avec    ||");
                System.out.println("||     une probabilité de survie supérieure à 0.5                    ||");
                System.out.println("|| 5 - Quitter le menu Read                                          ||");
                System.out.println("||                                                                   ||");
                System.out.println("=======================================================================");
                System.out.print("\nVotre choix : ");

                int choixRead = sc.nextInt();
                if (choixRead == 1) {
                    boolean nomCleCorrecte = false;
                    System.out.println("=======================================================================");
                    System.out.println("|| Vous avez choisi de lire la valeur d'une clé                      ||");
                    System.out.println("=======================================================================");

                    System.out.println("Veuillez entrer le nom de la clé, ou tapez ECHAP pour quitter : ");
                    String nomCle = sc.next();
                    if (nomCle.equals("ECHAP")) {
                        continue;
                    }
                    else if(READRedis.readOneKeyExist(nomCle) == false){
                        System.out.println("La clé n'existe pas, veuillez entrer un autre nom de clé : ");
                        nomCle = sc.next();
                        if (nomCle.equals("ECHAP")) {
                            continue;
                        }
                    } else if (READRedis.readOneKeyExist(nomCle) == true) {
                        READRedis.readOneKeyValue(nomCle);
                    }

                } else if (choixRead == 2) {
                    System.out.println("=======================================================================");
                    System.out.println("|| Vous avez choisi de lire toutes les clés présentes dans la base   ||");
                    System.out.println("|| de données                                                        ||");
                    System.out.println("=======================================================================");

                    READRedis.readAllKey();
                } else if (choixRead == 3) {
                    System.out.println("=======================================================================");
                    System.out.println("|| Vous avez choisi de lire toutes les clés qui a dans les valeurs   ||");
                    System.out.println("|| un humain                                                         ||");
                    System.out.println("=======================================================================");

                    READRedis.readAllKeyWithHuman();
                } else if (choixRead == 4) {
                    System.out.println("=======================================================================");
                    System.out.println("|| Vous avez choisi de lire toutes les clés qui a dans les valeurs   ||");
                    System.out.println("|| un humain avec une probabilité de survie supérieure à 0.5         ||");
                    System.out.println("=======================================================================");
                    READRedis.getHumanWithProbability();
                } else if (choixRead == 5) {
                    System.out.println("=======================================================================");
                    System.out.println("||       Vous avez choisi de quitter le menu Read                    ||");
                    System.out.println("=======================================================================");

                    continue;
                }
            } else if (choix == 3) {
                System.out.println("\n=======================================================================");
                System.out.println("||                          Menu Update                              ||");
                System.out.println("=======================================================================");
                System.out.println("=================== Veuillez choisir une option : =====================");
                System.out.println("||                                                                   ||");
                System.out.println("|| 1 - Modifier la valeur d'une clé                                  ||");
                System.out.println("|| 2 - Modifier la valeur d'une clé par un fichier XML               ||");
                System.out.println("|| 3 - Modifie toutes les valeurs des clés                           ||");
                System.out.println("||     pour remplacer 'Human' par une autre valeur                   ||");
                System.out.println("|| 4 - Modifie toutes les valeurs des clés pour remplacer le content ||");
                System.out.println("||     par une autre valeur                                          ||");
                System.out.println("|| 5 - Quitter le menu Update                                        ||");
                System.out.println("=======================================================================");
                System.out.print("\nVotre choix : ");
                int choixUpdate = sc.nextInt();
                if (choixUpdate == 1) {
                    System.out.println("=======================================================================");
                    System.out.println("|| Vous avez choisi de modifier la valeur d'une clé                  ||");
                    System.out.println("=======================================================================");

                    System.out.println("Veuillez entrer le nom de la clé ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCle = sc.next();
                    while (READRedis.readOneKeyExist(nomCle) == false) {
                        if (nomCle.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La clé n'existe pas, veuillez entrer un nom de clé valide ou tapez ECHAP pour revenir au menu principal : ");
                        nomCle = sc.next();
                    }
                    if (nomCle.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer la nouvelle valeur de la clé : ");
                    String valeurCle = sc.next();
                    UPDATERedis.updateOneKeyValue(nomCle, valeurCle);
                } else if (choixUpdate == 2) {
                    System.out.println("=======================================================================");
                    System.out.println("|| Vous avez choisi de modifier la valeur d'une clé par un fichier XML||");
                    System.out.println("=======================================================================");

                    System.out.println("Veuillez entrer le nom de la clé ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCle = sc.next();
                    while (READRedis.readOneKeyExist(nomCle) == false) {
                        if (nomCle.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La clé n'existe pas, veuillez entrer un nom de clé valide : ");
                        nomCle = sc.next();
                    }
                    if (nomCle.equals("ECHAP")) {
                        continue;
                    }
                    System.out.println("Veuillez entrer le chemin absolu du fichier XML : ");
                    String cheminFichierXML = sc.next();
                    UPDATERedis.updateOneKeyJSON(nomCle, cheminFichierXML);
                } else if (choixUpdate == 3) {
                    System.out.println("=======================================================================");
                    System.out.println("|| Vous avez choisi de modifier toutes les valeurs des clés pour     ||");
                    System.out.println("|| remplacer 'Human' par une autre valeur                            ||");
                    System.out.println("=======================================================================");

                    System.out.println("Veuillez entrer la nouvelle valeur ou tapez ECHAP pour revenir au menu principal : ");
                    String valeurCle = sc.next();
                    if (valeurCle.equals("ECHAP")) {
                        continue;
                    }
                    UPDATERedis.updateAllKeyJSONWithValueHuman(valeurCle);
                } else if (choixUpdate == 4) {
                    System.out.println("===========================================================================");
                    System.out.println("|| Vous avez choisi de modifier toutes les valeurs des clés pour         ||");
                    System.out.println("|| remplacer le content par une autre valeur (généralement c'est Human)  ||");
                    System.out.println("===========================================================================");

                    System.out.println("Veuillez entrer la nouvelle valeur ou tapez ECHAP pour revenir au menu principal : ");
                    String valeurCle = sc.next();
                    if (valeurCle.equals("ECHAP")) {
                        continue;
                    }
                    UPDATERedis.updateAllKeyJSONWithValue(valeurCle);
                } else if (choixUpdate == 5) {
                    System.out.println("=======================================================================");
                    System.out.println("||       Vous avez choisi de quitter le menu Update                  ||");
                    System.out.println("=======================================================================");
                    continue;
                }

            } else if (choix == 4) {
                System.out.println("\n=====================================================================");
                System.out.println("||                          Menu Delete                              ||");
                System.out.println("=======================================================================");
                System.out.println("=================== Veuillez choisir une option : =====================");
                System.out.println("||                                                                   ||");
                System.out.println("|| 1 - Supprimer une clé                                             ||");
                System.out.println("|| 2 - Supprimer toutes les clés                                     ||");
                System.out.println("|| 3 - Supprimer les 50 dernières clés                               ||");
                System.out.println("|| 4 - Quitter le menu Delete                                        ||");
                System.out.println("||                                                                   ||");
                System.out.println("========================================================================");

                System.out.println("\nVotre choix : ");
                int choixDelete = sc.nextInt();
                if (choixDelete == 1) {
                    System.out.println("=======================================================================");
                    System.out.println("||        Vous avez choisi de supprimer une clé                      ||");
                    System.out.println("=======================================================================");


                    System.out.println("Veuillez entrer le nom de la clé ou tapez ECHAP pour revenir au menu principal : ");
                    String nomCle = sc.next();
                    while (READRedis.readOneKeyExist(nomCle) == false) {
                        if (nomCle.equals("ECHAP")) {
                            break;
                        }
                        System.out.println("La clé n'existe pas, veuillez entrer un nom de clé valide : ");
                        nomCle = sc.next();
                    }
                    if (nomCle.equals("ECHAP")) {
                        continue;
                    }
                    DELETERedis.deleteOneKey(nomCle);
                } else if (choixDelete == 2) {
                    System.out.println("=======================================================================");
                    System.out.println("||        Vous avez choisi de supprimer toutes les clés              ||");
                    System.out.println("=======================================================================");
                    DELETERedis.deleteAllKey();

                } else if (choixDelete == 3) {
                    System.out.println("=======================================================================");
                    System.out.println("||        Vous avez choisi de supprimer les 50 dernières clés        ||");
                    System.out.println("=======================================================================");

                    DELETERedis.delete50LastKey();
                } else if (choixDelete == 4) {
                    System.out.println("=======================================================================");
                    System.out.println("||       Vous avez choisi de quitter le menu Delete                  ||");
                    System.out.println("=======================================================================");
                    continue;
                }

            } else if (choix == 5) {
                System.out.println("\n========================================================");
                System.out.println("|| Vous avez choisi de quitter le programme. Au revoir!||");
                System.out.println("========================================================");
                System.exit(0);
            } else {
                System.out.println("\n============================================");
                System.out.println("|| Veuillez entrer un nombre entre 1 et 5 ||");
                System.out.println("============================================");
            }

        }


    }


}
