package com.xiaobin.bindingadapter.ui.linear;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.bean.ChatListBean;
import com.xiaobin.bindingadapter.databinding.ActivityBaseBinding;
import com.xiaobin.bindingadapter.databinding.ItemLinearBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.quickbindadapter.ItemData;
import com.xiaobin.quickbindadapter.QuickBindAdapter;

/**
 * LinearLayoutManager 示例
 *
 * @author xiaobin
 */
public class LinearSingleActivity extends BaseActivity<ActivityBaseBinding> {

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
        return "LinearLayout单布局";
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        QuickBindAdapter adapter = new QuickBindAdapter(this);
        //绑定数据类型和布局
        adapter.bind(ChatListBean.class, R.layout.item_linear, BR.data);
        //添加子控件点击事件
        adapter.addClicks(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        //如果你想要在这里或者是在adapter中，写逻辑代码，可以这样：也可以单独写个类 实现 QuickCovert接口，然后传入这里
        adapter.setQuickBind((binding, itemData, position) -> {
            // binding 是这个item本身，itemData 是这个item的数据，position 是这个item所在列表中的位置
            ItemLinearBinding mBinding = (ItemLinearBinding) binding;
            mBinding.tvName.setTextColor(Color.RED);
        });
        //绑定item的点击事件
        adapter.setOnItemClickListener((adapter1, view, data, position) -> {
            //item点击事件
            ChatListBean itemData = (ChatListBean) data;
            Toast.makeText(this, "ID: " + itemData.getId(), Toast.LENGTH_SHORT).show();
        });
        adapter.setOnItemChildClickListener((adapter1, view, data, position) -> {
            //item上子控件 点击事件
            ChatListBean itemData = (ChatListBean) data;
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
