package net.cd1369.tbs.android.util

import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver

abstract class OnChangeCallback : AdapterDataObserver() {

    abstract fun onDataChange()

    override fun onChanged() = onDataChange()

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = onDataChange()

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = onDataChange()

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = onDataChange()

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) =
        onDataChange()

}