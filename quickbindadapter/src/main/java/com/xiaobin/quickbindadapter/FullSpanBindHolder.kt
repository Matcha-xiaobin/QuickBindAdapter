package com.xiaobin.quickbindadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

/**
 * @author 小斌
 * @data 2019/6/24
 */
class FullSpanBindHolder : BindHolder {

    constructor(binding: ViewDataBinding, lifecycleOwner: LifecycleOwner? = null) : super(binding, lifecycleOwner)

    constructor(view: View?) : super(view!!)
}