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

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Display
import android.view.Surface
import android.view.WindowManager

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