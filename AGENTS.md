# AGENTS.md

`NekoUi`：`DSHA-Next` 挖空到只剩界面之后的样子。包名 `top.youzix.neko`。

## 不变量

1. **两套引擎各写一遍**：`ui/miuix/**` 与 `ui/material3/**` 互不共享 Composable，改一个页签必须两个都改。
2. **不写业务逻辑**：没有网络、没有权限、没有跨进程调用、没有 shell。页面要的数据来自 `demo/`。
3. **演示数据只有一个来源**：`app/src/main/java/top/youzix/neko/demo/`（首页、终端、网页各一份）。

## 工作方式

- **不在本地构建。** 这台设备没有 Android SDK，也别装；唯一验证是 GitHub Actions（3～6 分钟一轮）。
- 改完 `.kt` 先跑一遍 `python3 ../DSHA-Next/scripts/klex.py <文件>`：词法、括号平衡、未使用 import。
  它看不见类型，缺符号只能靠 CI。
- 不要凭记忆写 Compose / miuix 的签名。miuix 源码：
  `https://repo1.maven.org/maven2/top/yukonga/miuix/kmp/miuix-ui-android/<版本>/miuix-ui-android-<版本>-sources.jar`。
- 本机 `write` 工具建不了硬链接（Android 禁 `link(2)`），整份重写用 `cat > file <<'EOF'`。
- 签名：CI 从 Secrets 读 `KEYSTORE_BASE64` / `KEYSTORE_PASSWORD` / `KEY_ALIAS` / `KEY_PASSWORD`；
  没有时退回 debug key，APK 仍可安装。
