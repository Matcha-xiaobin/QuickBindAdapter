package com.xiaobin.quickbindadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小斌
 * @data 2019/7/10
 **/
public class QuickBindAdapter extends RecyclerView.Adapter<BaseBindingViewHolder> {

    private final String TAG = "QuickBindAdapter";

    //数据类型集合
    private List<Class<?>> clazzList = new ArrayList<>();
    //databinding属性名集合
    private List<Integer> variableIds = new ArrayList<>();
    //item布局集合
    private List<Integer> layoutIds = new ArrayList<>();
    //需要点击事件，长按事件监听的viewId集合
    private Map<Class<?>, List<Integer>> clickListenerIds = new HashMap<>();
    private Map<Class<?>, List<Integer>> longClickListenerIds = new HashMap<>();
    //数据集合
    private ItemData dataList = new ItemData();

    //*******************************用于外部调用的方法******************************

    /**
     * 设置新的数据
     *
     * @param dataList
     */
    public void setNewData(List<?> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    /**
     * 设置新的数据
     *
     * @param dataList
     */
    public void setNewData(ItemData dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    /**
     * 添加单个数据
     *
     * @param data
     */
    public void addData(Object data) {
        this.dataList.add(data);
        notifyItemInserted(getItemCount());
    }

    /**
     * 添加数据
     *
     * @param datas
     */
    public void addDatas(List<?> datas) {
        int lastIndex = getItemCount();
        this.dataList.addAll(datas);
        notifyItemRangeInserted(lastIndex - datas.size(), datas.size());
    }

    /**
     * 添加数据
     *
     * @param datas
     */
    public void addDatas(ItemData datas) {
        int lastIndex = getItemCount();
        this.dataList.addAll(datas);
        notifyItemRangeInserted(lastIndex - datas.size(), getItemCount());
    }

    /**
     * 移除某个item
     *
     * @param position
     */
    public void remove(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    /**
     * 替换item内容
     *
     * @param position
     * @param itemData
     */
    public void replace(int position, Object itemData) {
        dataList.set(position, itemData);
        notifyItemChanged(position);
    }

    /**
     * 绑定布局
     *
     * @param clazz          数据类型
     * @param layoutId       布局ID
     * @param bindVariableId DataBinding BR
     * @return 这个对象
     */
    public QuickBindAdapter bind(Class<?> clazz, @LayoutRes int layoutId, int bindVariableId) {
        if (!clazzList.contains(clazz)) {
            clazzList.add(clazz);
            layoutIds.add(layoutId);
            variableIds.add(bindVariableId);
        }
        return this;
    }

    /**
     * 添加子控件点击监听
     *
     * @param clazz
     * @param viewId
     * @return
     */
    public QuickBindAdapter addClickListener(Class<?> clazz, @IdRes int... viewId) {
        List<Integer> ids = new ArrayList<>(viewId.length);
        for (Integer id : viewId) {
            ids.add(id);
        }
        clickListenerIds.put(clazz, ids);
        return this;
    }

    /**
     * 添加子控件长按监听
     *
     * @param clazz
     * @param viewId
     * @return
     */
    public QuickBindAdapter addLongClickListener(Class<?> clazz, @IdRes int... viewId) {
        List<Integer> ids = new ArrayList<>(viewId.length);
        for (Integer id : viewId) {
            ids.add(id);
        }
        longClickListenerIds.put(clazz, ids);
        return this;
    }

    /**
     * 获取指定item内容
     *
     * @param position
     * @return
     */
    public Object getItemData(int position) {
        if (dataList != null && dataList.size() > position) {
            return dataList.get(position);
        }
        return null;
    }

    /**
     * 设置item点击监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置item长按监听
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 设置子控件点击监听
     *
     * @param onItemChildClickListener
     */
    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }

    /**
     * 设置子控件长按监听
     *
     * @param onItemChildLongClickListener
     */
    public void setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        this.onItemChildLongClickListener = onItemChildLongClickListener;
    }

    //*******************************用于外部调用的方法 结束******************************

    @Override
    public int getItemViewType(int position) {
        //得到itemData的index，然后得到对应的数据
        Object itemData = dataList.get(position);
        //判断数据类型集合中是否有这个数据的类型
        if (clazzList.contains(itemData.getClass())) {
            //如果有这个类型，则返回这个类型所在集合的index
            return clazzList.indexOf(itemData.getClass());
        }
        //如果没有这个类型，则返回-1
        return -1;
    }

    @NonNull
    @Override
    public BaseBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //根据getItemViewType方法返回的viewType，判断是否是头部
        if (viewType > -1) {
            return new BaseBindingViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    layoutIds.get(viewType), parent, false));
        }
        return new BaseBindingViewHolder(new View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBindingViewHolder holder, int position) {
        int itemType = holder.getItemViewType();
        if (itemType < 0) {
            return;
        }
        Class clz = clazzList.get(itemType);
        //item点击事件绑定
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(this, view, position);
            }
        });
        //item长按事件绑定
        holder.itemView.setOnLongClickListener(view -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(this, view, position);
                return true;
            }
            return false;
        });
        //子控件点击事件
        if (clickListenerIds.containsKey(clz)) {
            List<Integer> _ids = clickListenerIds.get(clz);
            for (Integer id : _ids) {
                holder.itemView.findViewById(id).setOnClickListener(view -> {
                    if (onItemChildClickListener != null) {
                        onItemChildClickListener.onItemClick(this, view, position);
                    }
                });
            }
        }
        //子控件长按事件
        if (longClickListenerIds.containsKey(clz)) {
            List<Integer> _ids = longClickListenerIds.get(clz);
            for (Integer id : _ids) {
                holder.itemView.findViewById(id).setOnLongClickListener(view -> {
                    if (onItemChildLongClickListener != null) {
                        onItemChildLongClickListener.onItemLongClick(this, view, position);
                        return true;
                    }
                    return false;
                });
            }
        }

        holder.getBinding().setVariable(variableIds.get(itemType), dataList.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * item事件
     */
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;

    /**
     * 子控件事件
     */
    private OnItemChildClickListener onItemChildClickListener;
    private OnItemChildLongClickListener onItemChildLongClickListener;

    /**
     * 点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(QuickBindAdapter adapter, View view, int position);
    }

    /**
     * 长按事件
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(QuickBindAdapter adapter, View view, int position);
    }

    /**
     * 子控件点击事件
     */
    public interface OnItemChildClickListener {
        void onItemClick(QuickBindAdapter adapter, View view, int position);
    }

    /**
     * 子控件长按事件
     */
    public interface OnItemChildLongClickListener {
        void onItemLongClick(QuickBindAdapter adapter, View view, int position);
    }
}
