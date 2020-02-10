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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewableLogger
 * Created by garhira on 2020-02-09.
 */
class ViewableLogger: RecyclerView {
    private val mViewModel: ViewableLoggerViewModel
    constructor(context: Context) : super(context) {
        this.mViewModel = ViewableLoggerViewModel(context, adapter as ViewableLoggerAdapter)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mViewModel = ViewableLoggerViewModel(context, adapter as ViewableLoggerAdapter, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mViewModel = ViewableLoggerViewModel(context, adapter as ViewableLoggerAdapter, attrs)
    }

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = ViewableLoggerAdapter(context)
    }

    fun setOnViewableLoggerListener(listener: OnViewableLoggerListener): ViewableLogger {
        this.mViewModel.listener = listener
        return this
    }

    fun addFilter(fileterStr: String): ViewableLogger {
        this.mViewModel.addFilter(fileterStr)
        return this
    }

    fun addFilter(filterList: Array<String>): ViewableLogger {
        this.mViewModel.addFilter(filterList)
        return this
    }

    fun clearFilter() = this.mViewModel.clearFilter()

    fun filterByType(logType: LogType): ViewableLogger {
        this.mViewModel.filterByType(logType)
        return this
    }

    fun setDebugColor(color: String): ViewableLogger {
        setDebugColor(Color.parseColor(color))
        return this
    }

    fun setDebugColor(color: Int): ViewableLogger {
        this.mViewModel.debugColor = color
        return this
    }

    fun setInfoColor(color: String): ViewableLogger {
        setInfoColor(Color.parseColor(color))
        return this
    }

    fun setInfoColor(color: Int): ViewableLogger {
        this.mViewModel.infoColor = color
        return this
    }

    fun setWarnColor(color: String): ViewableLogger {
        setWarnColor(Color.parseColor(color))
        return this
    }

    fun setWarnColor(color: Int): ViewableLogger {
        this.mViewModel.warnColor = color
        return this
    }

    fun setErrorColor(color: String): ViewableLogger {
        setErrorColor(Color.parseColor(color))
        return this
    }

    fun setErrorColor(color: Int): ViewableLogger {
        this.mViewModel.errorColor = color
        return this
    }

    fun start() {
        this.mViewModel.backgroundColor(background)
        this.mViewModel.start()
    }

    fun stop() {
        this.mViewModel.stop()
    }

    fun clear(): ViewableLogger {
        this.mViewModel.clear()
        return this
    }

}