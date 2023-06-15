# TestsDataBaseNoSQL

Bienvenue dans mon projet qui a pour objectif de tester des bases de données NoSQL.

Actuellement voici les bases de données que j'ai testé et fonctionnelles :

- MongoDB
- Redis


## Prérequis

Avant tout vous devrez être administrateur de votre machine pour pouvoir lancer les bases de données.

Vous devez avoir les prérequis suivants pour lancer les bases de données et les applications :

- **Docker** : Vous devez avoir docker d'installé sur votre machine.
- **Docker-compose** : Vous devez avoir docker-compose d'installé sur votre machine.
- **Java** : Vous devez avoir java d'installé sur votre machine.

Vous devez lancer le fichier docker-compose.yml pour lancer les bases de données.

Ce fichier se trouve dans le dossier **/TestsDataBaseNoSQL/DataBaseCompose/docker-compose.yml**.

Pour le lancer il faut ouvrir un terminal dans le dossier **/TestsDataBaseNoSQL/DataBaseCompose** et lancer la commande suivante :

```docker-compose up```

## Application

Les fichiers .jar sont dans le dossier **/TestsDataBaseNoSQL/ApplicationsJar**.

Pour les lancer il faut ouvrir un terminal dans le dossier **/TestsDataBaseNoSQL/ApplicationsJar** et lancer la commande suivante :

```bash java -jar nomDuFichier.jar```

--------------------

## Cas d'utilisation


### Lancez les bases de données


**Étape 1** : Lancez une console en administrateur dans le dossier **/TestsDataBaseNoSQL/DataBaseCompose**.

![imgConsole1.png](imgREADME%2FimgConsole1.png)

**Étape 2** : Lancez le fichier docker-compose.yml pour lancer les bases de données.

![Conteneurs.png](imgREADME%2FConteneurs.png)

**Étape 3** : Vérification des bases de données lancées.



### Lancez les applications

L'application MongoDB effectue les opérations sur la base de données **'actiaDataBase'** donc si vous
n'avez pas cette base de données vous devez la créer ou vous pouvez modifier la base de données dans la classe 
`ConnectionMongoDB`.

**Étape 1** : Lancez une console dans le dossier **/TestsDataBaseNoSQL/ApplicationsJar**.

**Étape 2** : Lancez les fichiers .jar avec la commande suivante :

```bash java -jar nomDuFichier.jar```

