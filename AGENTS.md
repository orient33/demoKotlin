<!--
本文档由 AI Agent (glm-4.7-free / opencode/glm-4.7-free) 自动生成和维护
Model: opencode/glm-4.7-free
-->

# AGENTS.md - DemoKotlin 项目指南

本项目是一个 Kotlin/Android 演示应用，包含多种 Android 开发技术的示例代码。

## 项目结构
- `app/` - 主应用模块，包含所有 Android 功能演示
- `lib/` - 库模块，包含工具类和算法实现
- 混合使用 Kotlin 和 Java 代码

## 构建命令

### 基本构建
```bash
# 清理项目
./gradlew clean

# 构建所有变体
./gradlew assemble

# 构建调试版本
./gradlew assembleDebug

# 构建发布版本
./gradlew assembleRelease
```

### 测试命令
```bash
# 运行所有单元测试
./gradlew test

# 运行调试版本单元测试
./gradlew testDebugUnitTest

# 运行发布版本单元测试
./gradlew testReleaseUnitTest

# 运行设备测试（需要连接设备）
./gradlew connectedAndroidTest

# 运行特定测试类
./gradlew testDebugUnitTest --tests "com.example.ExampleTest"
```

### 代码检查
```bash
# 运行 lint 检查
./gradlew lint

# 运行 lint 并自动修复
./gradlew lintFix

# 运行所有检查
./gradlew check
```

### 安装命令
```bash
# 安装调试版本到设备
./gradlew installDebug

# 卸载调试版本
./gradlew uninstallDebug
```

## 代码风格指南

### Kotlin 代码风格
- 项目使用官方 Kotlin 代码风格 (`kotlin.code.style=official`)
- 使用 2 空格缩进
- 类名使用 PascalCase：`MainActivity`, `HomeFragment`
- 函数和变量使用 camelCase：`onCreate`, `mList`
- 常量使用 UPPER_SNAKE_CASE：`TAG`, `PREF_FILE_NAME`

### Java 代码风格
- 遵循 Google Java Style Guide
- 使用 4 空格缩进
- 包名全小写：`com.example.kotlindemo`
- 类名使用 PascalCase
- 方法和变量使用 camelCase

### 导入顺序
1. Android 框架导入
2. AndroidX 导入
3. 第三方库导入
4. 项目内部导入
5. Java/Kotlin 标准库导入

### 命名约定
- Fragment 类以 `Fragment` 结尾：`HomeFragment`, `KorFragment`
- Activity 类以 `Activity` 结尾：`MainActivity`
- ViewModel 类以 `VM` 结尾：`KorVM`, `MediaVM`
- DAO 接口以 `Dao` 结尾：`UserDao`
- 数据库类以 `Db` 结尾：`AppDb`

### ViewBinding 配置
项目启用了 ViewBinding：
```kotlin
viewBinding {
    enable = true
}
```
使用方式：
```kotlin
private lateinit var binding: FragmentHomeBinding
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding = FragmentHomeBinding.bind(view)
    binding.recyclerView.adapter = adapter
}
```

### 错误处理
- 使用 try-catch 处理异常，避免应用崩溃
- 在 `MainActivity.toFragment()` 中使用异常处理
- 网络请求和文件操作必须包含异常处理

### 日志规范
- 使用统一的日志工具：`Log.i("dff", "message")`
- 在 BaseActivity 中提供 `log()` 方法
- 调试日志使用 `Log.i()`，错误使用 `Log.e()`

### Fragment 最佳实践
- 继承 `androidx.fragment.app.Fragment`
- 使用构造参数传递布局：`class HomeFragment : Fragment(R.layout.fragment_home)`
- 在 `onViewCreated` 中初始化视图和设置监听器
- 使用 `viewLifecycleOwner` 观察 LiveData

### 依赖注入
- 使用 ViewModelProvider 获取 ViewModel 实例
- 在 Fragment 的 `onAttach` 或 `onViewCreated` 中初始化 ViewModel

### 网络请求
- 使用 Retrofit + OkHttp
- 接口定义在 `NetAPI.java` 中
- 使用协程处理异步操作

### 数据库
- 使用 Room 数据库
- Entity、DAO、Database 分离定义
- 使用 kapt 处理注解

### 资源管理
- 字符串资源使用 `strings.xml`
- 颜色资源使用 `colors.xml`
- 尺寸资源使用 `dimens.xml`
- 布局文件以 `fragment_`、`activity_`、`item_` 前缀命名

## 注意事项
- 项目使用阿里云 Maven 镜像加速依赖下载
- 最低支持 API 26，目标 API 33
- 使用 Java 17 兼容性
- 启用了 R8 代码混淆（发布版本）
- 支持多模块项目结构