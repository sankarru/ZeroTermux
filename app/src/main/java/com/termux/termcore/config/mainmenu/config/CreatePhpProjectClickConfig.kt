package com.termux.termcore.config.mainmenu.config

import com.termux.R
import com.termux.termcore.editor.EditorHelloProjectType

class CreatePhpProjectClickConfig : CreateEditorProjectClickConfig(
    EditorHelloProjectType.PHP,
    R.drawable.ic_project_php,
    R.string.menu_create_project_php
)
