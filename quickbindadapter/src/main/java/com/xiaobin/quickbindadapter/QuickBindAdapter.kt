package com.xiaobin.quickbindadapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.Integer.min

/**
 * @author xiao bin
 * @data 2019/7/10 最后修订 2022/11/15
 */
open class QuickBindAdapter() : RecyclerView.Adapter<BindHolder>() {

    private val TAG = "QuickBindAdapter"
    private val EMPTY_VIEW_TYPE = -1
    private val LOAD_MORE_TYPE = -2
    private val NONE_VIEW_TYPE = -3
    private var mContext: Context? = null

    //databinding属性名集合
    private val variableIds: MutableMap<Class<*>, Int> = HashMap()

    //item布局集合
    private val layoutType: MutableMap<Class<*>, Int> = HashMap()

    //item类型集合
    private val layoutClass: MutableMap<Int, Class<*>> = HashMap()

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
    private var emptyView: BasePageStateView<*, *, *>? = null

    /**
     * 生命周期，目前的想法只是给dataBinding提供一些用处
     */
    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * 是否允许列表数据不满一页时触发自动加载更多
     * 无论是否设置为true，当数据量少于1时不会触发
     */
    private var enableLoadMoreWhenNoFull = true

    /**
     * 是否自动触发加载更多
     */
    private var autoLoadMore = true

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
            if (onLoadMoreListener == null) return
            val reverseLayout = checkIsReverseLayout()
            val notNeedLoadMore = (reverseLayout && dy > 0) || (!reverseLayout && dy < 0)
            if (notNeedLoadMore || mRecyclerView!!.layoutManager == null || dataCount == 0) {
                //下拉,没有layoutManger,无数据时不触发
                return
            }
            var lastItemIndex = 0
            when (val layoutManager = mRecyclerView!!.layoutManager) {
                is LinearLayoutManager -> {
                    //GridLayoutManager同样走这个
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

                val isOnLoading =
                    loadMoreItemView?.loadMoreState == BaseLoadView.LoadMoreState.LOADING
                if (!isOnLoading && isHasMore) {
                    if (!autoLoadMore) {
                        //不允许自动加载更多，则改变状态为等待用户点击加载更多
                        loadMoreItemView?.isWaitLoading()
                        return
                    }
                    //如果rv不可滑动，并且不允许没满屏加载更多，则直接return
                    if (!enableLoadMoreWhenNoFull) {
                        if (!checkRvCanScroll()) return
                    }
                    //触发加载更多
                    onLoadMoreListener?.onLoadMore()
                    loadMoreItemView?.isLoading()
                }
            }
        }
    }

    constructor(lifecycleOwner: LifecycleOwner?) : this() {
        this.lifecycleOwner = lifecycleOwner
    }

    override fun getItemCount(): Int {
        if (emptyView != null && listData.isEmpty()) {
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
                //GridLayoutManager同样走这个
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

        return if (layoutType.containsKey(itemData.javaClass)) {
            //如果有这个类型，则返回这个类型所在集合的index
            layoutType[itemData.javaClass]!!
        } else {
            //如果没有这个类型，则返回 NULL_VIEW_TYPE
            NONE_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        //根据getItemViewType方法返回的viewType，判断需要用哪种布局
        return when (viewType) {
            LOAD_MORE_TYPE -> {
                if (loadMoreItemView == null) {
                    loadMoreItemView = DefaultLoadView(mContext!!)
                }
                loadMoreItemView!!.createViewHolder(parent, lifecycleOwner) {
                    loadMore()
                }
            }
            EMPTY_VIEW_TYPE -> {
                emptyView!!.createViewHolder(parent, lifecycleOwner)
            }
            else -> {
                if (layoutClass.containsKey(viewType)) {
                    val mClass = layoutClass[viewType]
                    val layoutId = layoutType[mClass]!!
                    if (StaggeredFullSpan::class.java.isAssignableFrom(mClass!!)) {
                        FullSpanBindHolder(
                            DataBindingUtil.inflate(
                                LayoutInflater.from(parent.context),
                                layoutId, parent, false
                            ),
                            lifecycleOwner
                        )
                    } else {
                        BindHolder(
                            DataBindingUtil.inflate(
                                LayoutInflater.from(parent.context),
                                layoutId, parent, false
                            ),
                            lifecycleOwner
                        )
                    }
                } else {
                    BindHolder(
                        TextView(parent.context).apply {
                            text = unknownViewTypeText
                            gravity = Gravity.CENTER_VERTICAL
                            setPadding(28)
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        if (listData.isEmpty()) return
        val itemType = holder.itemViewType
        if (itemType < 0) return
        val clz = layoutClass[itemType]
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
        //不缓存这两种布局
        val pool = recyclerView.recycledViewPool
        pool.setMaxRecycledViews(EMPTY_VIEW_TYPE, 0)
        pool.setMaxRecycledViews(LOAD_MORE_TYPE, 0)

        refreshSpanSizeLookup()
        setupScrollListener()
        checkLoadMoreState()
    }

    /**
     * 刷新SpanSizeLookup
     */
    private fun refreshSpanSizeLookup() {
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
     *
     * 如果可以滑动，说明item数量超出屏幕高度了，不需要这里做处理了
     */
    private fun checkLoadMoreState() {
        var check = checkRvCanScroll()
        if (check || onLoadMoreListener == null || listData.isEmpty()) return
        //逐步检查必要的参数，最后调用onLoadMore
        val state = loadMoreItemView?.loadMoreState ?: BaseLoadView.LoadMoreState.LOADING
        check = isHasMore
                && state != BaseLoadView.LoadMoreState.LOADING
                && mRecyclerView?.layoutManager?.childCount == itemCount
                && getItemViewType(itemCount - 1) == LOAD_MORE_TYPE
        //如果不符合上述条件，或者禁止了不满一页自动加载，或者禁止了自动触发加载，则return
        if (!check || !enableLoadMoreWhenNoFull) return
        if (!autoLoadMore) {
            loadMoreItemView?.isWaitLoading()
        } else {
            loadMoreItemView!!.isLoading()
            onLoadMoreListener!!.onLoadMore()
        }
    }

    private fun checkRvCanScroll(): Boolean {
        if (mRecyclerView == null) return false
        return mRecyclerView!!.canScrollHorizontally(1) ||
                mRecyclerView!!.canScrollHorizontally(-1) ||
                mRecyclerView!!.canScrollVertically(1) ||
                mRecyclerView!!.canScrollVertically(-1)
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
     * 加载更多
     */
    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    private fun checkPageState() {
        if (listData.isEmpty()) {
            emptyView?.setPageState(PageState.Empty)
        } else {
            emptyView?.setPageState(PageState.Finish)
            checkLoadMoreState()
        }
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
    fun getEmptyView(): BasePageStateView<*, *, *>? {
        return emptyView
    }

    /**
     * 获得拉到底之后显示的加载更多布局
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
     * 现在，立刻，马上，加载更多！
     */
    fun loadMore() {
        if (loadMoreItemView?.loadMoreState != BaseLoadView.LoadMoreState.LOADING) {
            onLoadMoreListener?.onLoadMore()
        }
        loadMoreItemView?.isLoading()
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
     * @param newData 全新数据
     */
    fun setNewData(newData: List<*>?) {
        listData.clear()
        newData?.let {
            listData.addAll(it)
        }
        isHasMore = listData.size != 0
        notifyDataSetChanged()
        checkPageState()
    }

    /**
     * 设置新的数据
     *
     * @param newData 全新数据
     */
    fun setNewData(newData: ItemData?) {
        listData.clear()
        newData?.let {
            listData = it
        }
        isHasMore = listData.size != 0
        notifyDataSetChanged()
        checkPageState()
    }

    /**
     * 移动Item
     *
     * @param fromPosition 要移动的Item下标
     * @param toPosition   要移动到的位置下标
     */
    fun movePosition(
        @IntRange(from = 0) fromPosition: Int,
        @IntRange(from = 0) toPosition: Int,
        scrollToThis: Boolean
    ) {
        movePosition(fromPosition, toPosition)
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
    fun movePosition(@IntRange(from = 0) fromPosition: Int, @IntRange(from = 0) toPosition: Int) {
        listData.add(toPosition, listData.removeAt(fromPosition)) //数据更换
        notifyItemMoved(fromPosition, toPosition)
        val minIndex = min(fromPosition, toPosition)
        notifyItemRangeChanged(
            minIndex,
            itemCount - minIndex
        )
    }

    /**
     * 插入单个数据
     *
     * @param index 插入位置
     * @param newData 单个数据，添加到最后
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun insertData(@IntRange(from = 0) index: Int, newData: Any, scrollToThis: Boolean) {
        insertData(index, newData)
        if (scrollToThis) {
            smoothScrollToPosition(index)
        }
    }

    /**
     * 插入单个数据
     *
     * @param index 插入位置
     * @param newData  单个数据
     */
    fun insertData(@IntRange(from = 0) index: Int, newData: Any) {
        listData.add(index, newData)
        isHasMore = true
        notifyItemRangeInserted(index, 1)
        notifyItemRangeChanged(index, itemCount - index)
        compatibilityDataSizeChanged(1)
        checkPageState()
    }

    /**
     * 插入多个数据
     *
     * @param index 插入位置
     * @param newData 多个数据
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun insertData(@IntRange(from = 0) index: Int, newData: Collection<*>, scrollToThis: Boolean) {
        insertData(index, newData)
        if (scrollToThis) {
            smoothScrollToPosition(index)
        }
    }

    /**
     * 插入多个数据
     *
     * @param newData 多个数据
     * @param index 插入位置
     */
    fun insertData(@IntRange(from = 0) index: Int, newData: Collection<*>) {
        listData.addAll(index, newData)
        isHasMore = true
        notifyItemRangeInserted(index, newData.size)
        notifyItemRangeChanged(index, itemCount - index)
        compatibilityDataSizeChanged(newData.size)
        checkPageState()
    }

    /**
     * 添加单个数据
     *
     * @param newData 单个数据，添加到最后
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun addData(newData: Any, scrollToThis: Boolean) {
        addData(newData)
        if (scrollToThis) {
            smoothScrollToPosition(listData.indexOf(newData))
        }
    }

    /**
     * 添加单个数据
     *
     * @param newData 单个数据，添加到最后
     */
    fun addData(newData: Any) {
        listData.add(newData)
        isHasMore = true
        notifyItemInserted(listData.size - 1)
        if (itemCount > dataCount) {
            notifyItemRangeChanged(dataCount, itemCount - dataCount)
        }
        compatibilityDataSizeChanged(1)
        checkPageState()
    }

    /**
     * 添加多个数据
     *
     * @param newData 多个数据
     * @param scrollToThis 是否需要滑动列表到这个位置
     */
    fun addData(newData: Collection<*>, scrollToThis: Boolean) {
        val insertIndex = addData(newData)
        if (scrollToThis) {
            smoothScrollToPosition(insertIndex)
        }
    }

    /**
     * 添加多个数据
     *
     * @param newData 多个数据，添加到最后
     */
    fun addData(newData: Collection<*>): Int {
        this.listData.addAll(newData)
        isHasMore = true
        val insertStart = listData.size - newData.size
        notifyItemRangeInserted(insertStart, newData.size)
        if (itemCount > dataCount) {
            notifyItemRangeChanged(dataCount, itemCount - dataCount)
        }
        compatibilityDataSizeChanged(newData.size)
        checkPageState()
        return insertStart
    }

    /**
     * 已过时，统一使用addData()
     */
    @Deprecated("Please use addData()", replaceWith = ReplaceWith("addData(newData)"))
    fun addDatas(newData: Collection<*>): Int {
        return addData(newData)
    }

    /**
     * 已过时，统一使用addData()
     */
    @Deprecated("Please use addData()", replaceWith = ReplaceWith("addData(newData, scrollToThis)"))
    fun addDatas(newData: Collection<*>, scrollToThis: Boolean) {
        addData(newData, scrollToThis)
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
        notifyItemRangeChanged(position, itemCount - position)
        compatibilityDataSizeChanged(0)
        if (listData.isEmpty()) {
            emptyView?.setPageState(PageState.Empty)
        } else {
            checkPageState()
        }
    }

    /**
     * 清空数据
     */
    fun removeAll() {
        listData.clear()
        notifyDataSetChanged()
        emptyView?.setPageState(PageState.Empty)
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
        layoutClass[layoutId] = clazz
        layoutType[clazz] = layoutId
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
        layoutClass[layoutId] = clazz
        layoutType[clazz] = layoutId
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
     * 如果列表有数据的话，调用这个方法并不会展示 加载中 页面
     * 但是在下一次展示占位图的时候，将会展示 加载中 页面
     */
    fun showLoadPage() {
        showLoadPage(false)
    }

    /**
     * 展示加载中页面
     * @param clearData 是否清空已有数据，如果不清空，并且listData大小不为0，则不会显示 加载中 页面
     */
    fun showLoadPage(clearData: Boolean = false) {
        if (clearData) {
            listData.clear()
            notifyDataSetChanged()
        }
        emptyView?.setPageState(PageState.Loading)
    }

    /**
     * 展示 加载错误 页面
     * 如果列表有数据的话，调用这个方法并不会展示 加载错误 页面
     * 但是在下一次展示占位图的时候，将会展示 加载错误 页面
     */
    fun showErrorPage() {
        showErrorPage(false)
    }

    /**
     * 展示 加载错误 页面
     * @param clearData 是否清空已有数据，如果不清空，并且listData大小不为0，则不会显示 加载错误 页面
     */
    fun showErrorPage(clearData: Boolean = false) {
        if (clearData) {
            listData.clear()
            notifyDataSetChanged()
        }
        emptyView?.setPageState(PageState.Error)
    }

    /**
     * 展示 空数据 页面
     * 如果列表有数据的话，调用这个方法并不会展示 空数据 页面
     * 但是在下一次展示占位图的时候，将会展示 空数据 页面
     */
    fun showEmptyPage() {
        showEmptyPage(false)
    }

    /**
     * 展示 空数据 页面
     * @param clearData 是否清空已有数据，如果不清空，并且listData大小不为0，则不会显示 空数据 页面
     */
    fun showEmptyPage(clearData: Boolean = false) {
        if (clearData) {
            listData.clear()
            notifyDataSetChanged()
        }
        emptyView?.setPageState(PageState.Empty)
    }

    /**
     * 设置是否触底自动触发加载更多
     */
    fun setAutoLoadMore(enable: Boolean) {
        autoLoadMore = enable
    }

    /**
     * 设置是否开启 当列表数据没有充满rv的情况下，也自动加载更多
     * 如果关闭了触底自动加载更多，那么这个方法将不在起作用
     */
    fun enableLoadMoreWhenPageNotFull(canLoadWhenPageNotFull: Boolean): QuickBindAdapter {
        enableLoadMoreWhenNoFull = canLoadWhenPageNotFull
        return this
    }

    fun setQuickBind(bind: QuickBind?): QuickBindAdapter {
        quickBind = bind
        return this
    }

    fun setEmptyView(view: BasePageStateView<*, *, *>?): QuickBindAdapter {
        emptyView = view
        return this
    }

    /**
     * 使用默认的空列表占位布局控制器
     */
    fun setEmptyView(context: Context): QuickBindAdapter {
        return setEmptyView(context, PageState.Loading)
    }

    /**
     * 使用默认的空列表占位布局控制器
     */
    fun setEmptyView(context: Context, defaultPageState: PageState): QuickBindAdapter {
        emptyView = DefaultEmptyStatePage(context).apply {
            setDefaultPage(defaultPageState)
        }
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

    companion object {

        var unknownViewTypeText = "Undefined data type!"

        fun create(): QuickBindAdapter {
            return QuickBindAdapter()
        }

        fun create(lifecycleOwner: LifecycleOwner): QuickBindAdapter {
            return QuickBindAdapter(lifecycleOwner)
        }

    }
}