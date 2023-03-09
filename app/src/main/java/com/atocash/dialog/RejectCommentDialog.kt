package com.atocash.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.atocash.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RejectCommentDialog(context: Context,private val choiceDialogCallback: RejectCommentDialogCallback) :
    Dialog(context, R.style.CustomDialogAnim) {

    private val nextBtn: AppCompatButton
    private val logoutContentTv: TextView

    private val commentEd: TextInputEditText
    private val commentLayout: TextInputLayout

    interface RejectCommentDialogCallback {
        fun onNext(rejectComment: String)
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_reject_comment, null)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (window != null) window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        nextBtn = view.findViewById(R.id.next_btn)
        logoutContentTv = view.findViewById(R.id.logoutContent)

        commentEd = view.findViewById(R.id.comment_ed)
        commentLayout = view.findViewById(R.id.comment_layout)

        commentEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotEmpty()) {
                    commentLayout.error = ""
                }
            }
        })

        nextBtn.setOnClickListener { v: View? ->
            checkAndClose()
        }
        setContentView(view)
        setCancelable(true)
    }

    private fun checkAndClose() {
        val comment = commentEd.text.toString().trim()

        if(TextUtils.isEmpty(comment)) {
            commentLayout.error = "Enter comments!"
            return
        }

        dismiss()
        choiceDialogCallback.onNext(comment)
    }
}
