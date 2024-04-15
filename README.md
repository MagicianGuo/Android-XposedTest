# 一、简介
这是一个用于演示如何开发Xposed模块的项目。通过它可以改变应用的运行效果。
当然，需要安装LSPatch和Shizuku，之后无需root即可完成应用的修补。



# 二、使用方法

XposedTestApp模块是需要测试的应用，提供了简单的界面，但是窗口设置了禁止截屏。

安装XposedAllowScreenshot模块，并通过LSPatch将测试应用与该模块集成，可以将截屏限制解除。

安装XposedChangeView模块并集成后可以在Activity布局中插入一块View，能够实现简单的跳页和控制其它View功能。