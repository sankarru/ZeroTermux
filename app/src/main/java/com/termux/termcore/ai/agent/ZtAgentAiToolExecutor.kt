package com.termux.termcore.ai.agent

import com.termux.R
import com.termux.termcore.ai.config.ZtAiConfigExecutor
import com.termux.termcore.utils.ZtLocaleStrings
import org.json.JSONObject

object ZtAgentAiToolExecutor {

    private val configToolNames = setOf(
        "list_termux_capabilities",
        "get_termux_config",
        "set_termux_config",
        "open_termux_page",
        "run_termux_zt",
        "get_termux_left_menu",
        "update_termux_left_menu",
        "list_termux_pkg_sources",
        "switch_termux_pkg_source",
        "reset_termux_beautify",
        "list_termux_containers",
        "create_termux_container",
        "switch_termux_container",
        "delete_termux_container",
        "list_termux_command_defs",
        "add_termux_command_def",
        "update_termux_command_def",
        "delete_termux_command_def",
        "run_termux_command_def"
    )

    fun execute(
        toolCall: ZtAgentAiChatClient.ToolCall,
        terminalEnabled: Boolean,
        ztControlEnabled: Boolean,
        filesystemEnabled: Boolean = false
    ): String {
        if (toolCall.name in configToolNames) {
            if (!ztControlEnabled) {
                return disabledMessage(com.termux.R.string.zt_agent_ai_zt_control_tool_disabled)
            }
            return ZtAiConfigExecutor.execute(toolCall)
        }
        if (toolCall.name in ZtAgentAiFilesystemExecutor.toolNames) {
            if (!filesystemEnabled) {
                return disabledMessage(
                    if (terminalEnabled) {
                        com.termux.R.string.zt_agent_ai_filesystem_tool_disabled
                    } else {
                        com.termux.R.string.zt_agent_ai_filesystem_tool_disabled_no_terminal
                    }
                )
            }
            return ZtAgentAiFilesystemExecutor.execute(toolCall)
        }
        if (toolCall.name in ZtAgentAiSkillTools.toolNames) {
            return ZtAgentAiSkillExecutor.execute(toolCall)
        }
        return when (toolCall.name) {
            "read_terminal", "send_terminal_command", "send_terminal_key" -> {
                if (!terminalEnabled) {
                    return disabledMessage(com.termux.R.string.zt_agent_ai_terminal_tool_disabled)
                }
                ZtAgentAiTerminalExecutor.execute(toolCall)
            }
            "run_zt_command" -> {
                if (!ztControlEnabled) {
                    return disabledMessage(com.termux.R.string.zt_agent_ai_zt_control_tool_disabled)
                }
                if (!terminalEnabled) {
                    return disabledMessage(com.termux.R.string.zt_agent_ai_zt_requires_terminal)
                }
                runZtCommand(toolCall)
            }
            else -> "Error: unknown tool `${toolCall.name}`"
        }
    }

    private fun disabledMessage(@androidx.annotation.StringRes messageRes: Int): String {
        return ZtLocaleStrings.getString(messageRes)
    }

    fun statusLabel(toolName: String): String {
        if (toolName in configToolNames) {
            return ZtAiConfigExecutor.statusLabel(toolName)
        }
        if (toolName in ZtAgentAiFilesystemExecutor.toolNames) {
            return ZtAgentAiFilesystemExecutor.statusLabel(toolName)
        }
        if (toolName in ZtAgentAiSkillTools.toolNames) {
            return ZtAgentAiSkillExecutor.statusLabel(toolName)
        }
        return when (toolName) {
            "read_terminal" -> ZtLocaleStrings.getString(R.string.zt_agent_ai_tool_read_terminal)
            "send_terminal_command" -> ZtLocaleStrings.getString(R.string.zt_agent_ai_tool_send_command)
            "send_terminal_key" -> ZtLocaleStrings.getString(R.string.zt_agent_ai_tool_send_key)
            "run_zt_command" -> ZtLocaleStrings.getString(R.string.zt_agent_ai_tool_run_command)
            else -> ZtLocaleStrings.getString(R.string.zt_agent_ai_tool_running)
        }
    }

    private fun runZtCommand(toolCall: ZtAgentAiChatClient.ToolCall): String {
        val args = JSONObject(toolCall.arguments.ifBlank { "{}" })
        val command = args.optString("command", "").trim()
        if (command.isEmpty()) {
            return "Error: command is required. Run help first. Examples: help, openpage zt_settings, openleft, toast hello"
        }
        android.util.Log.i("ZT_CMD", "run_zt_command via terminal: zt $command")
        return ZtAgentAiTerminalExecutor.runZtCommand(command)
    }
}
