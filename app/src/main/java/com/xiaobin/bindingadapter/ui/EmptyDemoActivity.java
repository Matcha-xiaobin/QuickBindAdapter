package com.xiaobin.bindingadapter.ui;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.bean.ChatListBean;
import com.xiaobin.bindingadapter.databinding.ActivityEmptyBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.quickbindadapter.PlaceholderAction;
import com.xiaobin.quickbindadapter.QuickBindAdapter;

/**
 * @author 小斌
 * @data 2019/8/20
 **/
public class EmptyDemoActivity extends BaseActivity<ActivityEmptyBinding> {

    private QuickBindAdapter bindAdapter;

    @Override
    protected boolean showBackButton() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected String getActionTitle() {
        return "空数据占位布局";
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        bindAdapter = new QuickBindAdapter();
        bindAdapter.bind(ChatListBean.class, R.layout.item_linear, BR.data);
        bindAdapter.setEmptyView(bindAdapter.getDefaultPlaceholder());//设置默认的无数据时占位布局
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(bindAdapter);
    }

    public void addData(View view) {
        ChatListBean item = new ChatListBean();
        item.setId("嘿咻");
        bindAdapter.addData(item);
    }

    public void reduceData(View view) {
        if (bindAdapter.getItemCount() > 0)
            bindAdapter.remove(bindAdapter.getItemCount() - 1);
    }

    public void loadPage(View view) {
        if (bindAdapter.getEmptyView() != null) {
            bindAdapter.removeAll();
            bindAdapter.getEmptyView().setPlaceholderAction(PlaceholderAction.ShowLoadingPage.INSTANCE);
        }
    }

    public void errPage(View view) {
        if (bindAdapter.getEmptyView() != null) {
            bindAdapter.removeAll();
            bindAdapter.getEmptyView().setPlaceholderAction(PlaceholderAction.ShowErrPage.INSTANCE);
        }
    }
}
