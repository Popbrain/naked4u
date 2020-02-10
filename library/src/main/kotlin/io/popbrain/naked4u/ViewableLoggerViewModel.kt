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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * ViewableLoggerViewModel
 * Created by garhira on 2020-02-09.
 */
class ViewableLoggerViewModel(context: Context,
                              private val adapter: ViewableLoggerAdapter,
                              attrs: AttributeSet? = null) {

    private var logReader: BufferedReader? = null
    private var isStop = true
    private val filterWords = ArrayList<String>()
    private var filterType = LogType.VERBOSE
    private val logPatternStr =
        """^\d{2}-\d{2}\s*\d{2}:\d{2}:\d{2}.\d{3}\s*[0-9]*\s*[0-9]*\s([A-Z])\s*"""
    private val logPattern = Pattern.compile(logPatternStr)
    private var backgroundColor: String = "#000000"
    var listener: OnViewableLoggerListener? = null
    var debugColor = 0
    var infoColor = 0
    var warnColor = 0
    var errorColor = 0

    init {
        attrs?.let {
            initAttr(context.applicationContext, it)
        }
        val process = Runtime.getRuntime().exec("logcat")
        this.logReader = BufferedReader(InputStreamReader(process.getInputStream()))
    }

    fun start() {
        this.isStop = false
        logging()
    }
    fun backgroundColor(background: Drawable) {
        try {
            if (background is ColorDrawable) {
                this.backgroundColor = background.color.toColorString()
                this.adapter.setRowBackgroundColor(this.backgroundColor)
            }
        } catch (e: java.lang.Exception){
            android.util.Log.e("ViewableLogger", "toColorString()", e)
        }
    }
    fun addFilter(filter: String) = this.filterWords.add(filter)
    fun addFilter(filter: Array<String>) = this.filterWords.addAll(filter)
    fun clearFilter() = this.filterWords.clear()
    fun filterByType(type: LogType) { this.filterType = type }
    fun stop() { this.isStop = true }
    fun clear() = adapter.clear()

    private fun logging() {
        try {
            Thread {
                while (!isStop && logReader != null) {
                    val logLine = logReader?.readLine()
                    if (!logLine.isNullOrEmpty()) {
                        val logType = getLogType(logLine)
                        if (isTargetType(logType) && isContainTargetWord(logLine)) {
                            listener?.onOutput(logType, logLine)
                            runOnUIThread {
                                addLog(Log(logType, logLine))
                            }

                        }
                    }
                }
            }.start()
        } catch (e: Exception) {
            android.util.Log.e("TestApp", "logging", e)
        }
    }

    private fun addLog(log: Log) {
        this.adapter.logs.add(log)
        this.adapter.notifyItemInserted(adapter.logs.size)
    }

    private fun isTargetType(logType: LogType): Boolean {
        return (filterType == LogType.VERBOSE ||
                logType == filterType)
    }

    private fun isContainTargetWord(logLine: String): Boolean {
        if (filterWords.size == 0) return true
        for (filter in filterWords) {
            if (logLine.indexOf(filter) != -1) return true
        }
        return false
    }

    private fun getLogType(logLine: String): LogType {
        val m = logPattern.matcher(logLine)
        val debugType = LogType.DEBUG.apply {
            color = if (debugColor == 0) "#01DF01" else debugColor.toColorString()
        }
        if (m.find()) {
            val logType = m.group(1)
            return when (logType) {
                "D" -> debugType
                "I" -> LogType.INFO.apply {
                    color = if (infoColor == 0) "#FFFFFF" else infoColor.toColorString()
                }
                "W" -> LogType.WARN.apply {
                    color = if (warnColor == 0) "#FFFF00" else warnColor.toColorString()
                }
                "E" -> LogType.ERROR.apply {
                    color = if (errorColor == 0) "#FF0000" else errorColor.toColorString()
                }
                else -> debugType
            }
        }
        return debugType
    }

    private fun initAttr(context: Context, attr: AttributeSet) {
        try {
            val typedArray = context.resources.obtainAttributes(attr, R.styleable.ViewableLogger)
            // filter
            if (typedArray.hasValue(R.styleable.ViewableLogger_filter)) {
                val filterStr = typedArray.getString(R.styleable.ViewableLogger_filter)
                if (!filterStr.isNullOrEmpty()) {
                    addFilter(filterStr)
                }
            }

            // filterByType
            if (typedArray.hasValue(R.styleable.ViewableLogger_filterByType)) {
                val code = typedArray.getInt(R.styleable.ViewableLogger_filterByType, 0)
                filterByType(LogType.get(code))
            }

            // debugColor
            if (typedArray.hasValue(R.styleable.ViewableLogger_debugColor)) {
                val color = typedArray.getString(R.styleable.ViewableLogger_debugColor)
                if (!color.isNullOrEmpty()) {
                    this.debugColor = Color.parseColor(color)
                }
            }

            // infoColor
            if (typedArray.hasValue(R.styleable.ViewableLogger_infoColor)) {
                val color = typedArray.getString(R.styleable.ViewableLogger_infoColor)
                if (!color.isNullOrEmpty()) {
                    this.infoColor = Color.parseColor(color)
                }
            }

            // warnColor
            if (typedArray.hasValue(R.styleable.ViewableLogger_warnColor)) {
                val color = typedArray.getString(R.styleable.ViewableLogger_warnColor)
                if (!color.isNullOrEmpty()) {
                    this.warnColor = Color.parseColor(color)
                }
            }

            // errorColor
            if (typedArray.hasValue(R.styleable.ViewableLogger_errorColor)) {
                val color = typedArray.getString(R.styleable.ViewableLogger_errorColor)
                if (!color.isNullOrEmpty()) {
                    this.errorColor = Color.parseColor(color)
                }
            }
            typedArray.recycle()
        } catch (e: java.lang.Exception) {
        }
    }
}