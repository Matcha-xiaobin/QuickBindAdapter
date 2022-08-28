package com.xiaobin.quickbindadapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * @author 小斌
 * @data 2019/6/24
 */
open class BindHolder : ViewHolder {

    var binding: ViewDataBinding? = null
        private set

    constructor(
        binding: ViewDataBinding,
        lifecycleOwner: LifecycleOwner? = null
    ) : super(binding.root) {
        this.binding = binding
        if (lifecycleOwner is LifecycleOwner) {
            binding.lifecycleOwner = lifecycleOwner
        } else {
            binding.lifecycleOwner = null
        }
    }

    constructor(view: View?) : super(view!!)
}