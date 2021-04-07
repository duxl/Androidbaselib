

# Android框架，集成大多数可能会有用的功能。用于快速搭建Android项目

使用示例见：[https://github.com/duxl/AndroidQuicklib](https://github.com/duxl/AndroidQuicklib)



## 引入方式一，复制代码（用config.gradle可以让每个module引用库的版本一致）

1. **新建项目**

2. **在项目的根目录下创建[config.gradle](https://github.com/duxl/AndroidQuicklib/blob/master/config.gradle)文件**

3. **在项目的根目录下创建baselib目录，将所有代码复制到baselib目录**

4. **在新建项目根目录的`build.gradle`的第一行加入** 

   > apply from: "config.gradle"

   并添加`jitpack`仓库地址

   > maven { url 'https://jitpack.io' }

5. **修改app下的build.gradle，添加如下代码**

   **5.1 添加java编译版本**

```groovy
compileOptions {
    sourceCompatibility = '1.8'
    targetCompatibility = '1.8'
}
```

​		**5.2 将默认的`defaultConfig`替换成下面**

```groovy
defaultConfig {
    applicationId "your package name"
    minSdkVersion rootProject.ext.android["minSdkVersion"]
    targetSdkVersion rootProject.ext.android["targetSdkVersion"]
    versionCode rootProject.ext.android["appVersionCode"]
    versionName rootProject.ext.android["appVersionName"]
}
```

​		**5.3 同时将默认的dependencies替换成如下**

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation project(path: ':baselib')

    // annotationProcessor 在每个module中都必须配置才起作用
    annotationProcessor rootProject.ext.dependencies["butterknife-compiler"]
    annotationProcessor rootProject.ext.dependencies["glide-compiler"]
}
```

6. **修改app的主题样式**

```
<style name="AppTheme" parent="BaseAppTheme">
```

**ps:如果报如下错误，肯定是忘记了上面的第4步**

`ERROR: Cannot get property 'version' on extra properties extension as it does not exist`



7. **将Application继承至BaseApplication**



## 引入方式二，gradle引入（推荐：不用拷贝大量代码，相对简单很多）

1. **添加`jitpack`仓库地址**

   ```groovy
   maven { url 'https://jitpack.io' }
   ```

2. **在app下的build.gradle添加引用**

   ```groovy
   implementation 'com.github.duxl:AndroidQuicklib:v1.0.0_bate'
   
   // annotationProcessor 在每个module中都必须配置才起作用
   annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
   annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
   ```

3. **修改app的主题样式**

   ```xml
   <style name="AppTheme" parent="BaseAppTheme">
   ```

4. **将Application继承至BaseApplication**

   



## BaseActivity API

### 页面基础通用

``` java
// 隐藏状态栏，默认显示（内容填充到状态栏区域）
public void hideStateBar()
```

``` java
// 显示状态栏，默认显示（内容在状态栏区域之下）
public void showStateBar()
```

```java
// 设置状态栏颜色
public void setStateBarColor(int color)
```

```java
// 设置状态栏背景
public void setStateBarResource(int resId)
```

*ActionBar与StateBar有如上相似的api，这里就不再累述*

```java
// 设置ActionBar悬浮（澄侵式ActionBar），也就是ActionBar透下去可已看到后面的内容
public void setActionBarFloat(boolean isFloat)
```

```java
// 设置标题
public void setTitle(int titleId)
public void setTitle(CharSequence title)
```

```java
// 返回按钮点击事件，可重写，默认是关闭Activity
protected void onClickActionBack(View v)
```

```java
// 关闭按钮点击事件，可重写，默认是关闭Activity。通常WebView页面有关闭按钮。
// 默认隐藏，需要显示请调用getActionBarView().getIvClose().setVisibility(View.VISIBLE);
protected void onClickActionClose(View v)
```

```java
// 设置右边文字点击事件
// 默认隐藏，需要显示请调用getActionBarView().getTvRight().setVisibility(View.VISIBLE);
protected void onClickActionTvRight(View v)
```

```java
// 设置右边图标点击事件
// 默认隐藏，需要显示请调用getActionBarView().getTvRight().setVisibility(View.VISIBLE);
protected void onClickActionIvRight(View v)
```

```java
// 重写此方法可单独修改页面的StatusView，全局配置可在app中重写global_status_view_config.xml
protected IStatusView initStatusView()
```

```java
// 设置状态栏字体和图标模式：深色和浅色
public void setStateBarDarkMode()
public void setStateBarLightMode()
```

*可配置全局[ActionBar](https://github.com/duxl/Androidbaselib/blob/master/src/main/res/values/global_action_bar_config.xml)和[StatusView](https://github.com/duxl/Androidbaselib/blob/master/src/main/res/values/global_status_view_config.xml)的样式*

### 页面刷新相关

```java
// 设置页面加载更多和下拉刷新是否可用
public void setEnableLoadMore(boolean enabled)
public void setEnableRefresh(boolean enabled)
```

```java
// 完成刷新/加载更多
public RefreshLayout finishRefresh()
public RefreshLayout finishLoadMore()
```

```java
// 完成加载并标记没有更多数据
public RefreshLayout finishLoadMoreWithNoMoreData()
```

```java
// 恢复没有更多数据的原始状态
public RefreshLayout resetNoMoreData()
```

```java
// 设置刷新、加载更多、点击重试 等监听
public void setOnLoadListener(OnLoadListener listener)
```



### http接口请求使用步骤

1. 重写Application的getGlobalHttpConfig()，配置全局的baseUrl、log处理器、网络监测处理、[code&msg](https://github.com/duxl/Androidbaselib/blob/master/src/main/res/values/global_http_exception_reason.xml)

2. 定义interface接口，用于描述接口地址和参数信息，示例如下

   ```java
   @GET("app/mock/258579/test_list_data")
   Observable<Root<List<String>>> getList(
           @Query("pageNum") int pageNum,
           @Query("pageSize") int pageSize
   );
   ```

3. 使用RetrofitManager发起请求，示例如下

   ```java
   private void loadData() {
           RetrofitManager
                   .getInstance()
                   .create(HttpService.class)
                   .getList(pageNum, 20)
                   .compose(new LifecycleTransformer<Root<List<String>>>(this))
                   // 如果刷新和状态view都是页面，这里可以传Activity.this作为参数，第一个参数adapter是必须的，后面两个是可选的
                   // 普通接口可以用 BaseHttpObserver
                   //.subscribe(new BaseRecyclerViewHttpObserver<Root<List<String>>, String>(mAdapter, this, this) {
                   .subscribe(new BaseRecyclerViewHttpObserver<Root<List<String>>, String>(mAdapter, mSmartRecyclerView, mSmartRecyclerView) {
                       @Override
                       public List<String> getListData(Root<List<String>> root) {
                           return root.data;
                       }
   
                       @Override
                       public boolean isFirstPage() {
                           return pageNum == 1;
                       }
   
                       @Override
                       public boolean hasMoreData(Root<List<String>> root) {
                           return pageNum < 3;
                       }
   
                       @Override
                       public void onError(int code, String msg, Root<List<String>> root) {
                           if(code == HttpExceptionReasons.CONNECT_NO.getCode()) {
                               // 这里可以特殊处理，比如弹框去设置打开wifi等
                           } else {
                               super.onError(code, msg, root);
                           }
                       }
                   });
       }
   ```




## BaseFragment API

BaseActivity 的 Api 在 BaseFragment中都有对应的，这里不再累述



## 工具类

- **AlbumUtils**

  *片选择和视频选择*

- **AppSigning**

  *获取app的签名信息*

- **ArithmeticUtils**

   *算数相关工具类*

- **DesensitizationUtils**

  脱敏工具类：例如手机号中间4个加星号，姓名加星号

- **DisplayUtil**

  *显示相关工具类：获取屏幕高宽；dp、sp与px的转换等*

- **DoubleClickExit**

  *双击退出屏幕*

- **EmptyUtils**

  *EmptyUtils，验证传入参数是否为null或者集合是否未空集合，数组是否为空数组。与NullUtils 的区别是，NullUtils 是用于转换，EmptyUtils用于验证判断。   前者着重转换，后者着重判断*

- **MultiTaskMarkUtil**

  *多任务标记工具类，可用于多个异步操作是否都执行完毕的判断举例：多个异步接口调用前显示dialog加载匡，所有接口都调用完毕后取消dialog这个工具类就可以用来记录这些接口是否都调用完毕，哪些已经调用完毕了*

- **NetCheckUtil**

  *网络监测相关：获取网络状态、是否连接网络等*

- **NullUtils**

  *Null工具类，format方法将传入的参数转换为非null对象。与EmptyUtils 的区别是，EmptyUtils 用于验证判断，NullUtils是用于转换前者着重判断，后者着重转换*

- **RegexUtils**

  *正则表达式工具类，提供了正则表达式验证方法和一些常用的正则规则*

- **SpanUtils**

  *字体加粗、斜体、变色等*

- **SPUtils**

  *SharedPreferences数据存储封装*

- **TimeUtils**

  *时间日期工具类*

- **ToastUtils**

  *Toast吐丝提示工具*

- **Utils**

  *其它工具*

- **ViewClickDelayUtil**

  *延迟控件的点击响应，避免快速连续点击*

- **WindowSoftInputCompat**

  *Android全屏／沉浸式状态栏下，键盘挡住输入框解决办法*