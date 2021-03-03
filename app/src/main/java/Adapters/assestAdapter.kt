package Adapters

import DataClasses.letterAndasset
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ar4tamil.R

class assestAdapter(private val data: ArrayList<letterAndasset>, private var layoutId: Int, private var context: Context) : RecyclerView.Adapter<assestAdapter.ViewHolder>() {
    private lateinit var viewHolder:ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(layoutId, parent, false)

        viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name:String = data.get(position).letter
        holder.Assestname.text = name
    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Assestname: TextView = itemView.findViewById(R.id.assestname)

    }
}