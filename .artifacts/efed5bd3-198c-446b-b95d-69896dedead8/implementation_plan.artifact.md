# Ajout de boutons d'action pour les effets LED

L'objectif est d'ajouter des boutons "Lancer" dans chaque section extensible (Rainbow, Fade, Fire Breath) pour envoyer la commande correspondante à l'ESP32.

## Proposed Changes

### [Resources](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/)

#### [MODIFY] [strings.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/values/strings.xml)
- Ajouter une chaîne `launch` ("Lancer").

#### [MODIFY] [activity_main.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/layout/activity_main.xml)
- Ajouter un bouton `btn_start_rainbow` dans `layout_rainbow_panel`.
- Ajouter un bouton `btn_start_fade` dans `layout_fade_panel`.
- Ajouter un bouton `btn_start_fire` dans `layout_fire_panel`.

### [MainActivity](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)
- Configurer les `OnClickListener` pour ces nouveaux boutons.
- Envoyer les commandes textuelles ("RAINBOW", "FADE", "FIRE") via Bluetooth.

## Verification Plan

### Manual Verification
1. Se connecter à l'ESP32.
2. Ouvrir la section "Custom Rainbow" et cliquer sur "Lancer". Vérifier que l'effet s'active sur les LED.
3. Répéter pour "Custom Fade" et "Custom Fire Breath".
