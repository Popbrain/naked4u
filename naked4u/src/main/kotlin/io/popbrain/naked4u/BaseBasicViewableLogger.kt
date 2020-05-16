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
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout

/**
 * BasicViewableLogger
 * Created by garhira on 2020-05-16.
 */
abstract class BaseBasicViewableLogger: FrameLayout, NakedLogger {

    private var currentViewableLogger: ViewableLogger
    private var clearButton: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.currentViewableLogger = ViewableLogger(context, attrs, defStyleAttr).apply {
            layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        this.clearButton = ClearButton(context, attrs, defStyleAttr)
        setup(context, attrs, defStyleAttr)
    }

    override fun setOnViewableLoggerListener(listener: OnViewableLoggerListener): NakedLogger =
        currentViewableLogger.setOnViewableLoggerListener(listener)

    override fun addFilter(fileterStr: String): NakedLogger =
        currentViewableLogger.addFilter((fileterStr))

    override fun addFilter(filterList: Array<String>): NakedLogger =
        currentViewableLogger.addFilter(filterList)

    override fun clearFilter() = currentViewableLogger.clearFilter()

    override fun filterByType(logType: LogType): NakedLogger =
        currentViewableLogger.filterByType(logType)

    override fun addExclusion(exclude: String): NakedLogger =
        currentViewableLogger.addExclusion(exclude)

    override fun addExclusion(excludes: Array<String>): NakedLogger =
        currentViewableLogger.addExclusion(excludes)

    override fun clearExclusion() = currentViewableLogger.clearExclusion()

    override fun setDebugColor(color: String): NakedLogger =
        currentViewableLogger.setDebugColor(color)

    override fun setDebugColor(color: Int): NakedLogger =
        currentViewableLogger.setDebugColor(color)

    override fun setInfoColor(color: String): NakedLogger =
        currentViewableLogger.setInfoColor(color)

    override fun setInfoColor(color: Int): NakedLogger =
        currentViewableLogger.setInfoColor(color)

    override fun setWarnColor(color: String): NakedLogger =
        currentViewableLogger.setWarnColor(color)

    override fun setWarnColor(color: Int): NakedLogger =
        currentViewableLogger.setWarnColor(color)

    override fun setErrorColor(color: String): NakedLogger =
        currentViewableLogger.setErrorColor(color)

    override fun setErrorColor(color: Int): NakedLogger =
        currentViewableLogger.setErrorColor(color)

    override fun start() = currentViewableLogger.start()

    override fun clear(): NakedLogger =
        currentViewableLogger.clear()

    override fun stop() = currentViewableLogger.stop()

    private fun setup(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val displayUtil = DisplayUtil(context)
        addView(currentViewableLogger)
        addView(clearButton,
            FrameLayout.LayoutParams(displayUtil.px2Dp(ClearButton.defaultDidth), displayUtil.px2Dp(ClearButton.defaultHeight)).apply {
                gravity = Gravity.TOP or Gravity.RIGHT
                topMargin = displayUtil.px2Dp(3)
                rightMargin = displayUtil.px2Dp(5)
            })
        clearButton.setOnClickListener {
            clear()
        }
    }
}

class ClearButton: Button {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup()
    }
    companion object {
        @JvmStatic
        val defaultDidth = 40
        @JvmStatic
        val defaultHeight = 40
    }

    private fun setup() {
        text = context.getString(R.string.label_clear)
        val displayUtil = DisplayUtil(context)
        layoutParams = FrameLayout.LayoutParams(displayUtil.px2Dp(defaultDidth), displayUtil.px2Dp(defaultHeight))
        gravity = Gravity.CENTER
    }
}