package com.ziwenl.baselibrary.widgets.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Author : Ziwen Lan
 * Date : 2020/3/30
 * Time : 10:09
 * Introduction :
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder{
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void build(@NonNull T dto, int position, List<T> data);
}
