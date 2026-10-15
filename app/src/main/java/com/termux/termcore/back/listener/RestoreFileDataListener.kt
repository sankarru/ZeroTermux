package com.termux.termcore.back.listener

import com.termux.termcore.back.bean.DataBean
import java.io.File

interface RestoreFileDataListener {
    fun file(mDataBean: DataBean)
}
