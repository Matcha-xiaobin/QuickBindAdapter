package com.xiaobin.quickbindadapter

import androidx.annotation.ColorInt
import com.xiaobin.quickbindadapter.databinding.LayoutPageEmptyBinding
import com.xiaobin.quickbindadapter.databinding.LayoutPageErrorBinding
import com.xiaobin.quickbindadapter.databinding.LayoutPageLoadingBinding

/**
 * 默认的加载更多布局
 */
class DefaultPlaceholder :
    BasePlaceholder<LayoutPageEmptyBinding, LayoutPageErrorBinding, LayoutPageLoadingBinding>(
        R.layout.layout_page_empty,
        R.layout.layout_page_error,
        R.layout.layout_page_loading,
    ) {

    companion object {
        val defaultPlaceholder: DefaultPlaceholder by lazy {
            DefaultPlaceholder()
        }
    }

    override fun onActionCall(action: PlaceholderAction) {
    }

    fun setErrorPageTextColor(@ColorInt textColor: Int) {
        errorView?.apply {
            tvError.setTextColor(textColor)
        }
    }

    fun setErrorPageText(text: String) {
        errorView?.apply {
            tvError.text = text
        }
    }

    fun setEmptyPageTextColor(@ColorInt textColor: Int) {
        emptyView?.apply {
            tvEmpty.setTextColor(textColor)
        }
    }

    fun setEmptyPageText(text: String) {
        emptyView?.apply {
            tvEmpty.text = text
        }
    }

}