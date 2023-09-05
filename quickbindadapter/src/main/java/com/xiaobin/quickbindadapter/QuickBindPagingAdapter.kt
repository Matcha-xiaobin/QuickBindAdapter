package com.xiaobin.quickbindadapter

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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xiaobin.quickbindadapter.interfaces.StaggeredFullSpan
import com.xiaobin.quickbindadapter.view.BasePageStateView

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
    private val brId: Int = 0
) : PagingDataAdapter<T, BindHolder>(itemCallback) {

    // 布局ID - 数据类型 互相绑定
    private val itemViewTypes = HashMap<Class<*>, Int>()
    private val itemBindVal = HashMap<Class<*>, Int>()

    private val EMPTY_VIEW_TYPE = -100
    private val UNKNOWN_VIEW_TYPE = -101

    private val UNKNOWN_VIEW_TYPE_TEXT = "Undefined data type!"

    private var mRecyclerView: RecyclerView? = null

    /**
     * 空数据占位图
     */
    private var emptyView: BasePageStateView<*, *, *>? = null

    /**
     * 绑定 数据类型 和 视图ID
     */
    fun bind(clazz: Class<*>, @LayoutRes layoutId: Int, brId: Int) {
        itemViewTypes[clazz] = layoutId
        itemBindVal[clazz] = brId
    }

    /**
     * 实际数据大小
     */
    val dataCount: Int = super.getItemCount()

    override fun getItemCount(): Int {
        if (emptyView != null && dataCount == 0) {
            return 1
        }
        return dataCount
    }

    override fun getItemViewType(position: Int): Int {
        if (dataCount == 0) {
            return EMPTY_VIEW_TYPE
        }
        val data = getItem(position) ?: return UNKNOWN_VIEW_TYPE
        return itemViewTypes[data::class.java] ?: UNKNOWN_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        return when (viewType) {
            EMPTY_VIEW_TYPE -> {
                emptyView!!.createViewHolder(parent, lifecycleOwner)
            }

            else -> {
                var mClass: Class<*>? = null
                if (viewType != UNKNOWN_VIEW_TYPE) {
                    for (itemViewType in itemViewTypes) {
                        if (itemViewType.value == viewType) {
                            mClass = itemViewType.key
                            break
                        }
                    }
                }
                if (mClass != null) {
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
        }
    }

    override fun onBindViewHolder(
        holder: BindHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (null != quickBindPayloads) {
            val data = getItem(position) ?: return
            quickBindPayloads?.invoke(this, holder, data, position, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        val data = getItem(position)
        if (brId != 0) {
            holder.binding?.setVariable(brId, data)
        }
        quickBind?.invoke(this, holder, data ?: return, position)
        if (onItemClickListener != null) {
            holder.binding?.root?.setOnClickListener {
                onItemClickListener?.invoke(this, data ?: return@setOnClickListener, position)
            }
        } else {
            holder.binding?.root?.setOnClickListener(null)
        }
        if (onItemLongClickListener != null) {
            holder.binding?.root?.setOnLongClickListener {
                return@setOnLongClickListener onItemLongClickListener?.invoke(
                    this,
                    data ?: return@setOnLongClickListener false,
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
                        data ?: return@setOnClickListener,
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
                            data ?: return@setOnLongClickListener false,
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
            if (holder.itemViewType == EMPTY_VIEW_TYPE || holder.fullSpan) {
                lp.isFullSpan = true
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecyclerView = recyclerView

        //不缓存缺省页布局
        val pool = recyclerView.recycledViewPool
        pool.setMaxRecycledViews(EMPTY_VIEW_TYPE, 0)

        //更新lookup
        refreshSpanSizeLookup()
    }

    private var customLookUp: GridLayoutManager.SpanSizeLookup? = null

    private val spanSizeLookUp = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (getItemViewType(position) == EMPTY_VIEW_TYPE) {
                //缺省页
                (mRecyclerView?.layoutManager as GridLayoutManager).spanCount
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

    var quickBind: ((adapter: QuickBindPagingAdapter<T>, viewHolder: BindHolder, data: T, position: Int) -> Unit)? =
        null
        private set

    var quickBindPayloads: ((
        adapter: QuickBindPagingAdapter<T>,
        viewHolder: BindHolder,
        data: T,
        position: Int,
        payloads: MutableList<Any>
    ) -> Unit)? = null
        private set

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