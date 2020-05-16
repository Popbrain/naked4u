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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * ViewableLoggerViewModel
 * Created by garhira on 2020-02-09.
 */
class ViewableLoggerViewModel(context: Context,
                              private val adapter: ViewableLoggerAdapter,
                              attrs: AttributeSet? = null) {

    private var logReader: BufferedReader? = null
    private var done = true
    private val filterWords = ArrayList<String>()
    private val exclusionWords = ArrayList<String>()
    private val defaultExclusionWords = ArrayList<String>().apply {
        add(InnerLogger.TAG)
        add("ViewRootImpl")
        add("zygote64")
    }
    private var filterType = LogType.VERBOSE
    private val logPatternStr =
        """^\d{2}-\d{2}\s*\d{2}:\d{2}:\d{2}.\d{3}\s*[0-9]*\s*[0-9]*\s([A-Z])\s*"""
    private val logPattern = Pattern.compile(logPatternStr)
    private var backgroundColor: String = "#000000"
    private var thread: Thread? = null
    var listener: OnViewableLoggerListener? = null
    var debugColor = 0
    var infoColor = 0
    var warnColor = 0
    var errorColor = 0
    private var startTime = Date(System.currentTimeMillis())

    init {
        attrs?.let {
            initAttr(context.applicationContext, it)
        }
    }

    fun start() {
        this.done = false
        startTime = Date(System.currentTimeMillis())
        logging()
    }
    fun backgroundColor(background: Drawable) {
        try {
            if (background is ColorDrawable) {
                this.backgroundColor = background.color.toColorString()
                this.adapter.setRowBackgroundColor(this.backgroundColor)
            }
        } catch (e: java.lang.Exception){
            InnerLogger.e("toColorString()", e)
        }
    }
    fun addFilter(filter: String) = this.filterWords.add(filter)
    fun addFilter(filter: Array<String>) = this.filterWords.addAll(filter)
    fun clearFilter() = this.filterWords.clear()
    fun addExclusion(exclude: String) = this.exclusionWords.add(exclude)
    fun addExclusion(excludes: Array<String>) = this.exclusionWords.addAll(excludes)
    fun clearExclusion() = this.exclusionWords.clear()
    fun filterByType(type: LogType) { this.filterType = type }
    fun stop() { this.done = true }
    fun finish() {
        this.logReader = null
        this.thread = null
    }
    fun clear() {
        adapter.clear()
        startTime = Date(System.currentTimeMillis())
    }

    private fun getLogReader(): BufferedReader = with(Runtime.getRuntime().exec("logcat")) {
        BufferedReader(InputStreamReader(getInputStream()))
    }

    private fun logging() {
        this.logReader = getLogReader()
        this.thread = Thread(runnable()).apply {
            start()
        }
    }

    private fun runnable(): Runnable {
        return Runnable {
            try {
                while (!done && logReader != null) {
                    val logLine = logReader?.readLine()
                    if (!logLine.isNullOrEmpty()) {
                        if (!isValidLog(logLine)) continue
                        val logType = getLogType(logLine)
                        if (isTargetType(logType) && isContainTargetWord(logLine)) {
                            listener?.onOutput(logType, logLine)
                            runOnUIThread {
                                addLog(Log(logType, logLine))
                            }

                        }
                    }
                }
            } catch (e: Exception) {
                InnerLogger.e("logging", e)
            }
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
            if (-1 < logLine.indexOf(filter)) return true
        }
        return false
    }

    private fun isValidLog(logLine: String): Boolean {
        defaultExclusionWords.forEach {
            if (-1 < logLine.indexOf(it)) return false
        }
        exclusionWords.forEach {
            if (-1 < logLine.indexOf(it)) return false
        }
        getDate(logLine)?.let { logTime ->
            return startTime.before(logTime)
        }
        return true
    }

    private fun getDate(logLine: String): Date? {
        try {
            logLine.substring(0, 1).toInt()
        } catch (e: Exception) {
            return null
        }
        try {
            val currentYear = Calendar.getInstance().apply {
                time = startTime
            }.get(Calendar.YEAR)
            val oldFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
            val logLines = logLine.split(" ")
            return oldFormat.parse("${currentYear}-${logLines[0]} ${logLines[1]}")
        } catch (e: Exception) {
            return null
        }
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