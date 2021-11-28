package com.tier.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tier.ui.R
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import com.vmadalin.easypermissions.dialogs.SettingsDialog

abstract class LocationPermissionBaseActivity : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {
    companion object {
        private const val TAG = "LocationPermissionBaseActivity"
        private const val REQUEST_CODE_LOCATION_PERMISSION = 987
    }

    abstract fun onLocationPermissionGranted()

    @AfterPermissionGranted(REQUEST_CODE_LOCATION_PERMISSION)
    fun askForLocationPermission() {
        if (EasyPermissions.hasPermissions(this, ACCESS_FINE_LOCATION)) {
            onLocationPermissionGranted()
        } else {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = getString(R.string.permission_location_rationale_message),
                requestCode = REQUEST_CODE_LOCATION_PERMISSION,
                perms = arrayOf(ACCESS_FINE_LOCATION)
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsGranted()")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsDenied()")

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, "onRationaleAccepted()")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, "onRationaleDenied()")

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.without_permission_dialog_title))
            .setPositiveButton(getString(R.string.enable)) { _, _ -> askForLocationPermission() }
            .setNegativeButton(getString(R.string.exit_app)) { _, _ -> finish() }
            .show()
    }
}