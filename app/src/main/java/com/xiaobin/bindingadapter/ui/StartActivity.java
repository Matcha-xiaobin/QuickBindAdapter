package com.xiaobin.bindingadapter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.databinding.ActivityBaseBinding;
import com.xiaobin.bindingadapter.databinding.ItemStartBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.bindingadapter.ui.grid.GridMultiActivity;
import com.xiaobin.bindingadapter.ui.grid.GridSingleActivity;
import com.xiaobin.bindingadapter.ui.linear.LinearMultiActivity;
import com.xiaobin.bindingadapter.ui.linear.LinearSingleActivity;
import com.xiaobin.bindingadapter.ui.staggered.StaggeredMultiActivity;
import com.xiaobin.bindingadapter.ui.staggered.StaggeredSingleActivity;
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QuickBindAdapter adapter = new QuickBindAdapter();
        adapter.setLoadView(adapter.getDefaultLoadItem());//设置默认的加载更多布局
        adapter.bind(String.class, R.layout.item_start, BR.data);//绑定数据类型和布局
        adapter.setQuickBind((binding, itemData, position) -> {
            //如果你想要在这里或者是在adapter中，写逻辑代码，可以这样：也可以单独写个类 实现 QuickCovert接口，然后传入这里
            // binding 是这个item本身，itemData 是这个item的数据，position 是这个item所在列表中的位置
            //如果是多布局，则需要做下判断：
            if (binding instanceof ItemStartBinding) {
                // R.layout.item_start 类型布局，在这里面写这个布局的逻辑代码
            }
            //也可以这样：
            if (itemData instanceof String) {
                // R.layout.item_start 类型布局
            }
        });
        adapter.setOnItemClickListener((adapter1, view, data, position) -> {
            //绑定item的点击事件
            Intent intent = new Intent();
            Class<?> mClass;
            switch (position) {
                case 1:
                    mClass = LinearMultiActivity.class;
                    break;
                case 2:
                    mClass = GridSingleActivity.class;
                    break;
                case 3:
                    mClass = GridMultiActivity.class;
                    break;
                case 4:
                    mClass = StaggeredSingleActivity.class;
                    break;
                case 5:
                    mClass = StaggeredMultiActivity.class;
                    break;
                case 6:
                    mClass = EmptyDemoActivity.class;
                    break;
                default:
                    mClass = LinearSingleActivity.class;
                    break;
            }
            intent.setClass(this, mClass);
            startActivity(intent);
        });
        adapter.setOnLoadMoreListener(() -> {
            Toast.makeText(this, "加载更多触发", Toast.LENGTH_SHORT).show();
            binding.getRoot().postDelayed(adapter::loadMoreEnd, 2000);
        });
        binding.recyclerView.setAdapter(adapter);

        adapter.setNewData(Arrays.asList(
                "LinearLayout单布局", "LinearLayout多布局",
                "GridLayout单布局", "GridLayout多布局",
                "StaggeredGridLayout单布局", "StaggeredGridLayout多布局",
                "空数据占位布局"));
    }
}
