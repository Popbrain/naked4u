/**
 * Copyright (C) 2020 Popbrain aka Garhira.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.popbrain.naked4u

import android.app.Dialog
import android.content.*
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast

/**
 * utils
 * Created by garhira on 2020-02-09.
 */
fun isUIThread(): Boolean = Looper.getMainLooper().thread == Thread.currentThread()

fun runOnUIThread(action: () -> Unit) {
    if (!isUIThread()) {
        Handler(Looper.getMainLooper()).post {
            action.invoke()
        }
    } else {
        action.invoke()
    }
}

fun Int.toColorString(): String {
    return java.lang.String.format("#%06X", 0xFFFFFF and this)
}

class DisplayUtil(context: Context) {
    private val mDisplay: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).run {
        defaultDisplay
    }

    val screenSize: Bound = screenSize()
    val density: Float = density()
    fun px2Dp(px: Int): Int = (px * density).toInt()

    private fun screenSize(): Bound = with(Point().apply {
        mDisplay.getSize(this)
    }) {
        Bound(x.toFloat(), y.toFloat(), mDisplay.rotation)
    }

    private fun density(): Float = DisplayMetrics().apply {
        mDisplay.getMetrics(this)
    }.density

    open class Bound(val width: Float, val height: Float, val rotation: Int) {
        val orientation: Int = when(rotation) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> 1
            else -> 0
        }
    }
}

fun Context.copyToClipboard(label: String = "naked4u_copy_log", text: String) {
    try {
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)?.let {
            it.setPrimaryClip(ClipData.newPlainText(label, text))
        }
    } catch (e: Exception) {
        InnerLogger.e("Copy to clipboard.", e)
    }
}

fun Dialog.sendToSlack(message: String) {
    fun getComponent(`package`: String): ComponentName? {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
        }
        val resolveInfoList = context.packageManager.queryIntentActivities(shareIntent, 0)
        resolveInfoList.forEach {
            val packageName = it.activityInfo.packageName
            if (packageName.equals(`package`)) {
                    return ComponentName(packageName, it.activityInfo.name)
            }
        }
        return null
    }
    var newMessage = message
    if (500 < message.length) {
        newMessage = message.substring(0, 499)
        Toast.makeText(context, R.string.warn_over_500_length, Toast.LENGTH_SHORT).show()
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        val targetPackage = context.getString(R.string.package_slack)
        getComponent(targetPackage)?.let {
            component = it
        }?: run {
            `package` = targetPackage
        }
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Logs")
        putExtra(Intent.EXTRA_TEXT, newMessage)
//        putExtra(Intent.EXTRA_STREAM,"")
    }
    context.startActivity(intent)
}