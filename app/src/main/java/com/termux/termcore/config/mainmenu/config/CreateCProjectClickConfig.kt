package com.termux.termcore.config.mainmenu.config

import com.termux.R
import com.termux.termcore.editor.EditorHelloProjectType

class CreateCProjectClickConfig : CreateEditorProjectClickConfig(
    EditorHelloProjectType.C,
    R.drawable.ic_project_c,
    R.string.menu_create_project_c
)
