package com.xiaobin.quickbindadapter.holder

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.xiaobin.quickbindadapter.BindHolder
import com.xiaobin.quickbindadapter.view.BasePageStateView
import com.xiaobin.quickbindadapter.view.PageState

class EmptyViewHolder : BindHolder {

    lateinit var emptyStatePageView: BasePageStateView<*, *, *>

    private constructor(view: View) : super(view)

    constructor(
        emptyStatePage: BasePageStateView<*, *, *>,
        binding: ViewDataBinding,
        lifecycleOwner: LifecycleOwner? = null
    ) : super(binding, lifecycleOwner) {
        emptyStatePageView = emptyStatePage
    }

    fun showLoadingPage() {
        emptyStatePageView.setPageState(PageState.Loading)
    }

    fun showErrorPage() {
        emptyStatePageView.setPageState(PageState.Error)
    }

    fun showEmptyPage() {
        emptyStatePageView.setPageState(PageState.Empty)
    }
}