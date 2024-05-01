package a.sboev.permissions

import a.sboev.permissions.databinding.ActivityMainBinding
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.tbruyelle.rxpermissions3.RxPermissions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val rxPermissions = RxPermissions(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonsLayout.isVisible = true
        binding.activityContainer.isVisible = false

        binding.buttonFragment.setOnClickListener {
            binding.fragmentContainer.isVisible = true
            binding.activityContainer.isVisible = false
            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, PermissionFragment())
                commit()
            }
        }

        binding.buttonActivity.setOnClickListener {
            binding.fragmentContainer.isVisible = false
            binding.activityContainer.isVisible = true
            binding.buttonsLayout.isVisible = false
        }

        val permissionProvided = checkSelfPermission(android.Manifest.permission.CAMERA)
        if (permissionProvided == PackageManager.PERMISSION_GRANTED) {
            // Всё хорошо, разрешение предоставлено!
            binding.permissionRequestFrame.visibility = View.GONE
            binding.permissionGranted.visibility = View.VISIBLE
        } else if (permissionProvided == PackageManager.PERMISSION_DENIED) {
            // Запрашиваем разрешение
            binding.permissionRequestFrame.visibility = View.VISIBLE
            binding.permissionGranted.visibility = View.GONE
        }
        binding.permissionRequestFrame.setOnClickListener {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != CAMERA_REQUEST_CODE) {
            Log.e("PermissionActivity", "Пришел результат не с тем requestCode, который ожидался")
            return
        }
        val cameraPermissionIndex = permissions.indexOf(android.Manifest.permission.CAMERA)
        val permissionProvided = grantResults[cameraPermissionIndex]
        if (permissionProvided == PackageManager.PERMISSION_GRANTED) {
            // Пользователь дал разрешение, можно продолжать работу
            binding.permissionRequestFrame.visibility = View.GONE
            binding.permissionGranted.visibility = View.VISIBLE
        } else if (permissionProvided == PackageManager.PERMISSION_DENIED) {
            // Пользователь отказал в предоставлении разрешения
            binding.permissionRequestFrame.visibility = View.VISIBLE
            binding.permissionGranted.visibility = View.GONE
            openAppDetailsSettings()
        }
    }

    private fun openAppDetailsSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data= Uri.fromParts("package", this.packageName, null)
        this.startActivity(intent)
    }

    companion object {
        const val CAMERA_REQUEST_CODE = 1
    }
}