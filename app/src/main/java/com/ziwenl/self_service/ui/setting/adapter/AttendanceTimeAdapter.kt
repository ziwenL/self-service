package com.ziwenl.self_service.ui.setting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ziwenl.baselibrary.widgets.recycler.BaseViewHolder
import com.ziwenl.self_service.R
import com.ziwenl.self_service.bean.AttendanceDto
import kotlinx.android.synthetic.main.setting_attendance_item.view.*

/**
 * PackageName : com.ziwenl.self_service.ui.setting.adapter
 * Author : Ziwen Lan
 * Date : 2020/7/20
 * Time : 11:42
 * Introduction :
 */
class AttendanceTimeAdapter(
    var data: MutableList<AttendanceDto>,
    private val onLongClickListener: View.OnLongClickListener
) :
    RecyclerView.Adapter<BaseViewHolder<AttendanceDto>>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<AttendanceDto> {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.setting_attendance_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AttendanceDto>, position: Int) {
        holder.build(data.get(position), position, data)
    }

    inner class ViewHolder(view: View) : BaseViewHolder<AttendanceDto>(view) {
        override fun build(dto: AttendanceDto, position: Int, data: MutableList<AttendanceDto>?) {
            itemView.tv_time.text = dto.time
            itemView.tv_punch.text = if (dto.isPunchIn) "上班卡" else "下班卡"
            itemView.setTag(position)
            itemView.setOnLongClickListener(onLongClickListener)
        }
    }
}