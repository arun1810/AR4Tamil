package Listeners

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

open class RecyclerTouchListener(context: Context?, recycleView: RecyclerView, clicklistener: RecyclerClickListener?) : RecyclerView.OnItemTouchListener{
    private var clicklistener: RecyclerClickListener? = null
    private var gestureDetector: GestureDetector? = null

    init {
        this.clicklistener = clicklistener
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recycleView.findChildViewUnder(e.x, e.y)
                if (child != null && clicklistener != null) {
                    clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && clicklistener != null && gestureDetector!!.onTouchEvent(e)) {
            clicklistener!!.onClick(child, rv.getChildAdapterPosition(child))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }


    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
