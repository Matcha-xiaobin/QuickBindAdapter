package com.xiaobin.bindingadapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.databinding.ActivityMainBinding;
import com.xiaobin.quickbindadapter.ItemData;
import com.xiaobin.quickbindadapter.QuickBindingAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_main, null, false);
        setContentView(binding.getRoot());

        QuickBindingAdapter adapter = new QuickBindingAdapter();
        //绑定数据类型和布局
        adapter.bind(ChatListBean.class, R.layout.item_main, BR.data);
        adapter.bind(String.class, R.layout.item_head, BR.data);
        //添加子控件点击事件
        adapter.addClickListener(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);
        adapter.addClickListener(String.class, R.id.tv_name);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
