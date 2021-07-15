package net.cd1369.tbs.android.ui.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_folder.*
import net.cd1369.tbs.android.R
import net.cd1369.tbs.android.data.entity.FavoriteEntity
import net.cd1369.tbs.android.ui.adapter.FolderFavoriteAdapter
import net.cd1369.tbs.android.util.doClick

/**
 * Created by Xiang on 2021/7/15 14:29
 * @description
 * @email Cymbidium@outlook.com
 */
class SelectFolderDialog : DialogFragment() {
    private lateinit var folderList: MutableList<FavoriteEntity>

    companion object {
        fun showDialog(
            fragmentManager: FragmentManager,
            tag: String,
            folders: MutableList<FavoriteEntity>
        ): SelectFolderDialog {
            return SelectFolderDialog().apply {
                show(fragmentManager, tag)
                folderList = folders
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_confirm.isSelected = true

        var selectId = folderList[0].id

        rv_content.layoutManager =
            object : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        val adapter = object : FolderFavoriteAdapter() {
            override fun onItemClick(id: String) {
                selectId = id
            }
        }

        adapter.setNewData(folderList)
        rv_content.adapter = adapter

        layout_add doClick {
            onCreateClick?.onCreate()
        }

        text_cancel doClick {
            dismiss()
        }

        text_confirm doClick {
            onConfirmClick?.onConfirm(selectId)
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val dm = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(dm)
            val width = dm.widthPixels * 0.88
            val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT

            val params = window!!.attributes
            params.gravity = Gravity.CENTER
            window.attributes = params
            window.setLayout(width.toInt(), height)
            window.setBackgroundDrawableResource(R.drawable.draw_white_12)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
        }
    }

    var onConfirmClick: OnConfirmClick? = null

    fun interface OnConfirmClick {
        fun onConfirm(content: String)
    }

    var onCreateClick: OnCreateClick? = null

    fun interface OnCreateClick {
        fun onCreate()
    }
}