# Explication de l'architecture

Le projet android utilise une architecture MVI utilisant la clean architecture.

Il est donc séparé en plusieurs couches : 

 ## I.  Couche application
 Elle contient l'initialisation du projet, ansi que les différentes configurations de base pour le projet (manifest,  initialisation des modules des différentes couches) 
 ## II. Couche core 
La couche **Core** contient les éléments partagés à travers toute l'application et elle est divisée en trois sous-couches : **Data**, **UI**, et **Domain**.
	
### A. Data

C'est la couche qui gère les sources de données. Elle est responsable de :

-   *Les repositories* : Ils définissent comment les données sont obtenues (base de données locale ou API).
-   *Les sources de données* : Les données peuvent venir de l'API Rick and Morty, ou de la base de données local e Room.

L'idée ici est que cette couche s'occupe uniquement de récupérer ou d'enregistrer des données.

### B. Domain

Cette couche représente le **cœur de l'application** en termes de logique métier :

-   *Les models* : Ce sont les objets métier qui sont utilisés dans l'application.
-   *Les interfaces de repositories* : Elle définie les contrats que peuvent utiliser les différentes couches pour récupérer les données.

La couche Domain ne dépend de rien d'autre. Elle est indépendante des détails de mise en œuvre.

### C. UI

Cette sous-couche contient les composants **généraux** et **réutilisables** de l'interface utilisateur :

-   *Composants visuels communs* (par exemple, des boutons customisés, des dialogues partagés entre plusieurs écrans).
-   *Thèmes ou styles globaux*.
-  *Navigation*

Elle ne contient pas de logique métier, seulement des éléments visuels.

## III. Couche Features

Cette couche est dédiée aux différentes **fonctionnalités** ou **écrans** de l'application. Chaque fonctionnalité (écran) a ses propres composants et suit la structure générale :




Chaque feature est ainsi isolée pour faciliter la maintenance de l'application grâce à la clean architecture.
