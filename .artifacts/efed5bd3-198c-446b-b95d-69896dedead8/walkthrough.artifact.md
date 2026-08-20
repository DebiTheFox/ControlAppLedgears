# Walkthrough - Option Sélecteur HSV Moderne

J'ai implémenté la possibilité de choisir votre style de sélecteur de couleur directement dans les réglages.

## Changements effectués

### 1. Choix du Sélecteur dans les Réglages
- Dans la page **Réglages** (⚙️), une nouvelle section permet de choisir entre :
    - **Cercle RGB Classique** : Le design actuel.
    - **Carré HSV Moderne** : Le design inspiré de votre lien Oklab, avec une jauge verticale pour la luminosité/intensité sur le côté.
- Le choix est sauvegardé et appliqué instantanément sur l'écran principal.

### 2. Nouveau Layout HSV
- Ajout d'un bloc compact dans `MainActivity` contenant le sélecteur et sa barre latérale.
- La barre latérale est placée à droite pour une manipulation facile.
- Les couleurs sont envoyées en temps réel à l'ESP32 lors du déplacement du curseur (format Hexadécimal `#RRGGBB`).

### 3. Fichiers mis à jour
- **MainActivity.kt** : Gestion dynamique de l'affichage du sélecteur choisi.
- **SettingsActivity.kt** : Enregistrement de la préférence utilisateur.
- **activity_main.xml** & **activity_settings.xml** : Mise à jour des interfaces.

## Prochaines étapes suggérées
Pour obtenir le look "Carré" exact de l'image, vous devrez ajouter une image de palette carrée (ex: `color_square_hsv.png`) dans votre dossier `res/drawable` et mettre à jour l'attribut `app:palette` dans `activity_main.xml` (actuellement, il réutilise le cercle pour éviter tout crash visuel).

---

Les modifications ont été poussées sur votre GitHub. 🚀
