package com.atocash.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.atocash.R
import com.atocash.utils.AppHelper
import com.atocash.utils.DataStorage
import com.atocash.utils.Keys
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.log

class UserProfileDialog(
    private val dataStorage: DataStorage,
    private val callback: ProfileDialogCallback
) :
    BottomSheetDialogFragment() {

    lateinit var helpLayout: LinearLayout
    lateinit var logoutLayout: LinearLayout

    lateinit var nameTv: TextView
    lateinit var emailTv: TextView
    lateinit var profileIv: ImageView

    public interface ProfileDialogCallback {
        fun onHelp()
        fun onLogout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_user_profile, null)

        helpLayout = view.findViewById(R.id.help_layout)
        logoutLayout = view.findViewById(R.id.logout_layout)

        nameTv = view.findViewById(R.id.user_name_tv)
        emailTv = view.findViewById(R.id.email_tv)
        profileIv = view.findViewById(R.id.user_profile_iv)

        nameTv.text = dataStorage.getString(Keys.UserData.FIRST_NAME)
        emailTv.text = dataStorage.getString(Keys.UserData.EMAIL)

        AppHelper.printLog("email ${dataStorage.getString(Keys.UserData.EMAIL)}")

        helpLayout.setOnClickListener {
            dismiss()
            callback.onHelp()
        }

        logoutLayout.setOnClickListener {
            dismiss()
            callback.onLogout()
        }

        dialog.setContentView(view)
        dialog.setCancelable(true)

        return dialog
    }
}