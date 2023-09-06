package com.xiaobin.quickbindadapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xiaobin.quickbindadapter.interfaces.StaggeredFullSpan
import com.xiaobin.quickbindadapter.paging.EmptyPageAdapter
import com.xiaobin.quickbindadapter.view.BaseLoadView
import com.xiaobin.quickbindadapter.view.BasePageStateView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Paging 分页专用适配器，增删改需要修改数据源头的方式实现
 * 这里只提供与QuickBindAdapter一样的多种布局实现
 * 由于技术有限，这里限制了数据类型
 * 加载更多条请使用 扩展方法： withLoadStateHeaderAndFooter 实现
 * @see withLoadStateHeaderAndFooter
 * @see withLoadStateHeader
 * @see withLoadStateFooter
 *
 * 如果泛型 T 定义了 具体的类型将失去多布局的主要意义，建议使用 Any
 * 当然，固定一种数据类型也可以使用这个适配器，可以方便随时切换不同的布局
 * 比如从一行1个item，变成1行2个、3个甚至更多个，不同数量使用不同的布局适配
 */
class QuickBindPagingAdapter<T : Any>(
    itemCallback: DiffUtil.ItemCallback<T>,
    private val lifecycleOwner: LifecycleOwner? = null,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PagingDataAdapter<T, BindHolder>(itemCallback, mainDispatcher, workerDispatcher) {

    // 布局ID - 数据类型 互相绑定
    private val itemViewTypes = HashMap<Class<*>, Int>()
    private val itemBindVal = HashMap<Class<*>, Int>()

    private val UNKNOWN_VIEW_TYPE = -101

    private val UNKNOWN_VIEW_TYPE_TEXT = "Undefined data type!"

    private var mRecyclerView: RecyclerView? = null

    private var curEmptyAdapter: EmptyPageAdapter? = null
    private var concatAdapter: ConcatAdapter? = null

    /**
     * 请给RecyclerView设置这个adapter, 参考:
     * recyclerView.adapter = pagingAdapter.adapter
     */
    val adapter: RecyclerView.Adapter<*>
        get() {
            return if (concatAdapter == null) this else concatAdapter!!
        }

    /**
     * 启用状态View, 包括 底部加载更多状态，以及占满RV的空数据缺省页
     * 请在调用这个方法后，重新给RecyclerView设置适配器
     * 请设置 this.adapter
     * @see adapter 给 recyclerView设置的适配器
     * @param context 上下文对象
     * @param emptyStatePage 自定义 空数据占位 View
     * @param loadStateItem 自定义底部 加载更多状态 View
     * @param enableEmptyPage 是否显示 空数据占位 布局
     * @param enableLoadMoreItem 是否显示底部 加载更多状态 View
     * @param retry 点击 重新加载 数据回调
     */
    fun enableStatusView(
        context: Context,
        emptyStatePage: BasePageStateView<*, *, *>? = null,
        loadStateItem: BaseLoadView<*>? = null,
        enableEmptyPage: Boolean = true,
        enableLoadMoreItem: Boolean = false,
        retry: () -> Unit = {}
    ) {
        val getItemCount: () -> Int = {
            itemCount
        }
        curEmptyAdapter?.let {
            concatAdapter?.removeAdapter(it)
        }
        curEmptyAdapter = EmptyPageAdapter(
            context,
            getItemCount,
            emptyStatePage,
            loadStateItem,
            lifecycleOwner,
            enableEmptyPage,
            enableLoadMoreItem,
            retry
        )
        curEmptyAdapter?.let {
            concatAdapter = withLoadStateFooter(it)
        }
    }

    /**
     * 绑定 数据类型 和 视图ID
     */
    fun bind(clazz: Class<*>, @LayoutRes layoutId: Int, brId: Int = 0) {
        itemViewTypes[clazz] = layoutId
        itemBindVal[clazz] = brId
        refreshScreenItems(clazz)
    }

    /**
     * 刷新当前可见Item
     */
    private fun refreshScreenItems(clazz: Class<*>?) {
        mRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager?.let { layoutManager ->
                var start = 0
                var end = Math.max(0, itemCount - 1)
                when (layoutManager) {
                    is LinearLayoutManager -> {
                        start = layoutManager.findFirstVisibleItemPosition()
                        end = layoutManager.findLastVisibleItemPosition()
                    }

                    is StaggeredGridLayoutManager -> {
                        val firstIndexArray = layoutManager.findFirstVisibleItemPositions(null)
                        val lastIndexArray = layoutManager.findLastVisibleItemPositions(null)
                        if (firstIndexArray.isNotEmpty() && lastIndexArray.isNotEmpty()) {
                            firstIndexArray.sort()
                            lastIndexArray.sort()
                            start = firstIndexArray.first()
                            end = lastIndexArray.last()
                        }
                    }
                }
                for (position in start..end) {
                    if (position < itemCount) {
                        if (clazz == null || getItem(position)?.javaClass == clazz) {
                            notifyItemChanged(position)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position >= itemCount) return super.getItemViewType(position)
        val data = getItem(position) ?: return UNKNOWN_VIEW_TYPE
        return itemViewTypes[data::class.java] ?: UNKNOWN_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        var mClass: Class<*>? = null
        if (viewType != UNKNOWN_VIEW_TYPE) {
            for (itemViewType in itemViewTypes) {
                if (itemViewType.value == viewType) {
                    mClass = itemViewType.key
                    break
                }
            }
        }
        return if (mClass != null) {
            val isFullSpan = StaggeredFullSpan::class.java.isAssignableFrom(mClass)
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                viewType,
                parent,
                false
            )
            BindHolder(
                binding = binding,
                lifecycleOwner = null,
                fullSpan = isFullSpan
            )
        } else {
            BindHolder(TextView(parent.context).apply {
                text = UNKNOWN_VIEW_TYPE_TEXT
                gravity = Gravity.CENTER_VERTICAL
                ViewCompat.setPaddingRelative(this, 12, 12, 12, 12)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })
        }
    }

    override fun onBindViewHolder(
        holder: BindHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val itemViewType = getItemViewType(position)
        if (itemViewType == UNKNOWN_VIEW_TYPE) {
            return
        }
        if (null != quickBindPayloads) {
            val data = getItem(position) ?: return
            quickBindPayloads?.invoke(this, holder.binding ?: return, data, position, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        if (itemViewType == UNKNOWN_VIEW_TYPE) {
            return
        }
        val data = getItem(position) ?: return
        itemBindVal[data::class.java]?.let { brId ->
            if (brId != 0) {
                holder.binding?.setVariable(brId, data)
            }
        }
        quickBind?.invoke(this, holder.binding ?: return, data, position)
        if (onItemClickListener != null) {
            holder.binding?.root?.setOnClickListener {
                onItemClickListener?.invoke(this, data, position)
            }
        } else {
            holder.binding?.root?.setOnClickListener(null)
        }
        if (onItemLongClickListener != null) {
            holder.binding?.root?.setOnLongClickListener {
                return@setOnLongClickListener onItemLongClickListener?.invoke(
                    this,
                    data,
                    position
                ) ?: false
            }
        } else {
            holder.binding?.root?.setOnLongClickListener(null)
        }
        if (onItemChildClickListener != null && childClicks.isNotEmpty()) {
            childClicks.forEach { viewId ->
                holder.binding?.root?.findViewById<View>(viewId)?.setOnClickListener { childView ->
                    onItemChildClickListener?.invoke(
                        this,
                        childView,
                        data,
                        position
                    )
                }
            }
        } else {
            onItemChildClickListener = null
        }
        if (onItemChildLongClickListener != null && childLongClicks.isNotEmpty()) {
            childLongClicks.forEach { viewId ->
                holder.binding?.root?.findViewById<View>(viewId)
                    ?.setOnLongClickListener { childView ->
                        return@setOnLongClickListener onItemChildLongClickListener?.invoke(
                            this,
                            childView,
                            data,
                            position
                        ) ?: false
                    }
            }
        } else {
            onItemChildLongClickListener = null
        }
    }

    override fun onViewAttachedToWindow(holder: BindHolder) {
        super.onViewAttachedToWindow(holder)
        //时刻检查spanSizeLookup是否需要覆盖自定义的
        refreshSpanSizeLookup()
        val lp = holder.itemView.layoutParams
        if (lp is StaggeredGridLayoutManager.LayoutParams) {
            if (holder.fullSpan) {
                lp.isFullSpan = true
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecyclerView = recyclerView

        //更新lookup
        refreshSpanSizeLookup()
    }

    private var customLookUp: GridLayoutManager.SpanSizeLookup? = null

    private val spanSizeLookUp = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            var itemViewType = adapter.getItemViewType(position)
            if (adapter is ConcatAdapter) {
                //获取真实的itemViewType
                itemViewType = (adapter as ConcatAdapter).getWrappedAdapterAndPosition(position)
                    .first.getItemViewType(position)
            }
            return if (itemViewType == UNKNOWN_VIEW_TYPE
                || itemViewType == EmptyPageAdapter.LOAD_VIEW_TYPE
                || itemViewType == EmptyPageAdapter.EMPTY_VIEW_TYPE
            ) {
                (mRecyclerView?.layoutManager as? GridLayoutManager)?.spanCount ?: 1
            } else {
                customLookUp?.getSpanSize(position) ?: 1
            }
        }
    }

    /**
     * 刷新SpanSizeLookup
     */
    private fun refreshSpanSizeLookup() {
        mRecyclerView?.let { rv ->
            val layoutManager = rv.layoutManager
            if (layoutManager is GridLayoutManager) {
                val rvSpanSizeLookup = layoutManager.spanSizeLookup
                if (rvSpanSizeLookup != spanSizeLookUp) {
                    //需要重新覆盖spanSizeLookup
                    customLookUp = rvSpanSizeLookup
                    layoutManager.spanSizeLookup = spanSizeLookUp
                }
            } else if (layoutManager == null) {
                throw NullPointerException("请在完成初始化前先给RecyclerView设置LayoutManager！")
            }
        }
    }

    var quickBind: ((adapter: QuickBindPagingAdapter<T>, mBinding: ViewDataBinding, data: T, position: Int) -> Unit)? =
        null

    var quickBindPayloads: ((
        adapter: QuickBindPagingAdapter<T>,
        mBinding: ViewDataBinding,
        data: T,
        position: Int,
        payloads: MutableList<Any>
    ) -> Unit)? = null

    var onItemClickListener: ((adapter: QuickBindPagingAdapter<T>, data: T, position: Int) -> Unit)? =
        null

    var onItemLongClickListener: ((adapter: QuickBindPagingAdapter<T>, data: T, position: Int) -> Boolean)? =
        null

    private var onItemChildClickListener: ((
        adapter: QuickBindPagingAdapter<T>,
        view: View,
        data: T,
        position: Int
    ) -> Unit)? = null

    private val childClicks = mutableSetOf<Int>()

    private var onItemChildLongClickListener: ((
        adapter: QuickBindPagingAdapter<T>,
        view: View,
        data: T,
        position: Int
    ) -> Boolean)? = null

    private val childLongClicks = mutableSetOf<Int>()

    fun onItemChildClickListener(
        vararg viewId: Int,
        onItemChildClickListener: ((adapter: QuickBindPagingAdapter<T>, view: View, data: T, position: Int) -> Unit)?
    ) {
        if (onItemChildClickListener != null) {
            childClicks.addAll(viewId.asList())
        } else {
            childClicks.clear()
        }
        this.onItemChildClickListener = onItemChildClickListener
    }

    fun onItemChildLongClickListener(
        vararg viewId: Int,
        onItemChildLongClickListener: ((adapter: QuickBindPagingAdapter<T>, view: View, data: T, position: Int) -> Boolean)?
    ) {
        if (onItemChildLongClickListener != null) {
            childLongClicks.addAll(viewId.asList())
        } else {
            childLongClicks.clear()
        }
        this.onItemChildLongClickListener = onItemChildLongClickListener
    }

}