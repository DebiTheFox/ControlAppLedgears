# Walkthrough - Boutons d'action LED

Les modifications suivantes ont été apportées pour permettre de lancer les effets LED directement depuis l'application :

## Changements effectués

### 1. Nouveaux boutons "Lancer"
- Des boutons **"Lancer"** ont été ajoutés dans les sections suivantes :
    - **Custom Rainbow** : Envoie la commande `RAINBOW`.
    - **Custom Fade** : Envoie la commande `FADE`.
    - **Custom Fire Breath** : Envoie la commande `FIRE`.
- Ces boutons sont situés en bas à droite de chaque panneau extensible.

### 2. Ressources
- Une nouvelle chaîne de caractères `launch` ("Lancer") a été ajoutée à `strings.xml`.

### 3. Logique de communication
- La méthode `setupEffectButtons()` dans `MainActivity.kt` gère les clics sur ces boutons et utilise `sendBluetoothData()` pour communiquer avec l'ESP32.

## Fichiers modifiés

- [strings.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/values/strings.xml)
- [activity_main.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/layout/activity_main.xml) (Portrait)
- [activity_main.xml](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/res/layout-land/activity_main.xml) (Paysage - Rainbow uniquement)
- [MainActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/app/src/main/java/com/example/controlledgears/MainActivity.kt)

## Vérification effectuée

- ✅ Validation de la hiérarchie XML pour les nouveaux boutons.
- ✅ Utilisation d'appels sécurisés (`?.`) dans le code Kotlin pour éviter les crashs si un bouton est absent d'une variante de layout.
- ✅ Vérification de l'envoi des commandes via Bluetooth.
