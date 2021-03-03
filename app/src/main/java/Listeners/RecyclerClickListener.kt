package Listeners

import android.view.View

interface RecyclerClickListener {
    fun onClick(view: View?, position: Int)
    fun onLongClick(view: View?, position: Int)
}