package com.xiaobin.quickbindadapter

import android.view.View
import androidx.databinding.ViewDataBinding

interface OnItemClickListener {
    fun onClick(adapter: QuickBindAdapter, view: View, data: Any, position: Int)
}

interface OnItemLongClickListener {
    fun onLongClick(
        adapter: QuickBindAdapter,
        view: View,
        data: Any,
        position: Int
    ): Boolean
}

interface QuickBind {
    /**
     * 和 常规的adapter中的onBindViewHolder，基本相同的用法
     *
     * @param binding  databinding
     * @param itemData item数据
     */
    fun onBind(binding: ViewDataBinding, itemData: Any, position: Int)
}

interface OnLoadMoreListener {
    fun onLoadMore()
}