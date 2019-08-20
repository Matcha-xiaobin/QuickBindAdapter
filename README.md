# QuickBindAdapter

[![](https://jitpack.io/v/Matchas-xiaobin/QuickBindAdapter.svg)](https://jitpack.io/#Matchas-xiaobin/QuickBindAdapter)

使用Databinding快速实现RecyclerView多布局Adapter。全项目，只需要用这一个Adapter就够了。

![blockchain](https://github.com/Matchas-xiaobin/QuickBindAdapter/blob/master/screenshot/Screenshot.gif "截图") ![blockchain](https://github.com/Matchas-xiaobin/QuickBindAdapter/blob/master/screenshot/Screen_EmptyView.gif "空数据占位布局")

## 更新记录

### v1.0.7 新增空数据时展示占位图;

      LayoutEmptyBinding layoutEmptyBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                      R.layout.layout_empty, binding.recyclerView, false);
      layoutEmptyBinding.setTitle("暂无数据");
      layoutEmptyBinding.setSubTitle("点击'加'按钮添加数据");
      adapter.setEmptyView(layoutEmptyBinding.getRoot());//必须在rv.setLayoutManager之后调用
      或者：
      View view = View.inflate(this, layoutId, recyclerView);
      adapter.setEmptyView(view);//必须在rv.setLayoutManager之后调用

### v1.0.6 修改设置item点击事件逻辑;

      1.0.6 版本中，如果不是在xml布局中设置item的点击事件，则需要在设置数据前，先绑定item的点击事件，否则点击事件可能不起作用。子控件不受影响。

### v1.0.5 新增插入数据到某个位置API;

      adapter.insertData(itemData, index);
      adapter.insertDatas(arraysData, index);

### v1.0.4 绑定布局的时候，可以不填充数据，如果要填充数据，建议配合QuickCovert一起使用;

      //有几种布局就bind几次
      adapter.bind(String.class, R.layout.item_group);

### v1.0.3 补上获取所有item数据的api;
      
      ItemData dataList = adapter.getDatas();
      
### v1.0.2 新增QuickCovert接口，用于只使用Databinding绑定控件，在adapter中写逻辑;
      
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
        implementation 'com.github.Matchas-xiaobin:QuickBindAdapter:1.0.7'
    }

### 在代码中使用：

    QuickBindAdapter adapter = new QuickBindAdapter();

### 绑定数据类型和布局  有几种布局，就bind几次，每一种布局要对应一种数据类型

    adapter.bind(DataBean.class, R.layout.item_child, BR.data);

    adapter.bind(String.class, R.layout.item_group, BR.data);

### 添加子控件点击事件  对应每一种数据类型的item，添加其子控件的点击事件

    adapter.addClickListener(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);

    adapter.addClickListener(String.class, R.id.tv_name);


### 点击事件：

    adapter.setOnItemClickListener

    adapter.setOnItemChildClickListener


### 长按事件：

    adapter.setOnItemLongClickListener

    adapter.setOnItemChildLongClickListener


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
                                                     
### 添加单个item
                                                     
    adapter.addData(object);
                                                     
### 添加多个item
                                                     
    adapter.addDatas(arrayList);
                                                     
### 移除某个item
                                                     
    adapter.remove(position);
    
### 替换某个item

    adapter.replace(position, object);
  
