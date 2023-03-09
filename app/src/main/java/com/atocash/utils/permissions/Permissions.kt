package com.atocash.utils.permissions

import android.Manifest

/**
 * Created by geniuS on 7/10/2019.
 */
object Permissions {
    private const val appName = "Atocash"

    const val READ_GALLERY_CAMERA_STRING =
        "$appName needs to use camera and storage permission to take pictures and pick images, allow permission in settings."
    const val READ_STORAGE_STRING =
        "$appName needs to use storage permission to access storage, allow permission in settings."

    const val CANNOT_OPEN_CAMERA_GALLERY = "Sorry, cannot open camera without permission!"
    const val CANNOT_GALLERY = "Sorry, cannot open gallery without permission!"
    const val CANNOT_STORAGE = "Sorry, cannot access storage without permission!"

    var READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    var WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    var CAMERA = Manifest.permission.CAMERA

    val storagePermission: Array<String>
        get() = arrayOf(
            READ_STORAGE,
            WRITE_STORAGE
        )

    val camAndStoragePermission: Array<String>
        get() = arrayOf(
            READ_STORAGE,
            WRITE_STORAGE,
            CAMERA
        )
}