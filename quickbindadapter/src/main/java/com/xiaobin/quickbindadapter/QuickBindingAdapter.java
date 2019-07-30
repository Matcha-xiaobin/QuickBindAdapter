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
public class QuickBindingAdapter extends RecyclerView.Adapter<BaseBindingViewHolder> {

    private final String TAG = "QuickBindingAdapter";

    private List<View> headViews = new ArrayList<>();
    private List<View> footViews = new ArrayList<>();
    //数据类型
    private List<Class<?>> clazzList = new ArrayList<>();
    //databinding属性名
    private List<Integer> variableIds = new ArrayList<>();
    //item布局
    private List<Integer> layoutIds = new ArrayList<>();
    //需要点击事件，长按事件监听的viewId
    private Map<Class<?>, List<Integer>> clickListenerIds = new HashMap<>();
    private Map<Class<?>, List<Integer>> longClickListenerIds = new HashMap<>();

    private ItemData dataList = new ItemData();
    private int realIndex;

    //*******************************用于外部调用的方法******************************
    public void addHead(View view) {
        headViews.add(view);
        notifyDataSetChanged();
    }

    public void addFooter(View view) {
        footViews.add(view);
        notifyDataSetChanged();
    }

    public void setNewData(List<?> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void setNewData(ItemData dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void addData(Object data) {
        this.dataList.add(data);
        notifyItemInserted(getItemCount() - footViews.size());
        if (footViews.size() > 0) {
            notifyItemRangeChanged(getItemCount() - footViews.size(), footViews.size());
        }
    }

    public void addDatas(List<?> datas) {
        this.dataList.addAll(datas);
        int lastIndex = getItemCount() - footViews.size();
        notifyItemRangeInserted(lastIndex - datas.size(), datas.size());
    }

    public void addDatas(ItemData datas) {
        this.dataList.addAll(datas);
        int lastIndex = getItemCount() - footViews.size();
        notifyItemRangeInserted(lastIndex - datas.size(), getItemCount());
    }

    public void remove(int position) {
        realIndex = headViews.size() + position;
        notifyItemRemoved(realIndex);
        dataList.remove(position);
        notifyItemRangeChanged(realIndex, getItemCount() - realIndex);
    }

    public void replace(int position, Object itemData) {
        realIndex = headViews.size() + position;
        dataList.set(position, itemData);
        notifyItemChanged(realIndex);
    }

    public QuickBindingAdapter bind(Class<?> clazz, @LayoutRes int layoutId, int bindVariableId) {
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
    public QuickBindingAdapter addClickListener(Class<?> clazz, @IdRes int... viewId) {
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
    public QuickBindingAdapter addLongClickListener(Class<?> clazz, @IdRes int... viewId) {
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
        if (headViews.size() > 0) {
            if (position < headViews.size()) {
                return position;
            }
        }
        if (footViews.size() > 0) {
            if (position >= headViews.size() + dataList.size()) {
                return -(position - (headViews.size() + dataList.size() + 1));
            }
        }
        position -= headViews.size();
        Object itemData = dataList.get(position);
        if (clazzList.contains(itemData.getClass())) {
            if (headViews.size() > 0) {
                return headViews.size() + clazzList.indexOf(itemData.getClass());
            } else {
                return clazzList.indexOf(itemData.getClass());
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public BaseBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType < headViews.size() && viewType > -1) {
            return new BaseBindingViewHolder(headViews.get(viewType));
        }
        if (footViews.size() > 0 && viewType < 0) {
            return new BaseBindingViewHolder(footViews.get(-viewType - 1));
        }
        if (viewType > -1) {
            int vt = headViews.size() > 0 ? viewType - headViews.size() : viewType;
            return new BaseBindingViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    layoutIds.get(vt), parent, false));
        }
        return new BaseBindingViewHolder(new View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBindingViewHolder holder, int position) {
        int itemType = holder.getItemViewType();
        if (itemType < headViews.size()) {
            return;
        }
        int clzIndex = headViews.size() > 0 ?
                itemType - headViews.size() : itemType;
        int finalPosition = position - headViews.size();
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(view ->
                    onItemClickListener.onItemClick(this, view, finalPosition));
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(view -> {
                onItemLongClickListener.onItemLongClick(this, view, finalPosition);
                return true;
            });
        }
        Class clz = clazzList.get(clzIndex);
        //子控件点击事件
        if (onItemChildClickListener != null && clickListenerIds.containsKey(clz)) {
            List<Integer> _ids = clickListenerIds.get(clz);
            for (Integer id : _ids) {
                holder.itemView.findViewById(id).setOnClickListener(view ->
                        onItemChildClickListener.onItemClick(this, view, finalPosition));
            }
        }
        //子控件长按事件
        if (onItemChildLongClickListener != null && longClickListenerIds.containsKey(clz)) {
            List<Integer> _ids = longClickListenerIds.get(clz);
            for (Integer id : _ids) {
                holder.itemView.findViewById(id).setOnLongClickListener(view -> {
                    onItemChildLongClickListener.onItemLongClick(this, view, finalPosition);
                    return true;
                });
            }
        }
        holder.getBinding().setVariable(variableIds.get(clzIndex), dataList.get(finalPosition));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return headViews.size() + dataList.size() + footViews.size();
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
        void onItemClick(QuickBindingAdapter adapter, View view, int position);
    }

    /**
     * 长按事件
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(QuickBindingAdapter adapter, View view, int position);
    }

    /**
     * 子控件点击事件
     */
    public interface OnItemChildClickListener {
        void onItemClick(QuickBindingAdapter adapter, View view, int position);
    }

    /**
     * 子控件长按事件
     */
    public interface OnItemChildLongClickListener {
        void onItemLongClick(QuickBindingAdapter adapter, View view, int position);
    }
}
