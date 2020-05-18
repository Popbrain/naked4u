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
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewableLogger
 * Created by garhira on 2020-02-09.
 */
class ViewableLogger: RecyclerView, NakedLogger, View.OnAttachStateChangeListener,
    OnViewableLoggerListener, ViewableLoggerAdapter.Listener {
    private val mViewModel: ViewableLoggerViewModel
    private var clientListener: OnViewableLoggerListener? = null
    private var popupEnable = true

    constructor(context: Context) : super(context) {
        setup()
        this.mViewModel = ViewableLoggerViewModel(context, adapter as ViewableLoggerAdapter).apply {
            listener = this@ViewableLogger
        }
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup()
        this.mViewModel = ViewableLoggerViewModel(context, adapter as ViewableLoggerAdapter, attrs).apply {
            listener = this@ViewableLogger
        }
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup()
        this.mViewModel = ViewableLoggerViewModel(context, adapter as ViewableLoggerAdapter, attrs).apply {
            listener = this@ViewableLogger
        }
    }

    private fun setup() {
        this.adapter = ViewableLoggerAdapter(context).apply {
            setListener(this@ViewableLogger)
        }
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).apply {
            stackFromEnd = true
            isVerticalScrollBarEnabled = true
//            isSmoothScrollbarEnabled = true
            isScrollbarFadingEnabled = false
//            setHasFixedSize(true)
        }
        addOnAttachStateChangeListener(this)
        isVerticalScrollBarEnabled = true
        isScrollbarFadingEnabled = false
//        setHasFixedSize(true)
    }

    override fun setOnViewableLoggerListener(listener: OnViewableLoggerListener): ViewableLogger {
        this.clientListener = listener
        return this
    }

    override fun addFilter(fileterStr: String): ViewableLogger {
        this.mViewModel.addFilter(fileterStr)
        return this
    }

    override fun addFilter(filterStr: String, color: LogColor): ViewableLogger {
        this.mViewModel.addFilter(filterStr, color)
        return this
    }

    override fun addFilter(filterList: Array<String>): ViewableLogger {
        this.mViewModel.addFilter(filterList)
        return this
    }

    override fun clearFilter() = this.mViewModel.clearFilter()

    override fun filterByType(logType: LogType): ViewableLogger {
        this.mViewModel.filterByType(logType)
        return this
    }

    override fun addExclusion(exclude: String): NakedLogger {
        this.mViewModel.addExclusion(exclude)
        return this
    }

    override fun addExclusion(excludes: Array<String>): NakedLogger {
        this.mViewModel.addExclusion(excludes)
        return this
    }

    override fun clearExclusion() = this.mViewModel.clearExclusion()

    override fun clearDefaultExclusion() = this.mViewModel.clearDefaultExclusion()

    override fun setDebugColor(color: String): ViewableLogger {
        setDebugColor(Color.parseColor(color))
        return this
    }

    override fun setDebugColor(color: Int): ViewableLogger {
        this.mViewModel.debugColor = color
        return this
    }

    override fun setInfoColor(color: String): ViewableLogger {
        setInfoColor(Color.parseColor(color))
        return this
    }

    override fun setInfoColor(color: Int): ViewableLogger {
        this.mViewModel.infoColor = color
        return this
    }

    override fun setWarnColor(color: String): ViewableLogger {
        setWarnColor(Color.parseColor(color))
        return this
    }

    override fun setWarnColor(color: Int): ViewableLogger {
        this.mViewModel.warnColor = color
        return this
    }

    override fun setErrorColor(color: String): ViewableLogger {
        setErrorColor(Color.parseColor(color))
        return this
    }

    override fun setErrorColor(color: Int): ViewableLogger {
        this.mViewModel.errorColor = color
        return this
    }

    override fun start() {
        this.mViewModel.backgroundColor(background)
        this.mViewModel.start()
    }

    override fun stop() {
        this.mViewModel.stop()
    }

    override fun clear(): ViewableLogger {
        this.mViewModel.clear()
        return this
    }

    override fun onViewDetachedFromWindow(v: View?) {
        this.mViewModel.finish()
        removeOnAttachStateChangeListener(this)
    }

    override fun onViewAttachedToWindow(v: View?) {
        InnerLogger.d("onViewAttachedToWindow")
    }

    override fun onOutput(type: LogType, logLine: String) {
        adapter?.let {
            InnerLogger.d("Log count : ${it.itemCount}")
            if (0 < it.itemCount) smoothScrollToPosition(it.itemCount + 1)
        }
        this.clientListener?.onOutput(type, logLine)
    }

    override fun popupEnable(enable: Boolean): NakedLogger {
        this.popupEnable = enable
        return this
    }

    override fun onClick(position: Int, log: Log, view: View) {
        NakedDialog(context, log).show()
    }

    override fun onLongClick(position: Int, log: Log, view: View): Boolean {
        context.copyToClipboard(text = log.contents)
        // TODO Customize toast
        Toast.makeText(context, context.getString(R.string.toast_copy), Toast.LENGTH_SHORT)
            .show()
        return true
    }
}