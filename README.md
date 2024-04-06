# L3-ProgAndroid-Projet1
Cette application permettra de suivre toutes les informations importantes dans le développement de League Of Legends. Elle aurait dû simulé un système d’inventaire hypothétique qui se base sur les items du jeu, ainsi que de réaliser des échanges entre joueurs. Cela n'a pu être réalisé dans les temps.
Ce projet a surtout permis de découvrir Android Studio. Il a abouti sur une interface permettant d'afficher, basé sur l'API League Of Legends de RIOT Games, tous les splash arts des champions du jeu ainsi que leur description. Les efforts ont été concentrés sur le fait de ne pas avoir de freeze dû à de mauvaise optimisation,
et de ne pas perdre le contrôle sur l'application lors de chargement etc...


Création de la table user : 

```
CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `mail` varchar(250) NOT NULL,
  `pseudo` varchar(250) NOT NULL,
  `password` varchar(250) NOT NULL,
  `usernameRiot` varchar(250) NOT NULL,
  `region` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

Pour que l'application fonctionne, il faut ajouter via Android Studio un dossier Assets qui contient un fichier config.properties.
Ce fichier contient les informations pour la connexion à la base de données en ligne mais aussi la clé API pour contatcter l'API de Riot.
Structure du fichier : 

```
hostname=***************
port=****
database=****************
username=****************
password=********
apiKey=***********************************
```
