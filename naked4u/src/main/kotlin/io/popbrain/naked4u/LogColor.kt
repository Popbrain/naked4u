package io.popbrain.naked4u

import android.graphics.Color
import org.jetbrains.annotations.NotNull

/**
 * LogColor
 * Created by garhira on 2020-05-17.
 */
class LogColor {

    private var textColor: Int = 0
    private var backgroundColor: Int = 0

    /**
     * @param color : e.g #FFFFFF,#000000..
     */
    fun setTextColor(@NotNull color: String) {
        this.textColor = Color.parseColor(color)
    }

    fun setTextColor(@NotNull color: Int) {
        this.textColor = color
    }

    fun getTextColor(): Int = this.textColor

    fun setRowBackgroundColor(@NotNull color: String) {
        this.backgroundColor = Color.parseColor(color)
    }

    fun setRowBackgroundColor(@NotNull color: Int) {
        this.backgroundColor = color
    }

    fun getRowBackgroundColor(): Int = this.backgroundColor

}