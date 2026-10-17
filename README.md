# NekoUi

一个 Android 界面外壳：四个页签、两套引擎（Miuix / Material 3）、液态玻璃底栏、预见式返回。

没有业务逻辑 —— 首页是演示数据，终端不会执行命令，网页没有内置视图。

包名 `top.youzix.neko` · minSdk 33 · targetSdk 35

## 构建

只在 GitHub Actions 里构建，推送 `main` 产出 APK artifact，推送 `v*` 标签额外创建 Release。

## 许可

界面来自 [NekoPlus](https://github.com/Youzix-Star/NekoPlus)（AGPL-3.0）。本项目同样以 [AGPL-3.0](LICENSE) 开源。
