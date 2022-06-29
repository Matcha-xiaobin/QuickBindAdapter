# QuickBindAdapter

[![](https://jitpack.io/v/Matchas-xiaobin/QuickBindAdapter.svg)](https://jitpack.io/#Matchas-xiaobin/QuickBindAdapter)

使用Databinding简单快速实现RecyclerView多布局Adapter。只需要用这一个Adapter就够了。
                                            
## 在项目中引用

### Gradle
   Step 1. Add it in your root build.gradle at the end of repositories
   
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
    
   Step 2. Add the dependency
   
    dependencies {
        implementation 'com.github.Matchas-xiaobin:QuickBindAdapter:3.0.0'
    }

### 2.1.0开始API大量改动，请注意斟酌是否升级版本：

    变化：
        > quickCovert -> quickBind
        > addClickListener -> addClicks
        > addLongClickListener -> addLongClicks
        > OnItemClickListener.onItemClick(adapter, view, position) -> OnItemClickListener.onItemClick(adapter, view, data, position)
        > OnItemLongClickListener.onItemLongClick(adapter, view, position) -> OnItemLongClickListener.onItemLongClick(adapter, view, data, position)
        > OnItemChildClickListener.onItemLongClick(adapter, view, position) 已移除，用 OnItemClickListener 替代
        > OnItemChildLongClickListener.onItemLongClick(adapter, view, position) 已移除，用 OnItemLongClickListener 替代
        > QuickBindAdapter.Create() 已移除
        > setEmpty(view) 方法已修改，现在必须自己实现 BasePlaceholder() 或者使用 DefaultPlaceholder(context)
        > setLoadView(BaseLoadView) -> setLoadMoreItemView(BaseLoadView)
        > setCanLoadMoreWhenNoFullContent(boolean) -> setLoadMoreWhenItemsNoFullScreen(boolean)
        > setSpanSizeLookup(SpanSizeLookup) 已移除，如有需要，在调用setAdapter之前给layoutManager设置spanSizeLookup即可，如有需要，可以在重新设置spanSizeLookup后调用adapter.refreshSpanSizeLookup()刷新
        > setSpanSizeLookup(SpanSizeLookup) 已移除，如有需要，在调用setAdapter之前给layoutManager设置spanSizeLookup即可，如有需要，可以在重新设置spanSizeLookup后调用adapter.refreshSpanSizeLookup()刷新
    更多请在demo中查看

    注意：以下文档按照最新的版本编写
    注意：以下文档按照最新的版本编写
    注意：以下文档按照最新的版本编写


### 在代码中使用：

    QuickBindAdapter adapter = new QuickBindAdapter(context);
    adapter.bind()
    adapter.setxxx()
    ...
    
    或者链式初始化
    QuickBindAdapter binAdapter = QuickBindAdapter.Create().bind().setxxx().setxxx();

### 绑定数据类型和布局  有几种布局，就绑定几次，每一种布局要对应一种数据类型

    三个参数分别为: 数据类型 , 对应的布局 , 这个数据要设置到的属性
    adapter.bind(DataBean.class, R.layout.item_child, BR.data);
    adapter.bind(String.class, R.layout.item_group, BR.data);
    
    绑定布局的时候，可以不绑定数据要设置到哪个属性
    一般用来穿插一些固定内容不变化的布局，或者想用代码动态配置布局的，建议配合QuickBind(原QuickCovert)一起使用:
    adapter.bind(class, layoutId);
    
### 新增空数据时展示全屏占位图;

    adapter.setEmptyView(DefaultPlaceholder(context));
    也可以自己写一个类，继承BasePlaceholder即可，样式自己写
    
### QuickBind接口，用于只使用Databinding绑定控件，手动写复杂逻辑;
          
    adapter.setQuickBind((binding, itemData, position) -> {
              // binding 是这个item本身，itemData 是这个item的数据，position 是这个item所在列表中的位置
              //如果是多布局，则需要做下判断：
              if (binding instanceof ItemStartBinding) {
                  // R.layout.item_start 类型 的item
              }
              //也可以这样：
              if (itemData instanceof String) {
                  // String 类型 的item
              }
          });

### 添加子控件点击事件  对应每一种数据类型的item，添加其子控件的点击事件

    adapter.addClicks(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);

    adapter.addClicks(String.class, R.id.tv_name);


### 点击事件：

    adapter.setOnItemClickListener(OnItemClickListener)

    adapter.setOnItemChildClickListener(OnItemClickListener)


### 长按事件：

    adapter.setOnItemLongClickListener(OnItemLongClickListener)

    adapter.setOnItemChildLongClickListener(OnItemLongClickListener)

### 获取item数据的api;
      
      所有数据
      ItemData dataList = adapter.getDatas();
      
      单个数据
      Object itemData = adapter.getItemData(position);

### 设置新数据：

    dataList 如果是需要多布局，建议使用ItemData 添加数据。
    
    例如：
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
                      
    如果仅一种类型，可以直接使用：
                      
    adapter.setNewData(dataList);
                                                     
### 添加数据
                                  
    添加单个
    adapter.addData(object);
    添加单个，并且让这个新加的处于可见范围
    adapter.addData(object, true);
                                                     
    添加多个到列表后面
    adapter.addDatas(arrayList);
    添加多个到列表后面，并且让新增的第一个处于可见范围
    adapter.addDatas(arrayList, true);
                                       
### 插入数据到某个位置API;
                                       
    插入单个数据到指定位置
    adapter.insertData(index, itemData);
    插入单个数据到指定位置，并且让新插入的处于可见范围
    adapter.insertData(index, itemData, true);
    
    从指定位置插入多个数据
    adapter.insertDatas(index, arraysData);
    从指定位置插入多个数据，并且让新增的第一个处于可见范围
    adapter.insertDatas(index, arraysData, true);
                                                     
### 移除单个
                                                     
    adapter.remove(position);
    
### 替换单个
    
    //替换指定item
    adapter.replace(position, object);
    //替换指定item，并使其可见
    adapter.replace(position, object, true);
    
### 移动位置
    
    //移动某个item到另一个位置
    movedPositions(fromPosition, toPosition);
    //移动某个item到另一个位置，并且让这个item的新位置处于可见
    movedPositions(fromPosition, toPosition, true);
    
### 加载更多功能;

    使用方式：
    adapter.setOnLoadMoreListener(@NonNull OnLoadMoreListener onLoadMoreListener);
    加载成功:
    adapter.loadMoreSuccess();
    加载失败:
    adapter.loadMoreFailed();
    加载完成，没有更多数据了:
    adapter.loadMoreSuccessAndNoMore();
      
    - 修改 正在加载 文字
    - 修改 加载成功 文字
    - 修改 加载失败 文字
    - 修改 没有更多数据了 文字
    使用方式:
    DefaultLoadView loadView = new DefaultLoadView(context)
    loadView.setOnLoadingText("加载中...")
    ...
    adapter.setLoadView(loadView);
      
    DefaultLoadView是内置的默认的。
    自定义的布局需要继承BaseLoadView去实现。
