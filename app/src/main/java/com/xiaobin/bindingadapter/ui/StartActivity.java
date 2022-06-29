package com.xiaobin.bindingadapter.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

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
import com.xiaobin.quickbindadapter.DefaultLoadView;
import com.xiaobin.quickbindadapter.DefaultLoadViewConfigsBean;
import com.xiaobin.quickbindadapter.DefaultPlaceholder;
import com.xiaobin.quickbindadapter.DefaultPlaceholderConfigsBean;
import com.xiaobin.quickbindadapter.QuickBindAdapter;

import java.util.Iterator;
import java.util.LinkedList;

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
        /**
         * 全局配置默认加载更多布局的配置，仅对使用默认加载更多布局生效，尽可能早配置。
         * 可以放在Application里配置，这是优先级最低的
         * 配置这个不会影响在创建adapter时再自定义配置：
         * 比如：
         * DefaultLoadView view = new DefaultLoadView()
         * view.setNoMoreDataText("")
         * view.setOnFailedText("")
         * ...
         * adapter.setLoadView(view)
         */
        DefaultLoadView.Companion.createGlobalConfig(() -> {
            DefaultLoadViewConfigsBean data = new DefaultLoadViewConfigsBean();
            data.setNoMoreDataText("没有更多数据啦~~");
            data.setOnFailedText("加载失败555~");
            data.setOnLoadingText("正在超级努力的加载更多啦~~");
            data.setOnSuccessText("加载成功啦，哒哒哒~");
            data.setOnLoadingTextColor(Color.GREEN);
            data.setOnFailedTextColor(Color.RED);
            data.setOnSuccessTextColor(Color.BLUE);
            data.setNoMoreDataTextColor(Color.LTGRAY);
            return data;
        });
        /**
         * 同理配置全局占位页
         * 仅对使用默认 占位页(DefaultPlaceholder) 控件生效
         */
        DefaultPlaceholder.Companion.createGlobalConfig(()-> {
            DefaultPlaceholderConfigsBean data = new DefaultPlaceholderConfigsBean();
            data.setEmptyText("似乎是空的");
            data.setErrorText("获取失败！\n请检查网络！");
            return data;
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QuickBindAdapter adapter = new QuickBindAdapter(this);

        DefaultLoadView defaultLoadItem = new DefaultLoadView(this);
        defaultLoadItem.setNoMoreDataText("我滴任务完成啦~");
        defaultLoadItem.setOnFailedText("跟我玩鹰滴是吧！");
        defaultLoadItem.setOnLoadingText("客官请稍等片刻~");
        defaultLoadItem.setOnSuccessText("轻轻松松~");
        //如果不设置字体颜色，会应用全局配置里的字体颜色。
//        defaultLoadItem.setOnLoadingTextColor();
        adapter.setLoadMoreItemView(defaultLoadItem);//设置默认的加载更多布局

        adapter.setEmptyView(new DefaultPlaceholder(this));
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

        //数据，这里为了模拟加载更多效果，使用LinkedList逐个加载数据
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("LinearLayout单布局");
        linkedList.add("LinearLayout多布局");
        linkedList.add("GridLayout单布局");
        linkedList.add("GridLayout多布局");
        linkedList.add("StaggeredGridLayout单布局");
        linkedList.add("StaggeredGridLayout多布局");
        linkedList.add("空数据占位布局");
        Iterator<String> iterator = linkedList.iterator();
        adapter.addData(iterator.next());

        /**
         * 配置加载更多监听，如果配置了这个，即使没有调用过adapter.setLoadView()方法，则自动使用默认的加载布局
         * 等同于调用了adapter.setLoadView(adapter.getDefaultLoadItem());
         */
        adapter.setOnLoadMoreListener(() -> {
            binding.getRoot().postDelayed(() -> {
                if (iterator.hasNext()) {
                    //建议是先调用loadMoreSuccess再设置数据
                    adapter.addData(iterator.next());
                    adapter.loadMoreSuccess();
                } else {
                    adapter.loadMoreSuccessAndNoMore();
                }
            }, 300);
        });
        binding.recyclerView.setAdapter(adapter);

    }
}
