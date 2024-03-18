## 开发步骤

1.安装idea社区版 https://www.jetbrains.com/idea/nextversion/
2.使用Java语言开发
3.idea开发插件的相关文档
    官方文档：https://plugins.jetbrains.com/docs/intellij/welcome.html
4.构建一个项目
    文件-新建-项目-IDE插件
5.了解目录
        bin
        build
        build.gradle.kts
        gradle
        gradle.properties
        gradlew
        gradlew.bat
        settings.gradle.kts
        src
6.主要的目录及文件
    build.gradle.kts
    src
    build/distributions
    src/main/resources/META-INF/plugin.xml
7.设置插件的基本信息
    1.ID
    2.插件名称及描述
    3.触发插件的事件
        例：editorFactoryMouseListener - 鼠标事件

## 待完成

1.更换插件的图标及名称,简介
2.优化代码处理报错
3.处理异常
4.显示多个翻译源
5.增加音标
6.增加快捷键翻译
7.增加自定义翻译源
