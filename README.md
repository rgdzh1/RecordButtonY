## RecordButtonY
[![](https://jitpack.io/v/rgdzh1/RecordButtonY.svg)](https://jitpack.io/#rgdzh1/RecordButtonY)
> 视频录制按钮, 可以设置最短录制时间和最长录制时间.
> 如果对实现源码感兴趣可以看看我写的[博客](https://blog.csdn.net/MoLiao2046/article/details/104688172)
### 使用   
- 配置依赖, 在所在Module的build.gradle文件下
  ```groovy
  implementation 'com.github.rgdzh1:RecordButtonY:Tag'
  ```
  
- 属性释意
    ```xml
      rby_circle_out_margin: 圆形与内部正方形的间距
      rby_circle_width: 圆形画笔宽度
      rby_circle_paint_color: 圆形画笔颜色
      rby_rect_paint_color: 内部正方形画笔的颜色
      rby_rect_rate_start: 内部正方形初始边长相对于外圆内切正方形边长比率 0-1 
      rby_rect_rate_fnish: 内部正方形结束边长相对于外圆内切正方形边长比率 0-1
      rby_short_time: 录制规定最短时间
      rby_long_time: 录制规定最长时间
    ```
    
- 布局文件中使用
    ```xml
     <com.yey.rby.RButtonY
         android:id="@+id/rby"
         android:layout_width="50dp"
         android:layout_height="50dp"
         android:layout_centerInParent="true"
         app:rby_circle_out_margin="5dp"
         app:rby_circle_paint_color="@color/colorPrimary"
         app:rby_circle_width="1dp"
    ```    
- 代码
    ```java
    ((RButtonY) findViewById(R.id.rby)).setiRBYClick(new RBYCallback() {
        @Override
        public void startCb(String current) {
            Toast.makeText(MainActivity.this, "录制开始" + current, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void finishCb(String current) {
            Toast.makeText(MainActivity.this, "录制结束" + current, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void eventCb(String current) {
            Log.e(TAG, "当前录制时间" + current);
        }
     });
    ```
- 效果图

    ![效果图](录制按钮.gif)