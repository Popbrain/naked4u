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
import io.popbrain.naked4u.Color.Companion.Green
import io.popbrain.naked4u.Color.Companion.Red
import io.popbrain.naked4u.Color.Companion.White
import io.popbrain.naked4u.Color.Companion.Yellow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
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
    private val filterWords = ArrayList<Pair<String, LogColor>>()
    private val exclusionWords = ArrayList<String>()
    private val defaultExclusionWords = ArrayList<String>().apply {
        add(InnerLogger.TAG)
        add("ViewRootImpl")
        add("zygote64")
        add("OpenGLRenderer")
        add("InputMethodManager")
        add("InputEventReceiver")
        add("performLongClick() is called")
    }
    private var filterType = LogType.VERBOSE
    private val logPatternStr =
        """^\d{2}-\d{2}\s*\d{2}:\d{2}:\d{2}.\d{3}\s*[0-9]*\s*[0-9]*\s([A-Z])\s*"""
    private val logPattern = Pattern.compile(logPatternStr)
    private var defaultRowBackgroundColor = "#000000"
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
                this.defaultRowBackgroundColor = background.color.toColorString()
//                this.adapter.setRowBackgroundColor(this.defaultRowBackgroundColor)
            }
        } catch (e: java.lang.Exception){
            InnerLogger.e("toColorString()", e)
        }
    }
    fun addFilter(filter: String) = this.addFilter(filter, LogColor())
    fun addFilter(filter: String, color: LogColor) = this.filterWords.add(filter to color)
    fun addFilter(filter: Array<String>) =
        filter.forEach { this.filterWords.add(it to LogColor()) }
    fun addFilter(filter: Array<Pair<String, LogColor>>) = this.filterWords.addAll(filter)
    fun clearFilter() = this.filterWords.clear()
    fun clearDefaultExclusion() = this.defaultExclusionWords.clear()
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
                    val (isValid, log) = isValidLog(logLine)
                    if (isValid) {
                        runOnUIThread {
                            addLog(log)
                            listener?.onOutput(log.type, log.contents)
                        }
                    }
                }
            } catch (e: Exception) {
                InnerLogger.e("logging", e)
            }
        }
    }

    private fun addLog(log: Log) {
        this.adapter.addLine(log)
        this.adapter.notifyItemInserted(adapter.getLineSize())
    }

    private fun isValidLog(logLine: String?): Pair<Boolean, Log> {
        fun invalid(): Pair<Boolean, Log> = false to Log(LogType.VERBOSE, "", LogColor())
        if (logLine.isNullOrEmpty() || isContainExclusionWord(logLine) || !isValidDate(logLine))
            return invalid()
        val (isContain, customLogColor) = isContainTargetWord(logLine)
        if (!isContain) return invalid()
        val (logType, defaultLogColor) = getLogType(logLine)
        if (!isTargetType(logType)) return invalid()
        val logColor = LogColor().apply {
            val textColor = if (customLogColor.getTextColor() != 0) customLogColor.getTextColor() else defaultLogColor.getTextColor()
            setTextColor(textColor)
            val bgColor = if (customLogColor.getRowBackgroundColor() != 0) customLogColor.getRowBackgroundColor() else defaultLogColor.getRowBackgroundColor()
            setRowBackgroundColor(bgColor)
        }
        return true to Log(logType, logLine, logColor)
    }


    /**
     * Check the type of a log
     */
    private fun isTargetType(logType: LogType): Boolean {
        return (filterType == LogType.VERBOSE ||
                logType == filterType)
    }

    /**
     * Check filtering word contained.
     * Is contain target word in a log line.
     *
     * @param logLine log
     * @return Boolean : isContain / LogColor : custom color
     */
    private fun isContainTargetWord(logLine: String): Pair<Boolean, LogColor> {
        if (filterWords.size == 0) return true to LogColor()
        for (filter in filterWords) {
            if (-1 < logLine.indexOf(filter.first)) return true to filter.second
        }
        return false to LogColor()
    }

    private fun isContainExclusionWord(logLine: String): Boolean {
        defaultExclusionWords.forEach {
            if (-1 < logLine.indexOf(it)) return true
        }
        exclusionWords.forEach {
            if (-1 < logLine.indexOf(it)) return true
        }
        return false
    }

    private fun isValidDate(logLine: String): Boolean {
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
            val oldFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val logLines = logLine.split(" ")
            return oldFormat.parse("${currentYear}-${logLines[0]} ${logLines[1]}")
        } catch (e: Exception) {
            return null
        }
    }

    private fun getLogType(logLine: String): Pair<LogType, LogColor> {
        val m = logPattern.matcher(logLine)
        fun createLogColor(textColor: String): LogColor = LogColor().apply{
            setTextColor(textColor)
            setRowBackgroundColor(defaultRowBackgroundColor)
        }
        val debugResult = LogType.DEBUG to
                (if (debugColor == 0) Green else debugColor.toColorString()).run {
                    createLogColor(this)
                }
        if (m.find()) {
            val logType = m.group(1)
            return when (logType) {
                "D" -> debugResult
                "I" -> LogType.INFO to
                        (if (infoColor == 0) White else infoColor.toColorString()).run {
                            createLogColor(this)
                        }

                "W" -> LogType.WARN to
                        (if (warnColor == 0) Yellow else warnColor.toColorString()).run {
                            createLogColor(this)
                        }
                "E" -> LogType.ERROR to
                        (if (errorColor == 0) Red else errorColor.toColorString()).run {
                            createLogColor(this)
                        }
                else -> debugResult
            }
        }
        return debugResult
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
        } catch (e: Exception) {
            InnerLogger.e("initAttr", e)
        }
    }
}