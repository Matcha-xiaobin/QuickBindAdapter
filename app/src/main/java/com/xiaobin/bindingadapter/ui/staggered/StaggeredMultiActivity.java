package com.xiaobin.bindingadapter.ui.staggered;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.xiaobin.bindingadapter.BR;
import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.bean.ChatListBean;
import com.xiaobin.bindingadapter.bean.ChatListBean2;
import com.xiaobin.bindingadapter.bean.StaggeredHeadBean;
import com.xiaobin.bindingadapter.databinding.ActivityBaseBinding;
import com.xiaobin.bindingadapter.databinding.ItemStaggered2Binding;
import com.xiaobin.bindingadapter.databinding.ItemStaggeredBinding;
import com.xiaobin.bindingadapter.databinding.ItemStaggeredHeadBinding;
import com.xiaobin.bindingadapter.ui.base.BaseActivity;
import com.xiaobin.quickbindadapter.ItemData;
import com.xiaobin.quickbindadapter.QuickBindAdapter;

/**
 * StaggeredLayoutManager 多布局示例
 *
 * @author xiaobin
 */
public class StaggeredMultiActivity extends BaseActivity<ActivityBaseBinding> {

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
        return "StaggeredGridLayout多布局";
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        QuickBindAdapter adapter = new QuickBindAdapter(this);
        //绑定数据类型和布局
        adapter.bind(ChatListBean.class, R.layout.item_staggered, BR.data);
        adapter.bind(ChatListBean2.class, R.layout.item_staggered2, BR.data);
        /**
         * 这是一个横跨整个屏幕宽度的头部
         * 因为StaggeredGridLayoutManager没有提供GridLayoutManager的spanLookup接口
         * 所以这里想要实现这个效果，只需要将需要当做头部的布局所对应的数据包装一下实体类并且务必实现StaggeredFullSpan接口
         * 否则不生效
         * 参考：StaggeredHeadBean 类
         */
        adapter.bind(StaggeredHeadBean.class, R.layout.item_staggered_head, BR.data);
        //添加子控件点击事件
        adapter.addClicks(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);
        adapter.addClicks(ChatListBean2.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);
        adapter.addClicks(StaggeredHeadBean.class, R.id.tv_name);

        //务必先调用setLayoutManager
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(() -> {
            Toast.makeText(this, "加载更多触发", Toast.LENGTH_SHORT).show();
            binding.getRoot().postDelayed(() -> {
                adapter.addData(createSomeData(), true);
                if (adapter.getItemCount() > 50) {
                    adapter.loadMoreSuccessAndNoMore();
                } else {
                    adapter.loadMoreSuccess();
                }
            }, 1500);
        });

        //如果你想要在这里或者是在adapter中，写逻辑代码，可以这样：也可以单独写个类 实现 QuickCovert接口，然后传入这里
        adapter.setQuickBind((binding, itemData, position) -> {
            // binding 是这个item本身，itemData 是这个item的数据，position 是这个item所在列表中的位置
            //如果是多布局，则在这里需要判断布局类型，参考：
            if (binding instanceof ItemStaggeredBinding) {
                // R.layout.item_staggered 类型布局
            } else if (binding instanceof ItemStaggered2Binding) {
                // R.layout.item_staggered2 类型布局
            } else if (binding instanceof ItemStaggeredHeadBinding) {
                // R.layout.item_staggered_head 类型布局
            }
            //也可以这样：
            if (itemData instanceof ChatListBean) {
                // R.layout.item_staggered 类型布局
            } else if (itemData instanceof ChatListBean2) {
                // R.layout.item_staggered2 类型布局
            } else if (itemData instanceof StaggeredHeadBean) {
                // R.layout.item_staggered_head 类型布局
            }
        });
        //绑定item的点击事件
        adapter.setOnItemClickListener((adapter1, view, data, position) -> {
            //item点击事件
            if (data instanceof ChatListBean) {
                ChatListBean mData = (ChatListBean) data;
                Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条", Toast.LENGTH_SHORT).show();
            } else if (data instanceof ChatListBean2) {
                ChatListBean2 mData = (ChatListBean2) data;
                Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条", Toast.LENGTH_SHORT).show();
            } else if (data instanceof StaggeredHeadBean) {
                StaggeredHeadBean mData = (StaggeredHeadBean) data;
                Toast.makeText(this, "点击的是 " + mData.getTitle() + " 分组条", Toast.LENGTH_SHORT).show();
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
            } else if (data instanceof ChatListBean2) {
                ChatListBean2 mData = (ChatListBean2) data;
                if (viewId == R.id.iv_image) {
                    Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条的 头像", Toast.LENGTH_SHORT).show();
                } else if (viewId == R.id.tv_name) {
                    Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条的 名字", Toast.LENGTH_SHORT).show();
                } else if (viewId == R.id.tv_message) {
                    Toast.makeText(this, "点击的是 " + mData.getId() + " 消息条的 消息", Toast.LENGTH_SHORT).show();
                }
            } else if (data instanceof StaggeredHeadBean) {
                StaggeredHeadBean mData = (StaggeredHeadBean) data;
                Toast.makeText(this, "点击的是 " + mData.getTitle() + " 分组条的 TextView", Toast.LENGTH_SHORT).show();
            }

        });

        //数据方式一：
        adapter.setNewData(createSomeData());

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

    private ItemData createSomeData() {
        ItemData dataList = new ItemData();
        for (int i = 0; i < 15; i++) {
            switch (i) {
                case 0:
                    dataList.add(new StaggeredHeadBean("分组一"));
                    break;
                case 3:
                    dataList.add(new StaggeredHeadBean("分组二"));
                    break;
                case 5:
                    dataList.add(new StaggeredHeadBean("分组三"));
                    break;
                case 7:
                    dataList.add(new StaggeredHeadBean("分组四"));
                    break;
                default:
                    break;
            }
            if (i % 3 == 0) {
                ChatListBean2 item = new ChatListBean2();
                item.setId(String.valueOf(i));
                dataList.add(item);
            } else {
                ChatListBean item = new ChatListBean();
                item.setId(String.valueOf(i));
                dataList.add(item);
            }
        }
        return dataList;
    }
}
