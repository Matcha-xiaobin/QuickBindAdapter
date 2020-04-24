package com.xiaobin.quickbindadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小斌
 * @data 2019/7/10
 **/
public class QuickBindAdapter extends RecyclerView.Adapter<BindHolder> {

    private final String TAG = "QuickBindAdapter";

    private final int NONE_TYPE = -1;
    private final int LOAD_MORE_TYPE = -2;

    enum LoadMoreState {
        LOADING_MORE, LOAD_SUCCESS, LOAD_FAIL, LOAD_COMPLETE
    }

    private LoadMoreState loadMoreState = LoadMoreState.LOAD_SUCCESS;

    //数据类型集合
    private List<Class<?>> clazzList = new ArrayList<>();
    //databinding属性名集合
    private Map<Class<?>, Integer> variableIds = new HashMap<>();
    //item布局集合
    private Map<Class<?>, Integer> layoutIds = new HashMap<>();
    //需要点击事件，长按事件监听的viewId集合
    private Map<Class<?>, List<Integer>> clickListenerIds = new HashMap<>();
    private Map<Class<?>, List<Integer>> longClickListenerIds = new HashMap<>();
    //数据集合
    private ItemData dataList = new ItemData();
    //额外的item样式处理
    private QuickCovert quickCovert;

    private OnLoadMoreListener onLoadMoreListener;
    private RecyclerView mRecyclerView;
    private GridLayoutManager.SpanSizeLookup spanSizeLookup;
    private boolean isHasMore = true;
    private String loadMoreText = "努力加载中...";
    private String loadFailText = "加载失败了!";
    private String loadCompleteText = "我是有底线的";
    private String loadSuccessText = "加载完成";
    private int oldScrollY = 0;

    //空数据占位图
    private View emptyView;
    private boolean showEmptyView;

    //*******************************用于外部调用的方法******************************

    public void setLoadMoreText(String loadMoreText) {
        this.loadMoreText = loadMoreText;
    }

    public void setLoadFailText(String loadFailText) {
        this.loadFailText = loadFailText;
    }

    public void setLoadCompleteText(String loadCompleteText) {
        this.loadCompleteText = loadCompleteText;
    }

    public void setSpanSizeLookup(GridLayoutManager.SpanSizeLookup spanLook) {
        this.spanSizeLookup = spanSizeLookup;
        setupSpanSizeLookUp();
    }

    private void setupSpanSizeLookUp() {
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (onLoadMoreListener != null && getItemViewType(position) == LOAD_MORE_TYPE) {
                            return gridLayoutManager.getSpanCount();
                        } else if (QuickBindAdapter.this.spanSizeLookup != null) {
                            return QuickBindAdapter.this.spanSizeLookup.getSpanSize(position);
                        } else if (spanSizeLookup != null) {
                            return spanSizeLookup.getSpanSize(position);
                        }
                        return 1;
                    }
                });
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        setupSpanSizeLookUp();
    }

    /**
     * 设置启用加载更多
     *
     * @param onLoadMoreListener 加载更多
     * @param recyclerView       recyclerView
     */
    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener onLoadMoreListener,
                                      @NonNull RecyclerView recyclerView) {
        this.onLoadMoreListener = onLoadMoreListener;
        mRecyclerView = recyclerView;
        mRecyclerView.setAdapter(this);
        setupSpanSizeLookUp();
        setupScrollListener();
    }

    public void loadMoreSuccess() {
        isHasMore = true;
        loadMoreState = LoadMoreState.LOAD_SUCCESS;
        notifyItemChanged(getItemCount() - 1);
    }

    public void loadMoreComplete() {
        isHasMore = false;
        loadMoreState = LoadMoreState.LOAD_COMPLETE;
        notifyItemChanged(getItemCount() - 1);
    }

    public void loadMoreFail() {
        loadMoreState = LoadMoreState.LOAD_FAIL;
        notifyItemChanged(getItemCount() - 1);
    }

    /**
     * 设置启用加载更多
     * 必须在绑定recyclerView之后才能调用
     *
     * @param onLoadMoreListener 加载更多
     */
    @Deprecated
    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener onLoadMoreListener) {
        if (mRecyclerView == null)
            throw new NullPointerException("请在 onAttachedToRecyclerView 之后在调用" +
                    ".setOnLoadMoreListener(onLoadMoreListener);");
        this.onLoadMoreListener = onLoadMoreListener;
        setupSpanSizeLookUp();
        setupScrollListener();
    }

    private void setupScrollListener() {
        if (mRecyclerView == null) return;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (oldScrollY > dy) {
                    oldScrollY = dy;
                    return;
                }
                oldScrollY = dy;
                if (mRecyclerView.getLayoutManager() == null) return;
                int lastItemIndex = 0;
                if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    lastItemIndex = layoutManager.findLastVisibleItemPosition();
                } else if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    lastItemIndex = layoutManager.findLastVisibleItemPosition();
                } else {
                    return;
                }
                if (getItemViewType(lastItemIndex) == LOAD_MORE_TYPE) {
                    //是在加载更多
                    if (loadMoreState != LoadMoreState.LOADING_MORE) {
                        if (onLoadMoreListener != null && isHasMore) {
                            loadMoreState = LoadMoreState.LOADING_MORE;
                            notifyItemChanged(lastItemIndex);
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            }
        });
    }

    /**
     * 链式调用
     *
     * @return new这个对象
     */
    public static QuickBindAdapter Create() {
        return new QuickBindAdapter();
    }

    /**
     * 是否显示的占位图
     *
     * @return 是否显示占位图
     */
    public boolean isShowEmptyView() {
        return showEmptyView;
    }

    /**
     * 设置空数据占位图
     *
     * @param view 站位视图
     */
    public QuickBindAdapter setEmptyView(View view) {
        emptyView = view;
        return this;
    }

    /**
     * 移除空数据占位图
     */
    public void removeEmptyView() {
        emptyView = null;
    }

    /**
     * 如果你只想用databinding来拿控件，其他的逻辑依然写在adapter中，那就实现这个吧
     *
     * @param quickCovert 用于自定义更多功能
     */
    public QuickBindAdapter setQuickCovert(QuickCovert quickCovert) {
        this.quickCovert = quickCovert;
        return this;
    }

    /**
     * 设置新的数据
     *
     * @param data 全新数据
     */
    public void setNewData(List<?> data) {
        if (data == null) {
            this.dataList = new ItemData();
        } else {
            ItemData itemData = new ItemData();
            itemData.addAll(data);
            this.dataList = itemData;
        }
        if (dataList.size() == 0) {
            isHasMore = false;
        } else {
            isHasMore = true;
        }
        notifyDataSetChanged();
    }

    /**
     * 设置新的数据
     *
     * @param data 全新数据
     */
    public void setNewData(ItemData data) {
        this.dataList = data == null ? new ItemData() : data;
        if (dataList.size() == 0) {
            isHasMore = false;
        } else {
            isHasMore = true;
        }
        notifyDataSetChanged();
    }

    /**
     * 移动Item
     *
     * @param fromPosition 要移动的Item下标
     * @param toPosition   要移动到的位置下标
     */
    public void movedPositions(int fromPosition, int toPosition) {
        this.dataList.add(toPosition, this.dataList.remove(fromPosition));//数据更换
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1);
    }

    /**
     * 添加单个数据
     *
     * @param data 单个数据，添加到最后
     */
    public void addData(Object data) {
        this.dataList.add(data);
        notifyItemInserted(dataList.size());
        compatibilityDataSizeChanged(1);
    }

    /**
     * 插入单个数据
     *
     * @param data  单个数据
     * @param index 插入位置
     */
    public void insertData(int index, Object data) {
        this.dataList.add(index, data);
        notifyItemInserted(index);
        notifyItemRangeChanged(index, dataList.size() - index);
        compatibilityDataSizeChanged(1);
    }

    /**
     * 插入多个数据
     *
     * @param datas 多个数据
     * @param index 插入位置
     */
    public void insertDatas(int index, ItemData datas) {
        this.dataList.addAll(index, datas);
        notifyItemRangeChanged(index, dataList.size() - index);
        compatibilityDataSizeChanged(datas.size());
    }

    /**
     * 插入多个数据
     *
     * @param datas 多个数据
     * @param index 插入位置
     */
    public void insertDatas(int index, List<?> datas) {
        this.dataList.addAll(index, datas);
        notifyItemRangeChanged(index, getItemCount() - index);
        compatibilityDataSizeChanged(datas.size());
    }

    /**
     * 添加数据
     *
     * @param datas 多个数据，添加到最后
     */
    public void addDatas(List<?> datas) {
        int lastIndex = getItemCount();
        this.dataList.addAll(datas);
        notifyItemRangeInserted(lastIndex - datas.size(), datas.size());
        compatibilityDataSizeChanged(datas.size());
    }

    /**
     * 添加数据
     *
     * @param datas 多个数据，添加到最后
     */
    public void addDatas(ItemData datas) {
        int lastIndex = getItemCount();
        this.dataList.addAll(datas);
        notifyItemRangeInserted(lastIndex - datas.size(), getItemCount());
        compatibilityDataSizeChanged(datas.size());
    }

    /**
     * 移除某个item
     *
     * @param position 位置
     */
    public void remove(int position) {
        if (dataList.size() <= position) {
            return;
        }
        dataList.remove(position);
        notifyItemRemoved(position);
        compatibilityDataSizeChanged(0);
        notifyItemRangeChanged(position, dataList.size() - position);
    }

    /**
     * 清空数据
     */
    public void removeAll() {
        dataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换item内容
     *
     * @param position 位置
     * @param itemData 单个数据
     */
    public void replace(int position, Object itemData) {
        if (dataList.size() <= position) {
            addData(itemData);
            return;
        }
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
            layoutIds.put(clazz, layoutId);
            variableIds.put(clazz, bindVariableId);
        }
        return this;
    }

    /**
     * 绑定布局
     *
     * @param clazz    数据类型
     * @param layoutId 布局ID
     * @return 这个对象
     */
    public QuickBindAdapter bind(Class<?> clazz, @LayoutRes int layoutId) {
        if (!clazzList.contains(clazz)) {
            clazzList.add(clazz);
            layoutIds.put(clazz, layoutId);
        }
        return this;
    }

    /**
     * 添加子控件点击监听
     *
     * @param clazz  数据类型
     * @param viewId 控件ID，多个
     * @return 这个对象
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
     * @param clazz  数据类型
     * @param viewId 控件ID，多个
     * @return 这个对象
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
     * @param position 位置
     * @return 这个位置的数据
     */
    public Object getItemData(int position) {
        if (dataList != null && dataList.size() > position) {
            return dataList.get(position);
        }
        return null;
    }

    /**
     * 获得全部item数据
     *
     * @return 整个数据ArrayList
     */
    public ItemData getDatas() {
        return dataList;
    }

    /**
     * 设置item点击监听
     *
     * @param onItemClickListener 点击事件实现
     */
    public QuickBindAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * 设置item长按监听
     *
     * @param onItemLongClickListener 长按事件实现
     */
    public QuickBindAdapter setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        return this;
    }

    /**
     * 设置子控件点击监听
     *
     * @param onItemChildClickListener 子控件点击事件实现
     */
    public QuickBindAdapter setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
        return this;
    }

    /**
     * 设置子控件长按监听
     *
     * @param onItemChildLongClickListener 子控件长按事件实现
     */
    public QuickBindAdapter setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        this.onItemChildLongClickListener = onItemChildLongClickListener;
        return this;
    }

    /**
     * 获取数据大小
     *
     * @return 数据大小
     */
    public int getDataCount() {
        return dataList.size();
    }

    //*******************************用于外部调用的方法 结束******************************

    /**
     * 如果变动的数据大小和实际数据大小一致，则刷新整个列表
     *
     * @param size 变动的数据大小
     */
    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = dataList == null ? 0 : dataList.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (onLoadMoreListener != null && position == getItemCount() - 1) {
            //如果设置了加载更多功能，则最后一个为加载更多的布局
            return LOAD_MORE_TYPE;
        }
        if (emptyView != null && dataList.size() == 0) {
            return NONE_TYPE;
        }
        //得到itemData的index，然后得到对应的数据
        Object itemData = dataList.get(position);
        //判断数据类型集合中是否有这个数据的类型
        if (clazzList.contains(itemData.getClass())) {
            //如果有这个类型，则返回这个类型所在集合的index
            return clazzList.indexOf(itemData.getClass());
        }
        //如果没有这个类型，则返回NONE_TYPE
        return NONE_TYPE;
    }

    @NonNull
    @Override
    public BindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //根据getItemViewType方法返回的viewType，判断是否是头部
        if (viewType == LOAD_MORE_TYPE) {
            //加载更多布局
            return new BindHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_loadmore, parent, false));
        } else if (viewType != NONE_TYPE) {
            return new BindHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    layoutIds.get(clazzList.get(viewType)), parent, false));
        } else if (dataList.size() == 0) {
            return new BindHolder(emptyView);
        }
        return new BindHolder(new View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull BindHolder holder, int position) {
        if (dataList.size() == 0) return;
        int itemType = holder.getItemViewType();
        if (itemType == LOAD_MORE_TYPE) {
            //加载更多
            holder.getBinding().setVariable(BR.loading, false);
            switch (loadMoreState) {
                case LOADING_MORE:
                    holder.getBinding().setVariable(BR.loading, true);
                    holder.getBinding().setVariable(BR.text, loadMoreText);
                    break;
                case LOAD_SUCCESS:
                    holder.getBinding().setVariable(BR.text, loadSuccessText);
                    break;
                case LOAD_FAIL:
                    holder.getBinding().setVariable(BR.text, loadFailText);
                    break;
                case LOAD_COMPLETE:
                    holder.getBinding().setVariable(BR.text, loadCompleteText);
                    break;
            }
            return;
        }
        if (itemType <= NONE_TYPE) return;
        Class clz = clazzList.get(itemType);
        //item点击事件绑定
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(view -> {
                onItemClickListener.onItemClick(this, view, position);
            });
        }
        //item长按事件绑定
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(view -> {
                onItemLongClickListener.onItemLongClick(this, view, position);
                return true;
            });
        }
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
        if (variableIds.containsKey(clz)) {
            holder.getBinding().setVariable(variableIds.get(clz), dataList.get(position));
        }
        if (quickCovert != null) {
            quickCovert.onCovert(holder.getBinding(), dataList.get(position), position);
        }
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (emptyView != null && dataList.size() == 0) {
            return 1;
        }
        if (onLoadMoreListener != null) {
            //如果真数据大于0，并且有设置加载更多
            return dataList.size() + 1;
        }
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

    /**
     * 加载更多
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
