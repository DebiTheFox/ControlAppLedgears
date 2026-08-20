# Option de choix du Sélecteur de Couleur (Cercle vs Carré HSV)

L'objectif est de permettre à l'utilisateur de choisir son style de sélecteur de couleur préféré dans les réglages.

## Proposed Changes

### [Resources](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/)

#### [MODIFY] [strings.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/values/strings.xml)
- Ajout des libellés pour le choix du sélecteur.

#### [MODIFY] [activity_main.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/layout/activity_main.xml)
- Ajout d'un deuxième layout pour le sélecteur HSV (Carré + Barre de luminosité).

#### [MODIFY] [activity_settings.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/layout/activity_settings.xml)
- Ajout d'un `RadioGroup` pour choisir entre "Cercle RGB" et "Carré HSV".

### [MainActivity](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)
- Ajout de `updatePickerVisibility()` dans `onResume`.
- Configuration des deux sélecteurs.

### [SettingsActivity](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/SettingsActivity.kt)

#### [MODIFY] [SettingsActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/SettingsActivity.kt)
- Sauvegarde du choix utilisateur dans `SharedPreferences`.

## Verification Plan

### Manual Verification
1. Ouvrir les réglages.
2. Changer le type de sélecteur pour "Carré HSV Moderne".
3. Revenir sur l'écran principal.
4. Vérifier que la section "Custom Rainbow" affiche le nouveau sélecteur avec la barre latérale.
5. Vérifier que le changement de luminosité/couleur fonctionne toujours.
