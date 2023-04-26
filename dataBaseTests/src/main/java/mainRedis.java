import RedisCRUD.CREATERedis;
import RedisCRUD.DELETERedis;
import RedisCRUD.READRedis;
import RedisCRUD.UPDATERedis;

import java.util.Scanner;

public class mainRedis {

    public static void main(String[] args) {
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
            System.out.print("\nVotre choix : ");
            Scanner sc = new Scanner(System.in);
            int choix = sc.nextInt();
            if (choix == 1) {
                System.out.println("\n=====================================================================");
                System.out.println("||                          Menu Create                              ||");
                System.out.println("=======================================================================");
                System.out.println("=================== Veuillez choisir une option : =====================");
                System.out.println("||                                                                   ||");
                System.out.println("|| 1 - Créer une clé avec un fichier XML en valeur                   ||");
                System.out.println("|| 2 - Stocker l'ensemble des fichiers XML d'un dossier dans Redis  ||");
                System.out.println("|| 3 - Quitter le menu Create                                        ||");
                System.out.println("||                                                                   ||");
                System.out.println("========================================================================");
                System.out.print("\nVotre choix : ");
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
                    System.out.println("\n=====================================================================");
                    System.out.println("||                          Menu Update                              ||");
                    System.out.println("=======================================================================");
                    System.out.println("=================== Veuillez choisir une option : =====================");
                    System.out.println("||                                                                   ||");
                    System.out.println("|| 1 - Modifier la valeur d'une clé                                  ||");
                    System.out.println("|| 2 - Modifier la valeur d'une clé par un fichier XML               ||");
                    System.out.println("|| 3 - Modifie toutes les valeurs des clés                           ||");
                    System.out.println("||     pour remplacer 'Human' par une autre valeur                   ||");
                    System.out.println("|| x - Quitter le menu Update                                        ||");
                    System.out.println("========================================================================");
                    System.out.print("\nVotre choix : ");
                    int choixUpdate = sc.nextInt();
                    if (choixUpdate == 1) {
                        System.out.println("Vous avez choisi de modifier la valeur d'une clé");
                        System.out.println("Veuillez entrer le nom de la clé : ");
                        String nomCle = sc.next();
                        while (READRedis.readOneKeyExist(nomCle) == false) {
                            System.out.println("La clé n'existe pas, veuillez entrer un nom de clé valide : ");
                            nomCle = sc.next();
                        }
                        System.out.println("Veuillez entrer la nouvelle valeur de la clé : ");
                        String valeurCle = sc.next();
                        UPDATERedis.updateOneKeyValue(nomCle, valeurCle);
                    }
                    else if(choixUpdate == 2){
                        System.out.println("Vous avez choisi de modifier la valeur d'une clé par un fichier XML (qui sera stocké sous forme JSON)");
                        System.out.println("Veuillez entrer le nom de la clé : ");
                        String nomCle = sc.next();
                        while (READRedis.readOneKeyExist(nomCle) == false) {
                            System.out.println("La clé n'existe pas, veuillez entrer un nom de clé valide : ");
                            nomCle = sc.next();
                        }
                        System.out.println("Veuillez entrer le chemin absolu du fichier XML : ");
                        String cheminFichierXML = sc.next();
                        UPDATERedis.updateOneKeyValue(nomCle, cheminFichierXML);
                    }
                    else if(choixUpdate == 4){
                        System.out.println("Vous avez choisi de quittez le menu Update");
                        continue;
                    }


                }

            } else if (choix == 2) {
                System.out.println("\n=====================================================================");
                System.out.println("||                          Menu Read                                ||");
                System.out.println("=======================================================================");
                System.out.println("=================== Veuillez choisir une option : =====================");
                System.out.println("||                                                                   ||");
                System.out.println("|| 1 - Lire la valeur d'une clé                                     ||");
                System.out.println("|| 2 - Lire toutes les clés présentes dans la base de données       ||");
                System.out.println("|| 3 - Lire toutes les clés qui a dans les valeurs un humain        ||");
                System.out.println("|| 4 - Lire toutes les clés qui a dans les valeurs un humain avec   ||");
                System.out.println("||     une probabilité de survie supérieure à 0.5                   ||");
                System.out.println("|| 5 - Quitter le menu Read                                         ||");
                System.out.println("||                                                                   ||");
                System.out.println("========================================================================");
                System.out.print("\nVotre choix : ");

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
