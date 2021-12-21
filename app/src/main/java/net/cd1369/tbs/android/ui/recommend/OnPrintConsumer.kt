package net.cd1369.tbs.android.ui.recommend

import net.cd1369.tbs.android.data.entity.PrintEntity
import net.cd1369.tbs.android.data.entity.PrintItemEntity

interface OnPrintConsumer {

    fun doNext(data: List<PrintItemEntity>)

}