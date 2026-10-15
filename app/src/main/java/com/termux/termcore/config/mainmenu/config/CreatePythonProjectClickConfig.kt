package com.termux.termcore.config.mainmenu.config

import com.termux.R
import com.termux.termcore.editor.EditorHelloProjectType

class CreatePythonProjectClickConfig : CreateEditorProjectClickConfig(
    EditorHelloProjectType.PYTHON,
    R.drawable.ic_project_python,
    R.string.menu_create_project_python
)
