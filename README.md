# L3-ProgAndroid-Projet1
Cette application permettra de suivre toutes les informations importantes dans le développement de League Of Legends. De plus elle simule un système d’inventaire hypothétique qui se base sur les items du jeu. L’application permettra de réaliser des échanges entre joueurs. Elle permettra aussi d’acheter des items via une boutique. 


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
Ce fichier contient les informations pour la connexion à la base de données en ligne.
Structure du fichier : 

```
hostname=***************
port=****
database=****************
username=****************
password=********
```
