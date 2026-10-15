package com.termux.termcore.workstation

import com.google.gson.Gson
import com.termux.termcore.bean.ZTUserBean
import com.termux.termcore.ftp.utils.UserSetManage

object ZtWorkstationSettingsHelper {

    private val gson = Gson()

    data class SettingItem(
        val key: String,
        val title: String,
        val type: String,
        val value: Any?,
        val group: String
    )

    data class SettingPage(val id: String, val title: String, val description: String)

    private data class BoolSetting(
        val key: String,
        val title: String,
        val getter: (ZTUserBean) -> Boolean,
        val setter: (ZTUserBean, Boolean) -> Unit,
        val group: String
    )

    private val booleanSettings = listOf(
        BoolSetting("isOpenDownloadFileServices", "ZT下载服务器", { it.isOpenDownloadFileServices }, { b, v -> b.isOpenDownloadFileServices = v }, "Termux"),
        BoolSetting("inputMethodTriggerClose", "输入法调起侧边栏关闭", { it.isInputMethodTriggerClose }, { b, v -> b.isInputMethodTriggerClose = v }, "Termux"),
        BoolSetting("styleTriggerOff", "美化设置关闭菜单", { it.isStyleTriggerOff }, { b, v -> b.isStyleTriggerOff = v }, "Termux"),
        BoolSetting("isToolShow", "禁止工具箱显示", { it.isToolShow }, { b, v -> b.isToolShow = v }, "Termux"),
        BoolSetting("forceUseNumpad", "强制使用小键盘规则", { it.isForceUseNumpad }, { b, v -> b.isForceUseNumpad = v }, "Termux"),
        BoolSetting("isOutputLOG", "输出显示LOG", { it.isOutputLOG }, { b, v -> b.isOutputLOG = v }, "Termux"),
        BoolSetting("isResetVolume", "还原音量加减", { it.isResetVolume }, { b, v -> b.isResetVolume = v }, "Termux"),
        BoolSetting("isCloseFoldMenu", "折叠菜单", { it.isCloseFoldMenu }, { b, v -> b.isCloseFoldMenu = v }, "Termux"),
        BoolSetting("isDisableMainConfigMenu", "禁用主菜单配置文件", { it.isDisableMainConfigMenu }, { b, v -> b.isDisableMainConfigMenu = v }, "Termux"),
        BoolSetting("isEditorWordWrap", "文本编辑器自动换行", { it.isEditorWordWrap }, { b, v -> b.isEditorWordWrap = v }, "Termux"),
        BoolSetting("isSnowflakeShow", "显示雪花", { it.isSnowflakeShow }, { b, v -> b.isSnowflakeShow = v }, "Termux"),
        BoolSetting("isShowCommand", "显示/隐藏终端", { it.isShowCommand }, { b, v -> b.isShowCommand = v }, "Termux"),
        BoolSetting("isInternalPassage", "内部/外部通道", { it.isInternalPassage }, { b, v -> b.isInternalPassage = v }, "Termux"),
        BoolSetting("mIsDeepSeekVisibleTerminal", "DeepSeek可见终端", { it.isIsDeepSeekVisibleTerminal }, { b, v -> b.setIsDeepSeekVisibleTerminal(v) }, "AI"),
        BoolSetting("mIsCustomVisibleTerminal", "自定义AI可见终端", { it.isIsCustomVisibleTerminal }, { b, v -> b.setIsCustomVisibleTerminal(v) }, "AI"),
        BoolSetting("mIsCustomAi", "选择自定义AI", { it.isCustomAi }, { b, v -> b.isCustomAi = v }, "AI"),
        BoolSetting("mIsBackMenuVisible", "显示左/右侧背景图片", { it.isBackMenuVisible }, { b, v -> b.setIsBackMenuVisible(v) }, "Termux"),
        BoolSetting("isCreateFolderForSdcardAndroid", "Android/data创建文件", { it.isCreateFolderForSdcardAndroid }, { b, v -> b.isCreateFolderForSdcardAndroid = v }, "Termux"),
        BoolSetting("isJumpGuide", "跳过引导页面", { it.isJumpGuide }, { b, v -> b.isJumpGuide = v }, "Termux"),
        BoolSetting("isHideGuideLayout", "隐藏引导页面", { it.isHideGuideLayout }, { b, v -> b.isHideGuideLayout = v }, "Termux"),
        BoolSetting("isWriterMenuBack", "写入菜单背景", { it.isWriterMenuBack }, { b, v -> b.isWriterMenuBack = v }, "Termux")
    )

    private val settingPages = listOf(
        SettingPage("termux", "Termux 设置", "Termux 核心偏好设置（需在 App 内打开）"),
        SettingPage("zt", "Termux 设置", "本页可远程切换 Termux 开关"),
        SettingPage("left_menu", "左侧菜单设置", "菜单布局与样式（需在 App 内打开）"),
        SettingPage("ai", "AI 设置", "DeepSeek 与自定义 AI（需在 App 内打开）"),
        SettingPage("online_server", "在线服务器", "在线服务器配置（需在 App 内打开）"),
        SettingPage("install", "安装管理", "插件与安装项（需在 App 内打开）"),
        SettingPage("save_path", "存储路径", "xinhao 存储路径（需在 App 内打开）")
    )

    fun listPages(): String = gson.toJson(mapOf("pages" to settingPages))

    fun listSettings(): String {
        val bean = UserSetManage.get().getZTUserBean()
        val items = booleanSettings.map {
            SettingItem(it.key, it.title, "boolean", it.getter(bean), it.group)
        }
        return gson.toJson(mapOf("settings" to items))
    }

    fun updateSetting(key: String, value: String): String {
        val bean = UserSetManage.get().getZTUserBean()
        val setting = booleanSettings.find { it.key == key }
            ?: return gson.toJson(mapOf("ok" to false, "error" to "unknown key"))
        val boolValue = value.equals("true", ignoreCase = true) || value == "1"
        setting.setter(bean, boolValue)
        UserSetManage.get().setZTUserBean(bean)
        return gson.toJson(mapOf("ok" to true))
    }
}
