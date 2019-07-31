package com.xiaobin.bindingadapter.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.databinding.ActivityBaseBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.bindingadapter.ui.grid.GridMultiActivity;
import com.xiaobin.bindingadapter.ui.grid.GridSingleActivity;
import com.xiaobin.bindingadapter.ui.linear.LinearMultiActivity;
import com.xiaobin.bindingadapter.ui.linear.LinearSingleActivity;
import com.xiaobin.quickbindadapter.QuickBindAdapter;

import java.util.Arrays;

/**
 * @author 小斌
 * @data 2019/7/31
 **/
public class StartActivity extends BaseActivity<ActivityBaseBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        QuickBindAdapter adapter = new QuickBindAdapter();
        //绑定数据类型和布局
        adapter.bind(String.class, R.layout.item_start, BR.data);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        //绑定item的点击事件
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            //item点击事件
            Intent intent = new Intent();
            switch (position) {
                case 0:
                    intent.setClass(this, LinearSingleActivity.class);
                    break;
                case 1:
                    intent.setClass(this, LinearMultiActivity.class);
                    break;
                case 2:
                    intent.setClass(this, GridSingleActivity.class);
                    break;
                case 3:
                    intent.setClass(this, GridMultiActivity.class);
                    break;
                default:
                    intent.setClass(this, LinearSingleActivity.class);
                    break;
            }
            startActivity(intent);
        });

        adapter.setNewData(Arrays.asList("LinearLayout单布局", "LinearLayout多布局", "GridLayout单布局", "GridLayout多布局"));
    }
}
