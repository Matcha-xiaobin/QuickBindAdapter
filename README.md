# QuickBindAdapter

[![](https://jitpack.io/v/Matchas-xiaobin/QuickBindAdapter.svg)](https://jitpack.io/#Matchas-xiaobin/QuickBindAdapter)

使用Databinding简单快速实现RecyclerView多布局Adapter。只需要用这一个Adapter就够了。

## 更新记录

### v1.1.0.f 增加简单的加载更多功能;

      使用方式：
      setOnLoadMoreListener(@NonNull OnLoadMoreListener onLoadMoreListener,
                                            @NonNull RecyclerView recyclerView);
                                            
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
        implementation 'com.github.Matchas-xiaobin:QuickBindAdapter:1.1.0.f'
    }

### 在代码中使用：

    QuickBindAdapter adapter = new QuickBindAdapter();
    adapter.bind()
    adapter.setxxx()
    ...
    
    或者链式初始化
    QuickBindAdapter binAdapter = QuickBindAdapter.Create().bind().setxxx().setxxx();

### 绑定数据类型和布局  有几种布局，就bind几次，每一种布局要对应一种数据类型

    三个参数分别为: 数据类型 , 对应的布局 , 这个数据要设置到的属性
    adapter.bind(DataBean.class, R.layout.item_child, BR.data);
    adapter.bind(String.class, R.layout.item_group, BR.data);
    
    绑定布局的时候，可以不绑定数据要设置到哪个属性
    一般用来穿插一些固定内容不变化的布局，或者想用代码动态配置布局的，建议配合QuickCovert一起使用:
    adapter.bind(class, layoutId);
    
### 新增空数据时展示全屏占位图;
    
    请注意: 
    不要使用: View.inflate(context, layoutId, recyclerView);
    不要使用: View.inflate(context, layoutId, recyclerView);
    不要使用: View.inflate(context, layoutId, recyclerView);
    
    View.inflate正确使用方式: 
    View.inflate(context, layoutId, null); 注意第三个root参数。
    
    或者用databinding:
    LayoutEmptyBinding layoutEmptyBinding = 
        DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, binding.recyclerView, false);
    adapter.setEmptyView(layoutEmptyBinding.getRoot());
    
    或者用LayoutInflater:
    View view = LayoutInflater.from(context).inflate（context, layoutId, recyclerView, false);
    View view = LayoutInflater.from(context).inflate（context, layoutId, null, false);
    adapter.setEmptyView(view);
    
### QuickCovert接口，用于只使用Databinding绑定控件，手动写复杂逻辑;
          
    adapter.setQuickCovert((binding, itemData, position) -> {
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

    adapter.addClickListener(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);

    adapter.addClickListener(String.class, R.id.tv_name);


### 点击事件：

    adapter.setOnItemClickListener

    adapter.setOnItemChildClickListener


### 长按事件：

    adapter.setOnItemLongClickListener

    adapter.setOnItemChildLongClickListener

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
                                                     
    添加多个
    adapter.addDatas(arrayList);
                                       
### 插入数据到某个位置API;
                                       
    单个插入
    adapter.insertData(index, itemData);
    
    多个插入
    adapter.insertDatas(index, arraysData);
                                                     
### 移除单个
                                                     
    adapter.remove(position);
    
### 替换单个

    adapter.replace(position, object);
    
### 移动位置
    
    movedPositions(fromPosition, toPosition);
    
### 加载更多功能;

      注意：
      数据没满一页的时候, 是不能触发上拉加载更多功能, 后续在考虑要不要加入这个开关
      
      使用方式：
      adapter.setOnLoadMoreListener(@NonNull OnLoadMoreListener onLoadMoreListener,
                                            @NonNull RecyclerView recyclerView);
      刷新成功:
      adapter.loadMoreSuccess();
      刷新失败:
      adapter.loadMoreFail();
      刷新完成，没有更多数据了:
      adapter.loadMoreComplete();
      
      修改 刷新成功 文字:
      adapter.setLoadMoreText("");
      修改 刷新失败 文字:
      adapter.setLoadFailText("");
      修改 刷新完成，没有更多数据了 文字:
      adapter.setLoadCompleteText("");
      
      暂未加入 自定义加载更多 的布局功能, 后续在加
