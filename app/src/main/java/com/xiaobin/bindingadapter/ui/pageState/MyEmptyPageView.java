package com.xiaobin.bindingadapter.ui.pageState;

import androidx.annotation.NonNull;

import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.databinding.LayoutMyPageEmptyBinding;
import com.xiaobin.bindingadapter.databinding.LayoutMyPageErrBinding;
import com.xiaobin.bindingadapter.databinding.LayoutMyPageLoadingBinding;
import com.xiaobin.quickbindadapter.BasePageStateView;
import com.xiaobin.quickbindadapter.PageState;

/**
 * 自定义空数据占位布局
 */
public class MyEmptyPageView extends BasePageStateView<
        LayoutMyPageEmptyBinding,
        LayoutMyPageErrBinding,
        LayoutMyPageLoadingBinding> {

    public MyEmptyPageView() {
        super(R.layout.layout_my_page_empty,
                R.layout.layout_my_page_err,
                R.layout.layout_my_page_loading);
    }

    @Override
    protected void onPageCreate(@NonNull PageState action) {

    }

    @Override
    protected void onActionCall(@NonNull PageState action) {

    }
}
