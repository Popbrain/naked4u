package io.popbrain.naked4u

/**
 * Logger
 * Created by garhira on 2020-05-16.
 */
interface NakedLogger {

    fun setOnViewableLoggerListener(listener: OnViewableLoggerListener): NakedLogger

    fun addFilter(fileterStr: String): NakedLogger

    fun addFilter(filterList: Array<String>): NakedLogger

    fun addFilter(filterStr: String, color: LogColor): NakedLogger

    fun clearFilter()

    fun filterByType(logType: LogType): NakedLogger

    fun addExclusion(exclude: String): NakedLogger

    fun addExclusion(excludes: Array<String>): NakedLogger

    fun clearExclusion()

    fun clearDefaultExclusion()

    fun setDebugColor(color: String): NakedLogger

    fun setDebugColor(color: Int): NakedLogger

    fun setInfoColor(color: String): NakedLogger

    fun setInfoColor(color: Int): NakedLogger

    fun setWarnColor(color: String): NakedLogger

    fun setWarnColor(color: Int): NakedLogger

    fun setErrorColor(color: String): NakedLogger

    fun setErrorColor(color: Int): NakedLogger

    fun start()

    fun clear(): NakedLogger

    fun stop()

}