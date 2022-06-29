package com.xiaobin.bindingadapter.ui.linear;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.bean.ChatListBean;
import com.xiaobin.bindingadapter.databinding.ActivityBaseBinding;
import com.xiaobin.bindingadapter.databinding.ItemHeadBinding;
import com.xiaobin.bindingadapter.databinding.ItemLinearBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.quickbindadapter.ItemData;
import com.xiaobin.quickbindadapter.QuickBindAdapter;

/**
 * LinearLayoutManager 多布局示例
 *
 * @author xiaobin
 */
public class LinearMultiActivity extends BaseActivity<ActivityBaseBinding> {

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
        return "LinearLayout多布局";
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        QuickBindAdapter adapter = new QuickBindAdapter(this);
        //绑定数据类型和布局
        adapter.bind(ChatListBean.class, R.layout.item_linear, BR.data);
        adapter.bind(String.class, R.layout.item_head, BR.data);
        //添加子控件点击事件
        adapter.addClicks(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);
        adapter.addClicks(String.class, R.id.tv_name);

        //务必先调用setLayoutManager
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            Toast.makeText(this, "加载更多触发", Toast.LENGTH_SHORT).show();
            binding.getRoot().postDelayed(() -> {
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
                adapter.addDatas(dataList, true);
                if (adapter.getItemCount() > 50) {
                    adapter.loadMoreSuccessAndNoMore();
                } else {
                    adapter.loadMoreSuccess();
                }
            }, 1500);
        });
        //如果你想要在这里或者是在adapter中，写逻辑代码，可以这样：也可以单独写个类 实现 QuickCovert接口，然后传入这里
        adapter.setQuickBind((binding, itemData, position) -> {
            //如果是多布局，则在这里需要判断布局类型，参考：
            if (binding instanceof ItemLinearBinding) {
                // R.layout.item_linear 类型布局，在这里面写这个布局的逻辑代码
                // binding 是这个item本身，itemData 是这个item的数据，position 是这个item所在列表中的位置
            } else if (binding instanceof ItemHeadBinding) {
                // R.layout.item_head 类型布局
            }
            //也可以这样：
            if (itemData instanceof ChatListBean) {
                // R.layout.item_linear 类型布局
            } else if (itemData instanceof String) {
                // R.layout.item_head 类型布局
            }
        });
        //绑定item的点击事件
        adapter.setOnItemClickListener((adapter1, view, data, position) -> {
            //item点击事件
            if (data instanceof ChatListBean) {
                ChatListBean mData = (ChatListBean) data;
                Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条", Toast.LENGTH_SHORT).show();
            } else if (data instanceof String) {
                Toast.makeText(this, "点击的是 " + data + " 分组条", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemChildClickListener((adapter1, view, data, position) -> {
            //item上子控件 点击事件
            int viewId = view.getId();
            if (data instanceof ChatListBean) {
                ChatListBean mData = (ChatListBean) data;
                if (viewId == R.id.iv_image) {
                    Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条的 头像", Toast.LENGTH_SHORT).show();
                } else if (viewId == R.id.tv_name) {
                    Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条的 名字", Toast.LENGTH_SHORT).show();
                } else if (viewId == R.id.tv_message) {
                    Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条的 消息", Toast.LENGTH_SHORT).show();
                }
            } else if (data instanceof String) {
                Toast.makeText(this, "点击的是 " + data + " 分组条的 TextView", Toast.LENGTH_SHORT).show();
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
