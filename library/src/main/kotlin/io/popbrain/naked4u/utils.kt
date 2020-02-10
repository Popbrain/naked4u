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

import android.os.Handler
import android.os.Looper

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