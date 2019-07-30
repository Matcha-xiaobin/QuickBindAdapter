# QuickBindAdapter
使用Databinding快速实现RecyclerView多布局Adapter。全项目，只需要用这一个Adapter就够了。

使用方法：
QuickBindAdapter adapter = new QuickBindAdapter();
//绑定数据类型和布局  有几种布局，就bind几次
adapter.bind(DataBean.class, R.layout.item_child, BR.data);
adapter.bind(String.class, R.layout.item_group, BR.data);
//添加子控件点击事件  对应每一种数据类型的item，添加其子控件的点击事件
adapter.addClickListener(ChatListBean.class, R.id.iv_image, R.id.tv_name, R.id.tv_message);
adapter.addClickListener(String.class, R.id.tv_name);

点击事件：
adapter.setOnItemClickListener
adapter.setOnItemChildClickListener
长按事件：
adapter.setOnItemLongClickListener
adapter.setOnItemChildLongClickListener

数据：
1.使用ItemData.class
  ItemData dataList = new ItemData();
  dataList.add(object);
  adapter.setNewData(dataList);
2.使用List<?>
  List<ChatListBean> dataList2 = new ArrayList<>();
  for (int i = 0; i < 15; i++) {
      dataList2.add(new ChatListBean());
  }
  adapter.setNewData(dataList2);
3.添加单个item
  adapter.addData(object);
4.添加多个item
  adapter.addDatas(arrayList);
5.移除某个item
  adapter.remove(position);
6.替换某个item
  adapter.replace(position, object);
  
