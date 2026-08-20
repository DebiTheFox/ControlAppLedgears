# Walkthrough - Création de SettingsActivity

L'application a été structurée pour séparer les contrôles directs de la configuration.

## Changements effectués

### 1. Nouvelle Activité : SettingsActivity
- Une nouvelle page de réglages a été créée.
- Elle est accessible via une icône ⚙️ ajoutée en haut à droite de l'écran principal.
- **Fonctionnalités incluses** :
    - **Maintenance** : Bouton pour vérifier manuellement les mises à jour sur GitHub.
    - **Appareils enregistrés** : Liste tous les ESP32 auxquels vous avez donné un nom.
    - **Gestion des noms** : Possibilité de supprimer un nom enregistré en cliquant sur l'appareil dans la liste.

### 2. Nettoyage de MainActivity
- Ajout du bouton d'ouverture des réglages.
- Allègement visuel du layout principal.

### 3. Ressources et Système
- Mise à jour du `AndroidManifest.xml` pour enregistrer la nouvelle activité.
- Ajout de chaînes de caractères dans `strings.xml`.
- Support des layouts Portrait et Paysage pour l'icône de réglages.

## Fichiers créés/modifiés

- [AndroidManifest.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/AndroidManifest.xml)
- [MainActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)
- [activity_main.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/layout/activity_main.xml)
- [SettingsActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/SettingsActivity.kt)
- [activity_settings.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/layout/activity_settings.xml)
- [strings.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/values/strings.xml)

## Note technique
Les erreurs de référence à `ActivitySettingsBinding` dans l'éditeur sont normales tant qu'un nouveau build n'a pas été lancé pour générer la classe de binding correspondante au nouveau layout.
