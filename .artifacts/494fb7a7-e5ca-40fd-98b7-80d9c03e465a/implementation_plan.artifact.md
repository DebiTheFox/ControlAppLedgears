# Filtrage des appareils Bluetooth au scan

L'objectif est de modifier le comportement de connexion Bluetooth pour qu'il effectue un scan (découverte) et n'affiche que les appareils dont le nom contient le mot-clé "ControlAppLedgears". Les autres appareils (comme "JBL") seront ignorés.

## Changements Proposés

### [MainActivity](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/java/com/example/controlledgears/MainActivity.kt)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/DebiTheFox.DESKTOP-G9J6UVR/AndroidStudioProjects/ControlAppLedgears/app/src/main/java/com/example/controlledgears/MainActivity.kt)
- Ajouter un `BroadcastReceiver` pour intercepter les appareils découverts via `BluetoothDevice.ACTION_FOUND`.
- Mettre à jour `connectToESP32()` pour lancer la découverte Bluetooth.
- Implémenter une boîte de dialogue (`AlertDialog`) qui affiche la liste des appareils trouvés (pairés ou découverts) répondant au critère de filtrage ("ControlAppLedgears").
- Filtrer les résultats pour exclure tout appareil ne contenant pas le mot-clé.

## Plan de Vérification

### Tests Manuels
1. Appuyer sur le bouton "Rechercher connexion ESP32".
2. Vérifier qu'une boîte de dialogue s'affiche avec la liste des appareils.
3. S'assurer que seuls les appareils contenant "ControlAppLedgears" sont visibles.
4. Vérifier que les appareils comme "JBL" ou d'autres ne sont pas affichés.
5. Sélectionner un appareil et vérifier que la connexion s'établit.
