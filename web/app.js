// Initialisation du sélecteur de couleurs
var colorPicker = new iro.ColorPicker("#color-picker-container", {
    width: 250,
    color: "#ff0000",
    borderWidth: 1,
    borderColor: "#fff",
});

// Gestion des bandeaux extensibles
document.querySelectorAll('.expand-header').forEach(header => {
    header.addEventListener('click', () => {
        const targetId = header.getAttribute('data-target');
        const panel = document.getElementById(targetId);

        // Fermer les autres
        document.querySelectorAll('.expand-panel').forEach(p => {
            if (p.id !== targetId) p.classList.remove('active');
        });
        document.querySelectorAll('.expand-header').forEach(h => {
            if (h !== header) h.classList.remove('active');
        });

        // Basculer l'actuel
        panel.classList.toggle('active');
        header.classList.toggle('active');
    });
});

// --- Logique Bluetooth (BLE) ---
let bluetoothDevice = null;
let characteristic = null;

const btnConnect = document.getElementById('btn-connect');
const statusText = document.getElementById('status');

// UUIDs standards pour le service UART (souvent utilisé avec ESP32 BLE)
const UART_SERVICE_UUID = '6e400001-b5a3-f393-e0a9-e50e24dcca9e';
const UART_TX_CHAR_UUID = '6e400002-b5a3-f393-e0a9-e50e24dcca9e';

btnConnect.addEventListener('click', async () => {
    if (bluetoothDevice && bluetoothDevice.gatt.connected) {
        bluetoothDevice.gatt.disconnect();
        return;
    }

    try {
        statusText.innerText = "Recherche...";

        // Filtre pour les appareils commençant par ControlLED&Gears
        bluetoothDevice = await navigator.bluetooth.requestDevice({
            filters: [{ namePrefix: 'ControlLED&Gears' }],
            optionalServices: [UART_SERVICE_UUID]
        });

        bluetoothDevice.addEventListener('gattserverdisconnected', onDisconnected);

        statusText.innerText = "Connexion à " + bluetoothDevice.name + "...";
        const server = await bluetoothDevice.gatt.connect();

        const service = await server.getPrimaryService(UART_SERVICE_UUID);
        characteristic = await service.getCharacteristic(UART_TX_CHAR_UUID);

        statusText.innerText = "Connecté à " + bluetoothDevice.name;
        btnConnect.innerText = "DÉCONNECTER";
        btnConnect.style.backgroundColor = "#D32F2F";

    } catch (error) {
        console.log(error);
        statusText.innerText = "Erreur ou Scan annulé";
        if (error.name === 'NotFoundError') {
            statusText.innerText = "Aucun appareil trouvé";
        }
    }
});

function onDisconnected() {
    statusText.innerText = "Déconnecté";
    btnConnect.innerText = "RECHERCHER CONNEXION ESP32";
    btnConnect.style.backgroundColor = "#EF6C00";
    characteristic = null;
}

// Fonction pour envoyer des données
async function sendData(data) {
    if (!characteristic) return;

    try {
        const encoder = new TextEncoder();
        await characteristic.writeValue(encoder.encode(data + "\n"));
    } catch (error) {
        console.error("Erreur d'envoi:", error);
    }
}

// Événements du sélecteur et sliders
colorPicker.on('color:change', function(color) {
    // On pourrait envoyer le RGB ici en temps réel
    // sendData(`RGB:${color.rgb.r},${color.rgb.g},${color.rgb.b}`);
});

document.getElementById('brightness').addEventListener('change', (e) => {
    sendData(`BRIGHT:${e.target.value}`);
});
