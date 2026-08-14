package com.example.controlledgears

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.example.controlledgears.databinding.ActivityMainBinding
import com.skydoves.colorpickerview.listeners.ColorListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var bluetoothSocket: BluetoothSocket? = null
    private var connectedDeviceName: String? = null
    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val deviceFilter = "ControlAppLedgears"

    private val discoveredDevices = mutableSetOf<BluetoothDevice>()
    private var deviceAdapter: ArrayAdapter<String>? = null
    private var deviceDialog: AlertDialog? = null
    private var progressBar: ProgressBar? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                updateBluetoothButtonState()
            }
        }
    }

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = IntentCompat.getParcelableExtra(intent, BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    device?.let {
                        try {
                            val name = it.name
                            if (name != null && name.contains(deviceFilter, ignoreCase = true)) {
                                if (discoveredDevices.add(it)) {
                                    deviceAdapter?.add(name)
                                }
                            }
                        } catch (_: SecurityException) {}
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private val enableBtLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth activé", Toast.LENGTH_SHORT).show()
            updateBluetoothButtonState()
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            handleBluetoothAction()
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setupWindowInsets()
        setupSocialButtons()
        setupExpandableSection(binding.layoutRainbowHeader, binding.layoutRainbowPanel, binding.ivRainbowArrow)
        setupExpandableSection(binding.layoutFadeHeader, binding.layoutFadePanel, binding.ivFadeArrow)
        setupExpandableSection(binding.layoutFireHeader, binding.layoutFirePanel, binding.ivFireArrow)
        setupExpandableSection(binding.layoutTextHeader, binding.layoutTextPanel, binding.ivTextArrow)

        setupColorPicker()
        setupTextSection()

        updateBluetoothButtonState()
        checkForUpdates()
        showChangelogIfNeeded()
        binding.btnBluetooth.setOnClickListener {
            checkPermissionsAndHandleBluetooth()
        }
    }

    private fun setupExpandableSection(header: View?, panel: View?, arrow: View?) {
        if (header == null || panel == null || arrow == null) return
        
        header.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.main)
            
            if (panel.isVisible) {
                panel.visibility = View.GONE
                arrow.animate().rotation(0f).setDuration(300).start()
            } else {
                panel.visibility = View.VISIBLE
                arrow.animate().rotation(180f).setDuration(300).start()
            }
        }
    }

    private fun setupTextSection() {
        binding.btnSendText?.setOnClickListener {
            val text = binding.etCustomText?.text?.toString() ?: ""
            if (text.isNotEmpty()) {
                sendBluetoothData(text)
                binding.etCustomText?.text?.clear()
            }
        }
    }

    private fun checkForUpdates() {
        val githubUser = "DebiTheFox"
        val githubRepo = "ControlAppLedgears"
        val apiUrl = "https://api.github.com/repos/$githubUser/$githubRepo/releases/latest"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(apiUrl).build()
                val response = client.newCall(request).execute()
                val jsonData = response.body?.string()

                if (jsonData != null) {
                    val jsonObject = JSONObject(jsonData)
                    val latestVersion = jsonObject.getString("tag_name") // ex: "v15"
                    val downloadUrl = jsonObject.getJSONArray("assets")
                        .getJSONObject(0)
                        .getString("browser_download_url")

                    // Extraire le numéro de version (ex: "v15" -> 15)
                    val latestVersionCode = latestVersion.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                    val currentVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
                    } else {
                        packageManager.getPackageInfo(packageName, 0).versionCode
                    }

                    if (latestVersionCode > currentVersionCode) {
                        withContext(Dispatchers.Main) {
                            showUpdateDialog(latestVersion, downloadUrl)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showUpdateDialog(versionName: String, downloadUrl: String) {
        AlertDialog.Builder(this)
            .setTitle("Mise à jour disponible")
            .setMessage("Une nouvelle version ($versionName) est disponible sur GitHub. Voulez-vous la télécharger et l'installer ?")
            .setPositiveButton("Mettre à jour") { _, _ ->
                downloadAndInstallApk(downloadUrl)
            }
            .setNegativeButton("Plus tard", null)
            .show()
    }

    private fun downloadAndInstallApk(url: String) {
        Toast.makeText(this, "Téléchargement de la mise à jour...", Toast.LENGTH_LONG).show()
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                
                val apkFile = File(getExternalFilesDir(null), "update.apk")
                val fos = FileOutputStream(apkFile)
                fos.write(response.body?.bytes())
                fos.close()

                withContext(Dispatchers.Main) {
                    installApk(apkFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun installApk(file: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun sendBluetoothData(data: String) {
        if (bluetoothSocket?.isConnected == true) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    bluetoothSocket?.outputStream?.write((data + "\n").toByteArray())
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Erreur d'envoi", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Non connecté à l'ESP32", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupColorPicker() {
        binding.colorPicker?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        binding.colorPicker?.setColorListener(ColorListener { _, fromUser ->
            if (fromUser) {
                // Envoyer la couleur à l'ESP32 plus tard
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filter)
        
        val discoveryFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        registerReceiver(discoveryReceiver, discoveryFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(bluetoothStateReceiver)
        unregisterReceiver(discoveryReceiver)
    }

    private fun updateBluetoothButtonState() {
        if (bluetoothSocket?.isConnected == true) {
            binding.btnBluetooth.text = getString(R.string.disconnect_esp32, connectedDeviceName ?: "Inconnu")
        } else if (bluetoothAdapter?.isEnabled == true) {
            binding.btnBluetooth.text = getString(R.string.connect_esp32)
        } else {
            binding.btnBluetooth.text = getString(R.string.activate_bluetooth)
        }
    }

    private fun checkPermissionsAndHandleBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT).show()
            return
        }

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isNotEmpty()) {
            requestPermissionsLauncher.launch(neededPermissions.toTypedArray())
            return
        }
        handleBluetoothAction()
    }

    private fun handleBluetoothAction() {
        bluetoothAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBtLauncher.launch(enableBtIntent)
            } else {
                if (bluetoothSocket?.isConnected == true) {
                    disconnectESP32()
                } else {
                    connectToESP32()
                }
            }
        }
    }

    private fun connectToESP32() {
        discoveredDevices.clear()
        
        // Ajouter d'abord les appareils déjà appairés qui correspondent au filtre
        try {
            bluetoothAdapter?.bondedDevices?.forEach { device ->
                if (device.name?.contains(deviceFilter, ignoreCase = true) == true) {
                    discoveredDevices.add(device)
                }
            }
        } catch (_: SecurityException) {}

        val isDiscovering = try {
            bluetoothAdapter?.isDiscovering == true
        } catch (_: SecurityException) {
            false
        }

        if (isDiscovering) {
            try { bluetoothAdapter?.cancelDiscovery() } catch (_: SecurityException) {}
        }
        
        val discoveryStarted = try {
            bluetoothAdapter?.startDiscovery() ?: false
        } catch (_: SecurityException) {
            false
        }

        if (discoveryStarted) {
            showDeviceSelectionDialog()
        } else {
            // Si le scan ne démarre pas, on montre quand même les appareils déjà connus
            showDeviceSelectionDialog()
        }
    }

    private fun showDeviceSelectionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_device_selection, null)
        val listView = dialogView.findViewById<ListView>(R.id.list_devices)
        progressBar = dialogView.findViewById(R.id.progress_scanning)
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel_scan)

        deviceAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listView.adapter = deviceAdapter

        // Peupler avec les appareils déjà trouvés (appairés ou du scan précédent)
        discoveredDevices.forEach { device ->
            try {
                deviceAdapter?.add(device.name ?: device.address)
            } catch (_: SecurityException) {
                deviceAdapter?.add(device.address)
            }
        }

        deviceDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .setOnDismissListener {
                try {
                    if (bluetoothAdapter?.isDiscovering == true) {
                        bluetoothAdapter?.cancelDiscovery()
                    }
                } catch (_: SecurityException) {}
                updateBluetoothButtonState()
            }
            .create()

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = discoveredDevices.toList()[position]
            deviceDialog?.dismiss()
            performConnection(device)
        }

        btnCancel.setOnClickListener {
            deviceDialog?.dismiss()
        }

        deviceDialog?.show()
    }

    private fun performConnection(device: BluetoothDevice) {
        val deviceName = try { device.name ?: "ESP32" } catch (_: SecurityException) { "ESP32" }
        binding.btnBluetooth.text = getString(R.string.connecting, deviceName)
        binding.btnBluetooth.isEnabled = false

        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(sppUuid)
                    
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || 
                        ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                        try { bluetoothAdapter?.cancelDiscovery() } catch (_: SecurityException) {}
                    }
                    
                    bluetoothSocket?.connect()
                    connectedDeviceName = deviceName
                    true
                } catch (e: IOException) {
                    try {
                        bluetoothSocket?.close()
                    } catch (_: IOException) {}
                    false
                } catch (_: SecurityException) {
                    false
                }
            }

            binding.btnBluetooth.isEnabled = true
            if (success) {
                Toast.makeText(this@MainActivity, "Connecté à $connectedDeviceName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            }
            updateBluetoothButtonState()
        }
    }

    private fun disconnectESP32() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
            Toast.makeText(this, "Déconnecté de $connectedDeviceName", Toast.LENGTH_SHORT).show()
            connectedDeviceName = null
        } catch (_: IOException) {
            Toast.makeText(this, "Erreur lors de la déconnexion", Toast.LENGTH_SHORT).show()
        }
        updateBluetoothButtonState()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSocialButtons() {
        val socialLinks = mapOf(
            binding.btnFacebook to "https://www.facebook.com/foxfursmythiccreati0ns",
            binding.btnInstagram to "https://www.instagram.com/foxfursmythiccreations/",
            binding.btnYoutube to "https://www.youtube.com/@FoxFursMythicCreations",
            binding.btnTiktok to "https://www.tiktok.com/@foxfursmythiccreations"
        )

        socialLinks.forEach { (button, url) ->
            button.setOnClickListener { openUrl(url) }
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }

    private fun showChangelogIfNeeded() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val lastShownVersion = prefs.getInt("last_changelog_version", 0)
        
        val currentVersion = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0).versionCode
            }
        } catch (_: Exception) { 0 }

        if (currentVersion > lastShownVersion) {
            val changelog = """
                🚀 What's New in Version 1.0.0:
                
                • Smart Bluetooth Scan: Filters only "ControlAppLedgears" devices.
                • Dynamic UI: WiFi-style scanning interface.
                • Animated Panels: New sections for Rainbow, Fade, and Fire Breath.
                • Auto-Updates: The app now checks GitHub for new versions automatically.
                • Web Controller: iOS/Universal access via our web dashboard.
                
                Enjoy the new features!
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("Changelog")
                .setMessage(changelog)
                .setPositiveButton("Awesome!", null)
                .setCancelable(true)
                .show()

            prefs.edit().putInt("last_changelog_version", currentVersion).apply()
        }
    }
}