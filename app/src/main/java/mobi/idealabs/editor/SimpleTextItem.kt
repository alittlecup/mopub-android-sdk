package mobi.idealabs.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ListenerData(
    val name: String, var invoked: Boolean = false
)

class SimpleTextItem(val listenerData: List<ListenerData>) :
    RecyclerView.Adapter<SimpleTextItem.VH>() {
    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val content = view.findViewById<TextView>(R.id.content)
        val check = view.findViewById<ImageView>(R.id.check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_listener, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return listenerData.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val listenerData = listenerData[position]
        holder.content.text = listenerData.name
        holder.check.visibility = if (listenerData.invoked) View.VISIBLE else View.GONE
        holder.itemView.setBackgroundResource(if (position % 2 == 0) android.R.color.darker_gray else android.R.color.white)
    }
}