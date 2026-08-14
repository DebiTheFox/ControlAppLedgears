# Plan d'implémentation - Renommage technique complet en "Control LED & Gears"

Ce plan vise à mettre à jour les références techniques restantes (Thèmes, Application ID, Namespace) pour correspondre au nouveau nom "Control LED & Gears" et ainsi assurer une cohérence totale, y compris dans les informations système de l'application.

## Changements proposés

### [Composant] Configuration Gradle

#### [MODIFIER] [build.gradle.kts](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/build.gradle.kts)
*   Changer `namespace` vers `com.example.controlledgears`.
*   Changer `applicationId` vers `com.example.controlledgears`.

### [Composant] Ressources et Thèmes

#### [MODIFIER] [themes.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/res/values/themes.xml)
*   Renommer `Base.Theme.ControlAppLedGears` en `Base.Theme.ControlLedGears`.
*   Renommer `Theme.ControlAppLedGears` en `Theme.ControlLedGears`.

#### [MODIFIER] [themes.xml (night)](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/res/values-night/themes.xml)
*   Renommer `Base.Theme.ControlAppLedGears` en `Base.Theme.ControlLedGears`.

### [Composant] Manifeste

#### [MODIFIER] [AndroidManifest.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/AndroidManifest.xml)
*   Mettre à jour la référence du thème vers `@style/Theme.ControlLedGears`.

## Verification Plan

### Automated Tests
- `gradle_build("app:assembleDebug")` pour vérifier que le renommage technique ne casse pas le build.

### Manual Verification
- Déployer l'application.
- Vérifier dans **Paramètres > Applications > Infos sur l'application** que le nom et l'identifiant technique sont mis à jour.
- **IMPORTANT** : Le changement d'identifiant d'application créera une "nouvelle" application sur le téléphone. L'ancienne (avec le nom "Control app...") devra être désinstallée manuellement si elle est toujours présente.
