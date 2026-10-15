package com.termux.termcore.ai.config

import com.termux.termcore.ai.agent.ZtAgentAiChatClient
import com.termux.termcore.command.ZtCommandDefHelper
import com.termux.termcore.config.ztcommand.navigation.ZtNavigationHelper
import org.json.JSONArray
import org.json.JSONObject

object ZtAiConfigExecutor {

    fun execute(toolCall: ZtAgentAiChatClient.ToolCall): String {
        val args = JSONObject(toolCall.arguments.ifBlank { "{}" })
        return try {
            when (toolCall.name) {
                "list_termux_capabilities" -> {
                    ZtAiConfigRegistry.listCapabilities(args.optString("category", "all"))
                }
                "get_termux_config" -> {
                    val keysArg = args.optJSONArray("keys")
                    ZtAiConfigRegistry.getConfig(
                        group = args.optString("group").takeIf { it.isNotBlank() },
                        keys = keysArg
                    )
                }
                "set_termux_config" -> {
                    val key = args.optString("key", "").trim()
                    if (key.isEmpty()) return "Error: key is required"
                    ZtAiConfigRegistry.setConfig(key, args.optString("value", ""))
                }
                "open_termux_page" -> {
                    openPage(args)
                }
                "run_termux_zt" -> {
                    val command = args.optString("command", "").trim()
                    if (command.isEmpty()) return "Error: command is required"
                    ZtAiZtSocketClient.send(command)
                }
                "get_termux_left_menu" -> ZtAiLeftMenuHelper.getMenuInfo()
                "update_termux_left_menu" -> ZtAiLeftMenuHelper.updateMenu(args)
                "list_termux_pkg_sources" -> ZtAiPkgSourceHelper.listSources()
                "switch_termux_pkg_source" -> ZtAiPkgSourceHelper.switchSource(args)
                "reset_termux_beautify" -> ZtBeautifyClearHelper.clearAndApplyUi()
                "list_termux_containers" -> ZtAiContainerHelper.listContainersJson()
                "create_termux_container" -> ZtAiContainerHelper.createContainer(args)
                "switch_termux_container" -> ZtAiContainerHelper.switchContainer(args)
                "delete_termux_container" -> ZtAiContainerHelper.deleteContainer(args)
                "list_termux_command_defs" -> ZtCommandDefHelper.listJson()
                "add_termux_command_def" -> ZtCommandDefHelper.addCommand(args)
                "update_termux_command_def" -> ZtCommandDefHelper.updateCommand(args)
                "delete_termux_command_def" -> ZtCommandDefHelper.deleteCommand(args)
                "run_termux_command_def" -> ZtCommandDefHelper.runCommand(args)
                else -> "Error: unknown config tool `${toolCall.name}`"
            }
        } catch (e: Exception) {
            "Error: ${e.message ?: "config tool failed"}"
        }
    }

    fun statusLabel(toolName: String): String {
        return when (toolName) {
            "list_termux_capabilities" -> ZtAiStrings.statusListCapabilities()
            "get_termux_config" -> ZtAiStrings.statusGetConfig()
            "set_termux_config" -> ZtAiStrings.statusSetConfig()
            "open_termux_page" -> ZtAiStrings.statusOpenPage()
            "run_termux_zt" -> ZtAiStrings.statusRunZt()
            "get_termux_left_menu" -> ZtAiStrings.statusGetLeftMenu()
            "update_termux_left_menu" -> ZtAiStrings.statusUpdateLeftMenu()
            "list_termux_pkg_sources" -> ZtAiStrings.statusListPkgSources()
            "switch_termux_pkg_source" -> ZtAiStrings.statusSwitchPkgSource()
            "reset_termux_beautify" -> ZtAiStrings.statusResetBeautify()
            "list_termux_containers" -> ZtAiStrings.statusListContainers()
            "create_termux_container" -> ZtAiStrings.statusCreateContainer()
            "switch_termux_container" -> ZtAiStrings.statusSwitchContainer()
            "delete_termux_container" -> ZtAiStrings.statusDeleteContainer()
            "list_termux_command_defs" -> ZtAiStrings.statusListCommandDefs()
            "add_termux_command_def" -> ZtAiStrings.statusAddCommandDef()
            "update_termux_command_def" -> ZtAiStrings.statusUpdateCommandDef()
            "delete_termux_command_def" -> ZtAiStrings.statusDeleteCommandDef()
            "run_termux_command_def" -> ZtAiStrings.statusRunCommandDef()
            else -> ZtAiStrings.statusConfigDefault()
        }
    }

    private fun openPage(args: JSONObject): String {
        val pageId = args.optString("page_id", "").trim()
        if (pageId.isEmpty()) {
            return ZtNavigationHelper.listPagesJson()
        }
        val extras = args.optJSONObject("extras")
        val context = com.termux.termcore.config.ztcommand.navigation.ZtForegroundActivityHolder.get()
            ?: return """{"code":1,"msg":"Termux main screen is not active. Return to terminal first."}"""
        return ZtNavigationHelper.openPage(context, pageId, extras)
    }
}
