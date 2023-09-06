package com.xiaobin.quickbindadapter.holder

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.xiaobin.quickbindadapter.BindHolder
import com.xiaobin.quickbindadapter.view.BaseLoadView

class LoadViewHolder(
    val loadStateView: BaseLoadView<*>,
    binding: ViewDataBinding,
    lifecycleOwner: LifecycleOwner? = null,
) : BindHolder(binding, lifecycleOwner) {

    fun onLoading() {
        loadStateView.isLoading()
    }

    fun onWait() {
        loadStateView.isWaitLoading()
    }

    fun onSuccess() {
        loadStateView.isLoadMoreSuccess()
    }

    fun onError() {
        loadStateView.isLoadMoreFailed()
    }

    fun noMore() {
        loadStateView.isNoMoreData()
    }

}