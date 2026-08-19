# Automatisation des versions et du Changelog via GitHub

L'objectif est de supprimer le code "en dur" lié aux versions et aux changelogs pour que tout soit géré via les "Releases" sur GitHub.

## User Review Required

> [!IMPORTANT]
> - Dorénavant, pour publier une mise à jour, il vous suffira de créer une **Release** sur GitHub avec une description. L'application récupérera automatiquement ce texte pour l'afficher aux utilisateurs.
> - Vous n'aurez plus besoin de modifier `MainActivity.kt` pour chaque nouvelle version.

## Proposed Changes

### [MainActivity](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)
- **`checkForUpdates()`** : Extraction du champ `"body"` (notes de version) et `"name"` (titre) de l'API GitHub.
- **`showUpdateDialog()`** : Mise à jour de la signature pour accepter et afficher ces notes dynamiques.
- **`showChangelogIfNeeded()`** : Suppression ou simplification, car les notes seront affichées lors de la proposition de mise à jour.

### [Build Configuration](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/build.gradle.kts)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/build.gradle.kts)
- Augmentation de `versionCode` et `versionName` pour préparer la prochaine étape réelle.

## Flux de travail recommandé (GitHub)

1.  Faites vos modifications de code normalement et faites un `push`.
2.  Quand vous estimez que c'est une "mise à jour importante" :
    *   Allez sur GitHub > **Releases** > **Create a new release**.
    *   Choisissez un tag (ex: `v2`, `v3`).
    *   Écrivez vos nouveautés dans la description.
    *   Attachez le nouveau fichier APK.
3.  L'application détectera automatiquement le changement et affichera **votre** texte.

## Verification Plan

### Manual Verification
1. Lancer l'application. Elle appelle l'API GitHub.
2. Vérifier que si une version supérieure existe sur GitHub, la boîte de dialogue affiche bien la description saisie sur le site GitHub au lieu du texte par défaut.
