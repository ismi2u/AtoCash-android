package com.atocash.utils.permissions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

/**
 * Created by geniuS on 7/10/2019.
 */
class PermissionsHelper {
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private lateinit var permissions: Array<String>
    private var mPermissionCallback: PermissionCallback? = null
    private var showRational = false

    constructor(activity: Activity?) {
        this.activity = activity
    }

    constructor(fragment: Fragment?) {
        this.fragment = fragment
    }

    fun initPermissions(permissions: Array<String>) {
        this.permissions = permissions
        checkIfPermissionPresentInAndroidManifest()
    }

    private fun checkIfPermissionPresentInAndroidManifest() {
        for (permission in permissions) {
            if (!hasPermissionInManifest(permission)) {
                throw RuntimeException("Permission ($permission) not declared in manifest")
            }
        }
    }

    fun requestPermission(permissionCallback: PermissionCallback?) {
        mPermissionCallback = permissionCallback
        if (!checkPermission(permissions)) {
            showRational = shouldShowRationalDialog(permissions)
            if (activity != null) ActivityCompat.requestPermissions(
                activity!!,
                getUnGrantedPermissions(permissions),
                REQUEST_CODE
            ) else fragment!!.requestPermissions(
                getUnGrantedPermissions(permissions),
                REQUEST_CODE
            )
        } else {
            printInfo("Permission Granted")
            if (mPermissionCallback != null) mPermissionCallback!!.onGranted()
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) { // AppHelper.printLog("checking in onRequestPermissionsResult");
            var isDenied = false
            var i = 0
            val grantedPermissions =
                ArrayList<String>()
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isDenied = true
                } else {
                    grantedPermissions.add(permissions[i])
                }
                i++
            }
            if (isDenied) {
                val currentShowRational = shouldShowRationalDialog(permissions)
                if (!showRational && !currentShowRational) {
                    printInfo("Permission Denied By System")
                    if (mPermissionCallback != null) mPermissionCallback!!.onDeniedCompletely()
                } else {
                    printInfo("Permission Denied")
                    //Checking if any single individual permission is granted then show user that permission
                    if (!grantedPermissions.isEmpty()) {
                        if (mPermissionCallback != null) mPermissionCallback!!.onSinglePermissionGranted(
                            grantedPermissions.toTypedArray()
                        )
                    }
                    if (mPermissionCallback != null) {
                        mPermissionCallback!!.onDenied()
                    }
                }
            } else {
                printInfo("Permission Granted")
                if (mPermissionCallback != null) mPermissionCallback!!.onGranted()
            }
        }
    }

    interface PermissionCallback {
        fun onGranted()
        fun onSinglePermissionGranted(grantedPermission: Array<String>?)
        fun onDenied()
        fun onDeniedCompletely()
    }

    private fun <T : Context?> getContext(): T? {
        return if (activity != null) activity as T else fragment!!.context as T?
    }

    //Return list that is not granted and we need to ask for permission
    private fun getUnGrantedPermissions(permissions: Array<String>): Array<String> {
        val notGrantedPermission: MutableList<String> =
            ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(getContext<Context>()!!,permission) != PackageManager.PERMISSION_GRANTED
            ) {
                notGrantedPermission.add(permission)
            }
        }
        return notGrantedPermission.toTypedArray()
    }

    //Check permission is there or not for group of permissions
    fun checkPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    getContext<Context>()!!,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    //Checking if there is need to show rational for group of permissions
    private fun shouldShowRationalDialog(permissions: Array<String>): Boolean {
        var currentShowRational = false
        for (permission in permissions) {
            if (activity != null) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                    currentShowRational = true
                    break
                }
            } else {
                if (fragment!!.shouldShowRequestPermissionRationale(permission)) {
                    currentShowRational = true
                    break
                }
            }
        }
        return currentShowRational
    }

    private fun hasPermissionInManifest(permission: String): Boolean {
        try {
            val context: Context =
                (if (activity != null) activity else fragment!!.activity)!!
            val info = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            if (info.requestedPermissions != null) {
                for (p in info.requestedPermissions) {
                    if (p == permission) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun askAndOpenSettings(toShowMsg: String?, cancelMsg: String?) {
        if (getContext<Context?>() == null) {
            return
        }
        val alertDialog =
            AlertDialog.Builder(getContext()).create()
        alertDialog.setMessage(toShowMsg)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "OK"
        ) { dialog, which ->
            Toast.makeText(
                getContext(),
                "Grant permissions in settings",
                Toast.LENGTH_LONG
            ).show()
            getContext<Context>()!!.startActivity(
                Intent()
                    .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .setData(Uri.parse("package:" + getContext<Context>()!!.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            )
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel"
        ) { dialog, which ->
            Toast.makeText(
                getContext(),
                cancelMsg,
                Toast.LENGTH_LONG
            ).show()
        }
        if (!alertDialog.isShowing) alertDialog.show()
    }

    private fun printInfo(s: String) { //AppHelper.printLog(s);
    }

    val isRTPermissionRequired: Boolean
        get() = Build.VERSION.SDK_INT >= 23

    val isCameraAndStoragePermissionsAvailable: Boolean
        get() = (checkPermission(arrayOf(Permissions.READ_STORAGE))
                && checkPermission(arrayOf(Permissions.WRITE_STORAGE))
                && checkPermission(arrayOf(Permissions.CAMERA)))

    val isStoragePermissionsAvailable: Boolean
        get() = (checkPermission(arrayOf(Permissions.READ_STORAGE))
                && checkPermission(arrayOf(Permissions.WRITE_STORAGE)))


    fun initCameraAndStoragePermissions() {
        initPermissions(
            arrayOf(
                Permissions.READ_STORAGE,
                Permissions.WRITE_STORAGE,
                Permissions.CAMERA
            )
        )
        printInfo("storage and camera init permission")
    }

    fun initStoragePermissions() {
        initPermissions(
            arrayOf(
                Permissions.READ_STORAGE,
                Permissions.WRITE_STORAGE
            )
        )
        printInfo("storage and camera init permission")
    }

    companion object {
        private const val TAG = "PermissionsHelper"
        private const val REQUEST_CODE = 999
    }
}