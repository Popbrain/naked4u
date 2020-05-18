package io.popbrain.naked4u

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/**
 * NakedDialog
 * Created by garhira on 2020-05-18.
 */
class NakedDialog(
    context: Context,
    private val log: Log
) : Dialog(context) {

    init {
        window?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setContentView(R.layout.naked_dialog)
        findViewById<LinearLayout>(R.id.container).apply {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                background.colorFilter =
                    PorterDuffColorFilter(log.color.getRowBackgroundColor(), PorterDuff.Mode.SRC_IN)
            } else {
                background.setColorFilter(log.color.getRowBackgroundColor(), PorterDuff.Mode.SRC_IN)
            }
        }
        findViewById<TextView>(R.id.log).apply {
            setText(log.contents)
            setTextColor(log.color.getTextColor())
//            setBackgroundColor(log.color.getRowBackgroundColor())
        }
        findViewById<View>(R.id.copy_btn).setOnClickListener {
            context.copyToClipboard(text = log.contents)
            // TODO Customize toast
            Toast.makeText(context, context.getString(R.string.toast_copy), Toast.LENGTH_SHORT)
                .show()
            dismiss()
        }
        findViewById<View>(R.id.close_btn).setOnClickListener {
            dismiss()
        }
    }

    override fun show() {
        super.show()
        this.window?.let {
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}