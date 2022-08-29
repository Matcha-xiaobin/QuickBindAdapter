package com.xiaobin.quickbindadapter

import android.view.View

interface OnItemClickListener {
    fun onClick(adapter: QuickBindAdapter, view: View, data: Any, position: Int)
}