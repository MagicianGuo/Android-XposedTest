# 一、简介
这是一个用于演示如何开发Xposed模块的项目。通过它可以改变应用的运行效果。
当然，需要安装LSPatch和Shizuku，之后无需root即可完成应用的修补。



# 二、使用方法

编译整个项目，生成的安装包会自动拷贝到项目根目录的“/apks”文件夹里。clean项目会同时将此文件夹删除。

XposedTestApp模块是需要测试的应用，提供了简单的界面，但是窗口设置了禁止截屏。

安装XposedAllowScreenshot模块，并通过LSPatch将测试应用与该模块集成，可以将截屏限制解除。

安装XposedChangeView模块并集成后可以在Activity布局中插入一块View，能够实现简单的跳页和控制其它View功能。

安装XposedLogWebViewUrl模块并集成后可以记录此应用的WebView加载过的网页链接，并保存到“/sdcard/Android/data/[应用包名]”路径下的文件“url_list.txt”中。

# 三、其他模块（需Root，配合LSPosed模块）

XposedRequestSU：勾选的应用会在启动时主动请求Root权限。

XposedShizukuCleaner：勾选Shizuku，能够自动清理激活时产生的残留文件，防止被检测。

XposedJumpAppInterceptor：勾选的应用，在跳转到其他应用时会被拦截，提示弹窗，点击允许才能跳转。