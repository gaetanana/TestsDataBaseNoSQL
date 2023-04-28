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

```bash docker-compose up```

## Application

Les fichiers .jar sont dans le dossier **/TestsDataBaseNoSQL/ApplicationsJar**.

Pour les lancer il faut ouvrir un terminal dans le dossier **/TestsDataBaseNoSQL/ApplicationsJar** et lancer la commande suivante :

```bash java -jar nomDuFichier.jar```
