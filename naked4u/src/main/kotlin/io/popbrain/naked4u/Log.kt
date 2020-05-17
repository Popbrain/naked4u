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

/**
 * Log
 * Created by garhira on 2020-02-09.
 */
data class Log(val type: LogType, val contents: String, val color: LogColor)

enum class LogType(val code: Int) {
    VERBOSE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    CUSTOM(5);

//    open var color: String = "#FFFFFF"
    companion object {
        fun get(code: Int): LogType {
            return when (code) {
                VERBOSE.code -> VERBOSE
                DEBUG.code -> DEBUG
                INFO.code -> INFO
                WARN.code -> WARN
                ERROR.code -> ERROR
                else -> CUSTOM
            }
        }
    }
}

class Color {
    companion object {
        const val Green = "#01DF01"
        const val White = "#FFFFFF"
        const val Yellow = "#FFFF00"
        const val Red = "#FF0000"
    }
}

class InnerLogger {

    companion object {

        val TAG = "InnerNaked4uLog"

        fun d(log: String) = android.util.Log.d(TAG, log)
        fun w(log: String) = android.util.Log.w(TAG, log)
        fun e(log: String) = android.util.Log.e(TAG, log)
        fun e(log: String, t: Throwable) = android.util.Log.e(TAG, log, t)
    }
}