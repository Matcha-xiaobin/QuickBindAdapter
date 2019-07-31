package com.xiaobin.bindingadapter.ui.grid;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
public class GridSingleActivity extends BaseActivity<ActivityBaseBinding> {

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
        return "GridLayout单布局";
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        QuickBindAdapter adapter = new QuickBindAdapter();
        //绑定数据类型和布局
        adapter.bind(ChatListBean.class, R.layout.item_grid, BR.data);
        //添加子控件点击事件
        adapter.addClickListener(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(adapter);
        //绑定item的点击事件
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            //item点击事件
            ChatListBean itemData = (ChatListBean) adapter1.getItemData(position);
            Toast.makeText(this, "ID: " + itemData.getId(), Toast.LENGTH_SHORT).show();
        });
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            //item上子控件 点击事件
            ChatListBean itemData = (ChatListBean) adapter1.getItemData(position);
            int viewId = view.getId();
            if (viewId == R.id.iv_image) {
                Toast.makeText(this, "点击的是ID: " + itemData.getId() + " 的 头像", Toast.LENGTH_SHORT).show();
            } else if (viewId == R.id.tv_name) {
                Toast.makeText(this, "点击的是ID: " + itemData.getId() + " 的 名字", Toast.LENGTH_SHORT).show();
            } else if (viewId == R.id.tv_message) {
                Toast.makeText(this, "点击的是ID: " + itemData.getId() + " 的 消息", Toast.LENGTH_SHORT).show();
            }

        });

        //数据方式一：
        ItemData dataList = new ItemData();
        ChatListBean item;
        for (int i = 0; i < 15; i++) {
            item = new ChatListBean();
            item.setId(String.valueOf(i));
            dataList.add(item);
        }
        adapter.setNewData(dataList);

        //数据方式二：
//        List<ChatListBean> dataList2 = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            item = new ChatListBean();
//            item.setId(String.valueOf(i));
//            dataList2.add(item);
//        }
//        adapter.setNewData(dataList2);

        //添加数据:
        item = new ChatListBean();
        item.setId("嘿咻");
        adapter.addData(item);

        //移除数据:
//        adapter.remove(adapter.getItemCount() - 1);
    }
}
