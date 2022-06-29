package com.xiaobin.quickbindadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

/**
 * @author 小斌
 * @data 2019/6/24
 */
open class BindHolder : ViewHolder {

    var binding: ViewDataBinding? = null
        private set

    constructor(binding: ViewDataBinding, lifecycleOwner: LifecycleOwner? = null) : super(binding.root) {
        this.binding = binding
        lifecycleOwner?.apply {
            binding.lifecycleOwner = this
        }
    }

    constructor(view: View?) : super(view!!)
}