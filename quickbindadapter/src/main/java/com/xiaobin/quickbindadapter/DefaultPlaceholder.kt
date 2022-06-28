package com.xiaobin.quickbindadapter

import com.xiaobin.quickbindadapter.databinding.LayoutPageEmptyBinding
import com.xiaobin.quickbindadapter.databinding.LayoutPageLoadingBinding

class DefaultPlaceholder :
    BasePlaceholder<LayoutPageEmptyBinding, LayoutPageEmptyBinding, LayoutPageLoadingBinding>(
        R.layout.layout_page_empty,
        R.layout.layout_page_loading,
        R.layout.layout_page_error
    ) {

    override fun onActionCall(action: PlaceholderAction) {
    }

}