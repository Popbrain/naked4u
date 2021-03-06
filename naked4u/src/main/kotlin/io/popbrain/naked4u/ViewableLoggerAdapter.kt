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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewableLoggerAdapter
 * Created by garhira on 2020-02-09.
 */
class ViewableLoggerAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val inflater = LayoutInflater.from(context)
    private val logs = ArrayList<Log>()
    private var listener: Listener? = null

    fun addLine(log: Log) = logs.add(log)
    fun getLineSize(): Int = logs.size

    fun clear() {
        val size = itemCount
        logs.clear()
        notifyItemRangeRemoved(0, size)
    }

    interface Listener {
        fun onClick(position: Int, log: Log, view: View)
        fun onLongClick(position: Int, log: Log, view: View): Boolean
    }

    fun setListener(l: Listener) {
        this.listener = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LogViewHolder(
            inflater.inflate(getLayout(viewType), parent, false)
        )
    }

    override fun getItemCount(): Int = logs.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LogViewHolder -> {
                holder.log.setText(logs[position].contents)
                holder.log.setTextColor(logs[position].color.getTextColor())
                holder.log.setBackgroundColor(logs[position].color.getRowBackgroundColor())
                holder.log.setOnClickListener { view ->
                    listener?.onClick(position, logs[position], view)
                }
                holder.log.setOnLongClickListener { view ->
                    listener?.let { l ->
                        l.onLongClick(position, logs[position], view)
                    }?: true
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (logs[position].type) {
            LogType.DEBUG -> LogType.DEBUG.code
            LogType.INFO -> LogType.INFO.code
            LogType.WARN -> LogType.WARN.code
            LogType.ERROR -> LogType.ERROR.code
            else -> LogType.VERBOSE.code
        }
    }

    private fun getLayout(viewType: Int): Int {
        return when (viewType) {
            LogType.DEBUG.code -> R.layout.log_d_row
            LogType.INFO.code -> R.layout.log_i_row
            LogType.WARN.code -> R.layout.log_w_row
            LogType.ERROR.code -> R.layout.log_e_row
            else -> R.layout.log_v_row
        }
    }

    open class LogViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val log = v.findViewById<TextView>(R.id.log)
    }
}