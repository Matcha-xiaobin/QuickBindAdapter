package com.xiaobin.bindingadapter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.bean.ChatListBean;
import com.xiaobin.bindingadapter.databinding.ActivityEmptyBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.bindingadapter.ui.pageState.MyEmptyPageView;
import com.xiaobin.quickbindadapter.DefaultEmptyStatePage;
import com.xiaobin.quickbindadapter.PageState;
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
        //修改默认初始显示无数据， 不修改则是加载中
        DefaultEmptyStatePage defaultEmptyStatePage = new DefaultEmptyStatePage(this);
        defaultEmptyStatePage.setDefaultPage(PageState.Empty);
        bindAdapter = QuickBindAdapter.Companion.create()
                .bind(ChatListBean.class, R.layout.item_linear, BR.data)
                .setEmptyView(defaultEmptyStatePage);//设置默认的无数据时占位布局

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(bindAdapter);
    }

    public void addData(View view) {
        ChatListBean item = new ChatListBean();
        item.setId("嘿咻");
        bindAdapter.addData(item, true);
    }

    public void reduceData(View view) {
        if (bindAdapter.getItemCount() > 0)
            bindAdapter.remove(bindAdapter.getItemCount() - 1);
    }

    public void loadPage(View view) {
        bindAdapter.showLoadPage(true);
    }

    public void errPage(View view) {
        bindAdapter.showErrorPage(true);
    }

    public void myPageState(View view) {
        Toast.makeText(this, "使用自定义布局", Toast.LENGTH_SHORT).show();
        bindAdapter.setEmptyView(new MyEmptyPageView());
        //在上面initView中，已经设置了默认的布局，所以这里可以重新设置一下adapter达到这个布局的目的
        binding.recyclerView.setAdapter(bindAdapter);
    }

    public void defaultPageState(View view) {
        Toast.makeText(this, "使用默认布局", Toast.LENGTH_SHORT).show();
        bindAdapter.setEmptyView(new DefaultEmptyStatePage(this));
        //在上面initView中，已经设置了默认的布局，所以这里可以重新设置一下adapter达到这个布局的目的
        binding.recyclerView.setAdapter(bindAdapter);
    }
}
