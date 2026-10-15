package com.termux.termcore.config.mainmenu.config

import com.termux.R
import com.termux.termcore.editor.EditorHelloProjectType

class CreateJavaProjectClickConfig : CreateEditorProjectClickConfig(
    EditorHelloProjectType.JAVA,
    R.drawable.ic_project_java,
    R.string.menu_create_project_java
)
