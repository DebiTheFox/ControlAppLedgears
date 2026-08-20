package com.example.controlledgears

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.controlledgears.databinding.ActivitySettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCheckUpdatesSettings.setOnClickListener {
            checkForUpdates(manual = true)
        }

        setupPickerChoice()
        loadSavedDevices()
    }

    private fun setupPickerChoice() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val pickerType = prefs.getString("color_picker_type", "circle")

        if (pickerType == "hsv") {
            binding.rbPickerHsv.isChecked = true
        } else {
            binding.rbPickerCircle.isChecked = true
        }

        binding.rgPickerType.setOnCheckedChangeListener { _, checkedId ->
            val type = if (checkedId == R.id.rb_picker_hsv) "hsv" else "circle"
            prefs.edit().putString("color_picker_type", type).apply()
            Toast.makeText(this, "Sélecteur mis à jour", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedDevices() {
        binding.layoutSavedDevices.removeAllViews()
        val prefs = getSharedPreferences("device_names", MODE_PRIVATE)
        val allEntries = prefs.all

        if (allEntries.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = getString(R.string.no_saved_devices)
                setTextColor(0xFF888888.toInt())
                setPadding(0, 20, 0, 0)
            }
            binding.layoutSavedDevices.addView(emptyView)
            return
        }

        allEntries.forEach { (address, name) ->
            val itemView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, binding.layoutSavedDevices, false)
            val text1 = itemView.findViewById<TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<TextView>(android.R.id.text2)
            
            text1.text = name.toString()
            text1.setTextColor(0xFFFFFFFF.toInt())
            text2.text = address
            text2.setTextColor(0xFFBBBBBB.toInt())

            itemView.setOnClickListener {
                showDeleteDeviceDialog(address, name.toString())
            }

            binding.layoutSavedDevices.addView(itemView)
        }
    }

    private fun showDeleteDeviceDialog(address: String, name: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage("Voulez-vous supprimer l'appareil '$name' ($address) ?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                getSharedPreferences("device_names", MODE_PRIVATE).edit().remove(address).apply()
                loadSavedDevices()
                Toast.makeText(this, "Appareil supprimé", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun checkForUpdates(manual: Boolean = false) {
        if (manual) Toast.makeText(this, "Recherche de mises à jour...", Toast.LENGTH_SHORT).show()
        
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
                    val latestVersion = jsonObject.getString("tag_name")
                    val releaseNotes = jsonObject.optString("body", "Aucune note de version fournie.")
                    val releaseTitle = jsonObject.optString("name", latestVersion)
                    
                    val assets = jsonObject.getJSONArray("assets")
                    if (assets.length() > 0) {
                        val downloadUrl = assets.getJSONObject(0).getString("browser_download_url")

                        val latestVersionCode = latestVersion.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                        val currentVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
                        } else {
                            @Suppress("DEPRECATION")
                            packageManager.getPackageInfo(packageName, 0).versionCode
                        }

                        if (latestVersionCode > currentVersionCode) {
                            withContext(Dispatchers.Main) {
                                showUpdateDialog(releaseTitle, releaseNotes, downloadUrl)
                            }
                        } else if (manual) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@SettingsActivity, "Votre application est à jour", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (manual) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SettingsActivity, "Erreur lors de la vérification", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showUpdateDialog(title: String, notes: String, downloadUrl: String) {
        AlertDialog.Builder(this)
            .setTitle("Mise à jour : $title")
            .setMessage("Nouveautés :\n\n$notes\n\nVoulez-vous télécharger et installer cette mise à jour ?")
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
                    Toast.makeText(this@SettingsActivity, "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show()
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
}