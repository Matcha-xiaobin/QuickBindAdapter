# QuickBindAdapter

[![](https://jitpack.io/v/Matchas-xiaobin/QuickBindAdapter.svg)](https://jitpack.io/#Matchas-xiaobin/QuickBindAdapter)

使用Databinding快速实现RecyclerView多布局Adapter。全项目，只需要用这一个Adapter就够了。

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
        implementation 'com.github.Matchas-xiaobin:QuickBindAdapter:release'
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
  
