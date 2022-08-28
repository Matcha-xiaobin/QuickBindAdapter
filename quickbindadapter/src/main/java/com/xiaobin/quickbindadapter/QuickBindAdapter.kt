package com.xiaobin.quickbindadapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.Integer.min
import kotlin.math.abs

/**
 * @author 小斌
 * @data 2019/7/10
 */
open class QuickBindAdapter() : RecyclerView.Adapter<BindHolder>() {

    private val TAG = "QuickBindAdapter"
    private val EMPTY_VIEW_TYPE = -1
    private val LOAD_MORE_TYPE = -2
    private val NONE_VIEW_TYPE = -3
    private var mContext: Context? = null

    //数据类型集合
    private val clazzList: MutableList<Class<*>> = ArrayList()

    //databinding属性名集合
    private val variableIds: MutableMap<Class<*>, Int> = HashMap()

    //item布局集合
    private val layoutIds: MutableMap<Class<*>, Int> = HashMap()

    //需要点击事件，长按事件监听的viewId集合
    private val clickListenerIds: MutableMap<Class<*>, List<Int>> = HashMap()
    private val longClickListenerIds: MutableMap<Class<*>, List<Int>> = HashMap()

    /**
     * 加载更多item样式
     */
    private var loadMoreItemView: BaseLoadView<*>? = null

    /**
     * 空数据占位图
     */
    private var emptyView: BasePlaceholder<*, *, *>? = null

    /**
     * 生命周期，目前的想法只是给dataBinding提供一些用处
     */
    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * 是否允许列表数据不满一页时触发自动加载更多
     * 无论是否设置为true，当数据量少于1时不会触发
     */
    private var loadMoreWhenItemsNoFullScreen = true

    /**
     * 获得全部item数据，不包含底部的加载更多item
     */
    private var listData: ItemData = ItemData()

    /**
     * item的数据绑定回调，除了dataBinding本身的绑定之外的其它特殊操作
     */
    private var quickBind: QuickBind? = null

    /**
     * 设置加载更多监听
     * 如果没有配置自定义的加载更多样式，则初始化默认的
     */
    private var onLoadMoreListener: OnLoadMoreListener? = null
        set(value) {
            field = value
            setupScrollListener()
        }
    private var mRecyclerView: RecyclerView? = null

    private var isHasMore = true

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val reverseLayout = checkIsReverseLayout()
            val notNeedLoadMore = (reverseLayout && dy > 0) || (!reverseLayout && dy < 0)
            if (notNeedLoadMore || mRecyclerView!!.layoutManager == null || dataCount == 0) {
                //下拉,没有layoutManger,无数据时不触发
                return
            }
            var lastItemIndex = 0
            when (val layoutManager = mRecyclerView!!.layoutManager) {
                is LinearLayoutManager -> {
                    if (layoutManager.childCount < 1) return
                    lastItemIndex = layoutManager.findLastVisibleItemPosition()
                }
                is GridLayoutManager -> {
                    if (layoutManager.childCount < 1) return
                    lastItemIndex = layoutManager.findLastVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    val positions = IntArray(layoutManager.spanCount)
                    layoutManager.findLastVisibleItemPositions(positions)
                    lastItemIndex = getTheBiggestNumber(positions)
                }
            }
            if (getItemViewType(lastItemIndex) == LOAD_MORE_TYPE) {
                if (dataCount == 0) {
                    if (loadMoreItemView == null && mContext != null) {
                        loadMoreItemView = DefaultLoadView(mContext!!)
                    }
                    loadMoreItemView?.isNoMoreData()
                    return
                }
                //触发加载更多
                if (onLoadMoreListener != null && isHasMore) {
                    if (loadMoreItemView?.loadMoreState != BaseLoadView.LoadMoreState.LOADING) {
                        onLoadMoreListener?.onLoadMore()
                    }
                    loadMoreItemView?.isLoading()
                }
            }
        }
    }

    constructor(lifecycleOwner: LifecycleOwner?) : this() {
        this.lifecycleOwner = lifecycleOwner
    }

    override fun getItemCount(): Int {
        if (onLoadMoreListener != null && listData.isEmpty()) {
            return 1
        }
        return if (onLoadMoreListener != null && listData.isNotEmpty()) {
            //如果真数据大于0，并且有设置加载更多
            listData.size + 1
        } else {
            listData.size
        }
    }

    private fun checkIsReverseLayout(): Boolean {
        return when (val layoutManager = mRecyclerView?.layoutManager) {
            is LinearLayoutManager -> {
                layoutManager.reverseLayout
            }
            is GridLayoutManager -> {
                layoutManager.reverseLayout
            }
            is StaggeredGridLayoutManager -> {
                layoutManager.reverseLayout
            }
            else -> false
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (listData.isNotEmpty() && onLoadMoreListener != null && position >= listData.size) {
            //如果设置了加载更多功能，则最后一个为加载更多的布局
            return LOAD_MORE_TYPE
        }
        if (emptyView != null && listData.isEmpty()) {
            return EMPTY_VIEW_TYPE
        }
        if (position < 0) {
            return NONE_VIEW_TYPE
        }
        //得到itemData的index，然后得到对应的数据
        //判断数据类型集合中是否有这个数据的类型
        val itemData = getItemData(position) ?: return NONE_VIEW_TYPE
        val typeIndex = clazzList.indexOf(itemData.javaClass)
        return if (typeIndex >= 0) {
            //如果有这个类型，则返回这个类型所在集合的index
            typeIndex
        } else {
            //如果没有这个类型，则返回 NULL_VIEW_TYPE
            NONE_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        //根据getItemViewType方法返回的viewType，判断需要用哪种布局
        if (viewType == LOAD_MORE_TYPE) {
            //加载更多布局
            if (loadMoreItemView == null) {
                loadMoreItemView = DefaultLoadView(mContext!!)
            }
            return loadMoreItemView!!.createViewHolder(parent, lifecycleOwner)
        } else if (viewType >= 0) {
            val mClass = clazzList[viewType]
            val layoutId = layoutIds[mClass]!!
            if (StaggeredFullSpan::class.java.isAssignableFrom(mClass)) {
                return FullSpanBindHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        layoutId, parent, false
                    ),
                    lifecycleOwner
                )
            }
            return BindHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    layoutId, parent, false
                ),
                lifecycleOwner
            )
        } else if (listData.isEmpty() && emptyView != null) {
            return emptyView!!.createViewHolder(parent, lifecycleOwner)
        }
        return BindHolder(View(parent.context))
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        if (listData.isEmpty()) return
        val itemType = holder.itemViewType
        if (itemType < 0) return
        val clz = clazzList[itemType]
        val itemData = getItemData(position) ?: return
        //item点击事件绑定
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener { view ->
                onItemClickListener?.onClick(this, view, itemData, position)
            }
        }
        //item长按事件绑定
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener { view ->
                onItemLongClickListener?.onLongClick(this, view, itemData, position) == true
            }
        }
        //子控件点击事件
        if (clickListenerIds.containsKey(clz)) {
            for (id in clickListenerIds[clz]!!) {
                holder.itemView.findViewById<View>(id).setOnClickListener { view ->
                    onItemChildClickListener?.onClick(this, view, itemData, position)
                }
            }
        }
        //子控件长按事件
        if (longClickListenerIds.containsKey(clz)) {
            for (id in longClickListenerIds[clz]!!) {
                holder.itemView.findViewById<View>(id).setOnLongClickListener { view ->
                    onItemChildLongClickListener?.onLongClick(
                        this,
                        view,
                        itemData,
                        position
                    ) == true
                }
            }
        }
        if (variableIds.containsKey(clz)) {
            holder.binding?.setVariable(variableIds[clz]!!, itemData)
        }
        quickBind?.onBind(holder.binding!!, itemData, position)
        holder.binding?.executePendingBindings()
    }

    override fun onViewAttachedToWindow(holder: BindHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp is StaggeredGridLayoutManager.LayoutParams) {
            if (holder.itemViewType == LOAD_MORE_TYPE
                || holder.itemViewType == EMPTY_VIEW_TYPE
                || holder is FullSpanBindHolder
            ) {
                lp.isFullSpan = true
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mContext = recyclerView.context
        mRecyclerView = recyclerView
        refreshSpanSizeLookup()
        setupScrollListener()
        checkLoadMoreState()
    }

    /**
     * 刷新SpanSizeLookup
     */
    fun refreshSpanSizeLookup() {
        mRecyclerView?.let { rv ->
            val layoutManager = rv.layoutManager
            if (layoutManager is GridLayoutManager) {
                val rvSpanSizeLookup = layoutManager.spanSizeLookup
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (getItemViewType(position) == LOAD_MORE_TYPE ||
                            getItemViewType(position) == EMPTY_VIEW_TYPE
                        ) {
                            //空布局 or 加载更多布局
                            layoutManager.spanCount
                        } else {
                            rvSpanSizeLookup.getSpanSize(position)
                        }
                    }
                }
            } else if (layoutManager == null) {
                throw NullPointerException("请在完成初始化前先给RecyclerView设置LayoutManager！")
            }
        }
    }

    private fun setupScrollListener() {
        if (onLoadMoreListener == null) return
        //先移除之前的，在添加，防止重复添加
        mRecyclerView?.removeOnScrollListener(onScrollListener)
        mRecyclerView?.addOnScrollListener(onScrollListener)
    }

    private fun getTheBiggestNumber(numbers: IntArray?): Int {
        if (numbers == null || numbers.isEmpty()) {
            return -1
        }
        return numbers.max()
    }

    /**
     * 检查RV是否可以滑动，如果不可滑动(视为数据没有占满一页的情况)，并且开启了不满一页也可触发加载更多
     * 则触发加载更多回调
     */
    private fun checkLoadMoreState() {
        //逐步检查必要的参数，最后调用onLoadMore
        var check = isHasMore
                && loadMoreItemView != null
                && dataCount > 0
                && loadMoreWhenItemsNoFullScreen
                && loadMoreItemView!!.loadMoreState != BaseLoadView.LoadMoreState.LOADING
                && mRecyclerView != null
                && mRecyclerView!!.layoutManager != null
                //必须做这个判断，否则容易导致多次调用
                && mRecyclerView!!.layoutManager!!.childCount == itemCount
                && getItemViewType(itemCount - 1) == LOAD_MORE_TYPE
        if (!check) return
        //检查RV是否可以滑动，如果不可滑动(视为数据没有占满一页的情况)
        check = !mRecyclerView!!.canScrollHorizontally(1) ||
                !mRecyclerView!!.canScrollHorizontally(-1) ||
                !mRecyclerView!!.canScrollVertically(1) ||
                !mRecyclerView!!.canScrollVertically(-1)
        if (!check) return
        loadMoreItemView!!.isLoading()
        onLoadMoreListener!!.onLoadMore()
    }

    /**
     * 如果变动的数据大小和实际数据大小一致，则刷新整个列表
     *
     * @param size 变动的数据大小
     */
    private fun compatibilityDataSizeChanged(size: Int) {
        val dataSize = listData.size
        if (dataSize == size) {
            notifyDataSetChanged()
        }
    }

    /**
     * 平滑滚动到某个item
     */
    private fun smoothScrollToPosition(position: Int) {
        mRecyclerView?.smoothScrollToPosition(position)
    }

    /**
     * item事件
     */
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    /**
     * 子控件事件
     */
    private var onItemChildClickListener: OnItemClickListener? = null
    private var onItemChildLongClickListener: OnItemLongClickListener? = null

    /**
     * 点击事件
     */
    interface OnItemClickListener {
        fun onClick(adapter: QuickBindAdapter, view: View, data: Any, position: Int)
    }

    /**
     * 长按事件
     */
    interface OnItemLongClickListener {
        fun onLongClick(
            adapter: QuickBindAdapter,
            view: View,
            data: Any,
            position: Int
        ): Boolean
    }

    /**
     * 加载更多
     */
    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    /******************************  **************************************/

    /**
     * 获得整个列表的数据
     */
    fun getListData(): ItemData {
        return listData
    }

    /**
     * 获得空数据占位图控制器
     */
    fun getEmptyView(): BasePlaceholder<*, *, *>? {
        return emptyView
    }

    /**
     * 获得拉到低之后的加载更多布局
     */
    fun getLoadMoreView(): BaseLoadView<*>? {
        return loadMoreItemView
    }

    /**
     * 加载更多完成
     */
    fun loadMoreSuccess() {
        if (dataCount == 0 || loadMoreItemView == null || !isHasMore) return
        loadMoreItemView!!.isLoadMoreSuccess()
    }

    /**
     * 加载更多完成，没有更多数据了
     */
    fun loadMoreSuccessAndNoMore() {
        if (dataCount == 0 || loadMoreItemView == null) return
        isHasMore = false
        loadMoreItemView!!.isNoMoreData()
    }

    /**
     * 加载更多失败了
     */
    fun loadMoreFailed() {
        if (dataCount == 0 || loadMoreItemView == null || !isHasMore) return
        loadMoreItemView!!.isLoadMoreFailed()
    }

    /**
     * 设置生命周期所有者
     * 用于特定用途，这个lifecycleOwner会给到每一个item
     *
     * @param lifecycleOwner
     */
    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner?): QuickBindAdapter {
        this.lifecycleOwner = lifecycleOwner
        return this
    }

    /**
     * 移除空数据占位图
     */
    fun removeEmptyView() {
        emptyView = null
    }

    /**
     * 设置新的数据
     *
     * @param data 全新数据
     */
    fun setNewData(data: List<*>?) {
        listData.clear()
        data?.let {
            listData.addAll(it)
        }
        isHasMore = listData.size != 0
        notifyDataSetChanged()
        if (listData.isEmpty()) {
            emptyView?.setPlaceholderAction(PlaceholderAction.ShowEmptyPage)
        } else {
            checkLoadMoreState()
        }
    }

    /**
     * 设置新的数据
     *
     * @param data 全新数据
     */
    fun setNewData(data: ItemData?) {
        listData.clear()
        data?.let {
            listData = it
        }
        isHasMore = listData.size != 0
        notifyDataSetChanged()
        if (listData.isEmpty()) {
            emptyView?.setPlaceholderAction(PlaceholderAction.ShowEmptyPage)
        } else {
            checkLoadMoreState()
        }
    }

    /**
     * 移动Item
     *
     * @param fromPosition 要移动的Item下标
     * @param toPosition   要移动到的位置下标
     */
    fun movedPositions(fromPosition: Int, toPosition: Int, scrollToThis: Boolean) {
        movedPositions(fromPosition, toPosition)
        if (scrollToThis) {
            smoothScrollToPosition(toPosition)
        }
    }

    /**
     * 移动Item
     *
     * @param fromPosition 要移动的Item下标
     * @param toPosition   要移动到的位置下标
     */
    fun movedPositions(fromPosition: Int, toPosition: Int) {
        listData.add(toPosition, listData.removeAt(fromPosition)) //数据更换
        notifyItemMoved(fromPosition, toPosition)
        notifyItemRangeChanged(
            min(fromPosition, toPosition),
            abs(fromPosition - toPosition) + 1
        )
    }

    /**
     * 添加单个数据
     *
     * @param data 单个数据，添加到最后
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun addData(data: Any, scrollToThis: Boolean) {
        addData(data)
        if (scrollToThis) {
            smoothScrollToPosition(listData.indexOf(data))
        }
    }

    /**
     * 添加单个数据
     *
     * @param data 单个数据，添加到最后
     */
    fun addData(data: Any) {
        listData.add(data)
        notifyItemInserted(listData.size)
        compatibilityDataSizeChanged(1)
        checkLoadMoreState()
    }

    /**
     * 插入单个数据
     *
     * @param index 插入位置
     * @param data 单个数据，添加到最后
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun insertData(index: Int, data: Any, scrollToThis: Boolean) {
        insertData(index, data)
        if (scrollToThis) {
            smoothScrollToPosition(index)
        }
    }

    /**
     * 插入单个数据
     *
     * @param index 插入位置
     * @param data  单个数据
     */
    fun insertData(index: Int, data: Any) {
        listData.add(index, data)
        notifyItemInserted(index)
        notifyItemRangeChanged(index, listData.size - index)
        compatibilityDataSizeChanged(1)
        checkLoadMoreState()
    }

    /**
     * 插入多个数据
     *
     * @param index 插入位置
     * @param data 多个数据
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun insertDatas(index: Int, data: ItemData, scrollToThis: Boolean) {
        insertDatas(index, data)
        if (scrollToThis) {
            smoothScrollToPosition(index)
        }
    }

    /**
     * 插入多个数据
     *
     * @param datas 多个数据
     * @param index 插入位置
     */
    fun insertDatas(index: Int, datas: ItemData) {
        this.listData.addAll(index, datas)
        notifyItemRangeChanged(index, datas.size - index)
        compatibilityDataSizeChanged(datas.size)
        checkLoadMoreState()
    }

    /**
     * 插入多个数据
     *
     * @param index 插入位置
     * @param data 多个数据
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun insertDatas(index: Int, data: List<*>, scrollToThis: Boolean) {
        insertDatas(index, data)
        if (scrollToThis) {
            smoothScrollToPosition(index)
        }
    }

    /**
     * 插入多个数据
     *
     * @param datas 多个数据
     * @param index 插入位置
     */
    fun insertDatas(index: Int, datas: List<*>) {
        this.listData.addAll(index, datas)
        notifyItemRangeChanged(index, itemCount - index)
        compatibilityDataSizeChanged(datas.size)
        checkLoadMoreState()
    }

    /**
     * 插入多个数据
     *
     * @param data 多个数据
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun addDatas(data: List<*>, scrollToThis: Boolean) {
        val insertIndex = addDatas(data)
        if (scrollToThis) {
            smoothScrollToPosition(insertIndex)
        }
    }

    /**
     * 添加数据
     *
     * @param datas 多个数据，添加到最后
     */
    fun addDatas(datas: List<*>): Int {
        val lastIndex = itemCount
        this.listData.addAll(datas)
        notifyItemRangeInserted(lastIndex - 1, datas.size)
        compatibilityDataSizeChanged(datas.size)
        checkLoadMoreState()
        return lastIndex
    }

    /**
     * 插入多个数据
     *
     * @param data 多个数据
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun addDatas(data: ItemData, scrollToThis: Boolean) {
        val insertIndex = addDatas(data)
        if (scrollToThis) {
            smoothScrollToPosition(insertIndex)
        }
    }

    /**
     * 添加数据
     *
     * @param datas 多个数据，添加到最后
     */
    fun addDatas(datas: ItemData): Int {
        val lastIndex = itemCount
        this.listData.addAll(datas)
        notifyItemRangeInserted(lastIndex - 1, datas.size)
        compatibilityDataSizeChanged(datas.size)
        checkLoadMoreState()
        return lastIndex
    }

    /**
     * 移除某个item
     *
     * @param position 位置
     */
    fun remove(position: Int) {
        if (listData.size <= position) {
            return
        }
        listData.removeAt(position)
        notifyItemRemoved(position)
        compatibilityDataSizeChanged(0)
        notifyItemRangeChanged(position, listData.size - position)
        if (listData.isEmpty()) {
            emptyView?.setPlaceholderAction(PlaceholderAction.ShowEmptyPage)
        } else {
            checkLoadMoreState()
        }
    }

    /**
     * 清空数据
     */
    fun removeAll() {
        listData.clear()
        notifyDataSetChanged()
        emptyView?.setPlaceholderAction(PlaceholderAction.ShowEmptyPage)
    }

    /**
     * 插入多个数据
     *
     * @param data 多个数据
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun replace(position: Int, itemData: Any, scrollToThis: Boolean) {
        if (listData.size <= position) {
            addData(itemData, scrollToThis)
            return
        }
        replace(position, itemData)
        if (scrollToThis) {
            smoothScrollToPosition(position)
        }
    }

    /**
     * 替换item内容
     *
     * @param position 位置
     * @param itemData 单个数据
     */
    fun replace(position: Int, itemData: Any) {
        if (listData.size <= position) {
            addData(itemData)
            return
        }
        listData[position] = itemData
        notifyItemChanged(position)
    }

    /**
     * 绑定布局
     *
     * @param clazz          数据类型
     * @param layoutId       布局ID
     * @param bindVariableId DataBinding BR
     * @return 这个对象
     */
    fun bind(clazz: Class<*>, @LayoutRes layoutId: Int, bindVariableId: Int): QuickBindAdapter {
        if (!clazzList.contains(clazz)) {
            clazzList.add(clazz)
        }
        layoutIds[clazz] = layoutId
        variableIds[clazz] = bindVariableId
        return this
    }

    /**
     * 绑定布局
     *
     * @param clazz    数据类型
     * @param layoutId 布局ID
     * @return 这个对象
     */
    fun bind(clazz: Class<*>, @LayoutRes layoutId: Int): QuickBindAdapter {
        if (!clazzList.contains(clazz)) {
            clazzList.add(clazz)
        }
        layoutIds[clazz] = layoutId
        return this
    }

    /**
     * 添加子控件点击监听
     *
     * @param clazz  数据类型
     * @param viewId 控件ID，多个
     * @return 这个对象
     */
    fun addClicks(clazz: Class<*>, @IdRes vararg viewId: Int): QuickBindAdapter {
        val ids: MutableList<Int> = ArrayList(viewId.size)
        for (id in viewId) {
            ids.add(id)
        }
        clickListenerIds[clazz] = ids
        return this
    }

    /**
     * 添加子控件长按监听
     *
     * @param clazz  数据类型
     * @param viewId 控件ID，多个
     * @return 这个对象
     */
    fun addLongClicks(clazz: Class<*>, @IdRes vararg viewId: Int): QuickBindAdapter {
        val ids: MutableList<Int> = ArrayList(viewId.size)
        for (id in viewId) {
            ids.add(id)
        }
        longClickListenerIds[clazz] = ids
        return this
    }

    /**
     * 获取指定item内容
     *
     * @param position 位置
     * @return 这个位置的数据
     */
    fun getItemData(position: Int): Any? {
        if (position < 0 || position >= listData.size) {
            return null
        }
        return listData[position]
    }

    /**
     * 获取数据大小
     *
     * @return 数据大小
     */
    val dataCount: Int
        get() = listData.size

    /**
     * 展示加载中页面
     * @param clearData 是否清空已有数据，如果不清空，并且listData大小不为0，则不会显示加载中页面
     */
    fun showLoadPage(clearData: Boolean = false) {
        if (clearData) {
            listData.clear()
            notifyDataSetChanged()
        }
        emptyView?.setPlaceholderAction(PlaceholderAction.ShowLoadingPage)
    }

    /**
     * 展示错误页面
     * @param clearData 是否清空已有数据，如果不清空，并且listData大小不为0，则不会显示错误页面
     */
    fun showErrorPage(clearData: Boolean = false) {
        if (clearData) {
            listData.clear()
            notifyDataSetChanged()
        }
        emptyView?.setPlaceholderAction(PlaceholderAction.ShowErrPage)
    }

    /**
     * 展示空数据页面
     * @param clearData 是否清空已有数据，如果不清空，并且listData大小不为0，则不会显示空数据页面
     */
    fun showEmptyPage(clearData: Boolean = false) {
        if (clearData) {
            listData.clear()
            notifyDataSetChanged()
        }
        emptyView?.setPlaceholderAction(PlaceholderAction.ShowEmptyPage)
    }

    fun canLoadMoreWhenPageNotFull(canLoadWhenPageNotFull: Boolean): QuickBindAdapter {
        loadMoreWhenItemsNoFullScreen = canLoadWhenPageNotFull
        return this
    }

    fun setQuickBind(bind: QuickBind?): QuickBindAdapter {
        quickBind = bind
        return this
    }

    fun setEmptyView(view: BasePlaceholder<*, *, *>?): QuickBindAdapter {
        emptyView = view
        return this
    }

    fun setLoadMoreItemView(view: BaseLoadView<*>?): QuickBindAdapter {
        loadMoreItemView = view
        return this
    }

    fun setOnLoadMoreListener(loadMoreListener: OnLoadMoreListener?): QuickBindAdapter {
        onLoadMoreListener = loadMoreListener
        return this
    }

    fun setOnItemClickListener(listener: OnItemClickListener): QuickBindAdapter {
        onItemClickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener): QuickBindAdapter {
        onItemLongClickListener = listener
        return this
    }

    fun setOnItemChildClickListener(listener: OnItemClickListener): QuickBindAdapter {
        onItemChildClickListener = listener
        return this
    }

    fun setOnItemChildLongClickListener(listener: OnItemLongClickListener): QuickBindAdapter {
        onItemChildLongClickListener = listener
        return this
    }

    fun setLoadMoreListener(listener: OnItemLongClickListener): QuickBindAdapter {
        onItemChildLongClickListener = listener
        return this
    }

    companion object {

        fun create(): QuickBindAdapter {
            return QuickBindAdapter()
        }

        fun create(lifecycleOwner: LifecycleOwner): QuickBindAdapter {
            return QuickBindAdapter(lifecycleOwner)
        }

    }
}