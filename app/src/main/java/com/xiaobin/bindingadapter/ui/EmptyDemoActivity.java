package com.xiaobin.bindingadapter.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.bean.ChatListBean;
import com.xiaobin.bindingadapter.databinding.ActivityEmptyBinding;
import com.xiaobin.bindingadapter.databinding.LayoutEmptyBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindAdapter = new QuickBindAdapter();
        bindAdapter.bind(ChatListBean.class, R.layout.item_linear, BR.data);
        LayoutEmptyBinding layoutEmptyBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.layout_empty, binding.recyclerView, false);
        layoutEmptyBinding.setTitle("暂无数据");
        layoutEmptyBinding.setSubTitle("点击'加'按钮添加数据");
        bindAdapter.setEmptyView(layoutEmptyBinding.getRoot());//必须在rv.setLayoutManager之后调用
        binding.recyclerView.setAdapter(bindAdapter);
    }

    public void add(View view) {
        ChatListBean item = new ChatListBean();
        item.setId("嘿咻");
        bindAdapter.addData(item);
    }

    public void reduce(View view) {
        if (bindAdapter.getItemCount() > 0)
            bindAdapter.remove(bindAdapter.getItemCount() - 1);
    }
}
