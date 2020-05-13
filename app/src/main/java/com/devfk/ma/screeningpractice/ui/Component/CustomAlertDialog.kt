package com.devfk.ma.screeningpractice.ui.Component

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.devfk.ma.screeningpractice.R


class CustomAlertDialog(var context: Context) {
    interface AlertDialogListner {
        fun onDismissClick()
    }

    private val dialog: Dialog
    protected var yesTextBtn: TextView? = null
    protected var alert_content_text: ImageView? = null
    protected var txt_title: TextView? = null
    protected var title: String? = null
    protected var content: Int? = null
    protected var btns: String? = null

    fun setTitleandContent(
        title: String?,
        content: Int?,
        btns: String?
    ) {
        this.title = title
        this.content = content
        this.btns = btns
        updateUi()
    }

    private fun updateUi() {
        txt_title!!.text = title
        content?.let { alert_content_text!!.setImageResource(it) }
        yesTextBtn!!.text = btns
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    val isShowing: Boolean
        get() = dialog.isShowing

    private fun initViews() {
        txt_title = dialog.findViewById<View>(R.id.txt_title) as TextView
        yesTextBtn = dialog.findViewById<View>(R.id.yesTextBtn) as TextView
        alert_content_text =
            dialog.findViewById<View>(R.id.alert_content) as ImageView
    }

    companion object {
        private val listner: AlertDialogListner? = null
    }

    init {
        dialog = Dialog(context)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_alert_dialog)
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                listner?.onDismissClick()
            }
            false
        }
        dialog.show()
        initViews()
        yesTextBtn!!.setOnClickListener { dismiss() }
    }
}