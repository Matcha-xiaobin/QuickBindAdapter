package com.xiaobin.bindingadapter.ui.grid;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.bean.ChatListBean;
import com.xiaobin.bindingadapter.databinding.ActivityBaseBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.quickbindadapter.ItemData;
import com.xiaobin.quickbindadapter.QuickBindAdapter;

/**
 * @author xiaobin
 */
public class GridMultiActivity extends BaseActivity<ActivityBaseBinding> {

    @Override
    protected boolean showBackButton() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base;
    }

    @Override
    protected String getActionTitle() {
        return "GridLayout多布局";
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        QuickBindAdapter adapter = new QuickBindAdapter();
        //绑定数据类型和布局
        adapter.bind(ChatListBean.class, R.layout.item_grid, BR.data);
        adapter.bind(String.class, R.layout.item_head, BR.data);
        //添加子控件点击事件
        adapter.addClickListener(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);
        adapter.addClickListener(String.class, R.id.tv_name);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Object itemData = adapter.getItemData(position);
                if (itemData instanceof ChatListBean) {
                    return 1;
                } else if (itemData instanceof String) {
                    return 3;
                }
                return 0;
            }
        });
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        //绑定item的点击事件
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            //item点击事件
            Object itemData = adapter1.getItemData(position);
            if (itemData instanceof ChatListBean) {
                ChatListBean data = (ChatListBean) itemData;
                Toast.makeText(this, "点击的是 " + data.getId() + " 消息条", Toast.LENGTH_SHORT).show();
            } else if (itemData instanceof String) {
                Toast.makeText(this, "点击的是 " + itemData + " 分组条", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            //item上子控件 点击事件
            Object itemData = adapter1.getItemData(position);
            int viewId = view.getId();
            if (itemData instanceof ChatListBean) {
                ChatListBean data = (ChatListBean) itemData;
                if (viewId == R.id.iv_image) {
                    Toast.makeText(this, "点击的是 " + data.getId() + " 消息条的 头像", Toast.LENGTH_SHORT).show();
                } else if (viewId == R.id.tv_name) {
                    Toast.makeText(this, "点击的是 " + data.getId() + " 消息条的 名字", Toast.LENGTH_SHORT).show();
                } else if (viewId == R.id.tv_message) {
                    Toast.makeText(this, "点击的是 " + data.getId() + " 消息条的 消息", Toast.LENGTH_SHORT).show();
                }
            } else if (itemData instanceof String) {
                Toast.makeText(this, "点击的是 " + itemData + " 分组条的 TextView", Toast.LENGTH_SHORT).show();
            }

        });

        //数据方式一：
        ItemData dataList = new ItemData();
        ChatListBean item;
        for (int i = 0; i < 15; i++) {
            switch (i) {
                case 0:
                    dataList.add("分组一");
                    break;
                case 3:
                    dataList.add("分组二");
                    break;
                case 5:
                    dataList.add("分组三");
                    break;
                case 7:
                    dataList.add("分组四");
                    break;
                default:
                    break;
            }
            item = new ChatListBean();
            item.setId(String.valueOf(i));
            dataList.add(item);
        }
        adapter.setNewData(dataList);

        //数据方式二：
//        List<ChatListBean> dataList2 = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            dataList2.add(new ChatListBean());
//        }
//        adapter.setNewData(dataList2);

        //添加数据:
        adapter.addData("分组五");
        adapter.addData(new ChatListBean());

        //移除数据:
//        adapter.remove(adapter.getItemCount() - 1);
    }
}
