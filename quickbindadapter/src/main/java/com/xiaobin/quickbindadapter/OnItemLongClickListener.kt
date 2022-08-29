package com.xiaobin.quickbindadapter

import android.view.View

interface OnItemLongClickListener {
    fun onLongClick(
        adapter: QuickBindAdapter,
        view: View,
        data: Any,
        position: Int
    ): Boolean
}