# CoinTrace

CoinTrace est une application qui permet à l’utilisateur de découvrir et d’expérimenter le monde de la crypto-monnaie en toute simplicité.

## Fonctionnalités :

- **Suivi des cryptomonnaies** : Affiche les détails d'une cryptomonnaie, y compris son prix actuel.
- **Graphiques interactifs** : Visualisation des données historiques des prix sous forme de graphiques.
- **Gestion des favoris** : Ajoutez ou supprimez des cryptomonnaies de vos favoris.
- **Transactions** : Accès à une interface pour acheter ou vendre des cryptomonnaies.
- **Gestion des utilisateurs** : Stockage et récupération des données utilisateur via une base de données SQLite.
- **Prise de note** : Fonctionnalité de prise de note simple et intuitive pour l’utilisateur.

## Technologies utilisées :

- **Langage** : Kotlin
- **Base de données** : SQLite (via `DatabaseHelper.kt`)
- **API** : Intégration avec une API REST (CoinGecko) pour récupérer les données des cryptomonnaies (via Retrofit).
- **UI** : Utilisation de `ConstraintLayout`, `RecyclerView`, et `MaterialCardView` pour une interface utilisateur moderne.
- **Graphiques** : Bibliothèque [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) pour les graphiques.

## Structure du projet :

- **`DatabaseHelper.kt`** : Gestion des opérations CRUD pour les utilisateurs et les portefeuilles.
- **`CryptoDetailActivity.kt`** : Activité principale pour afficher les détails d'une cryptomonnaie et son graphique.
- **Réseau** :
  - `ApiService` : Interface pour les appels API.
  - `RetrofitInstance` : Configuration de Retrofit pour les requêtes réseau.

## Installation

1. Clonez ce dépôt :
   ```bash
   git clone : https://github.com/Mathias-Gh/CoinTrace.git
