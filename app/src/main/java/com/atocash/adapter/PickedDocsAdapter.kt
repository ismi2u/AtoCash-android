package com.atocash.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.adapter.PickedDocsAdapter.PickedDocsItem
import com.atocash.network.response.PostDocumentsResponse
import com.atocash.utils.extensions.getExtension
import com.atocash.utils.extensions.getFileName

class PickedDocsAdapter(
    private val hideDelete: Boolean,
    private val callback: PickedDocsCallback
) :
    RecyclerView.Adapter<PickedDocsItem>() {

    private val items: ArrayList<PostDocumentsResponse?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedDocsItem {
        return PickedDocsItem(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chosen_document, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PickedDocsItem, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class PickedDocsItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val parentLayout: LinearLayout = itemView.findViewById(R.id.docs_parent)
        private val fileNameTv: TextView = itemView.findViewById(R.id.docs_name_tv)
        private val deleteBtn: ImageView = itemView.findViewById(R.id.delete_btn)

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {

            if (hideDelete) {
                val fileName = items[position]?.actualFileName.toString()
                if (fileName.isEmpty()) {
                    fileNameTv.text = "File $position"
                } else {
                    when (fileName.getExtension()) {
                        ".jpg", ".jpeg", ".png" -> {
                            fileNameTv.text = "Image File - ${fileName.getFileName()}"
                        }
                        else -> {
                            fileNameTv.text = "File $position"
                        }
                    }
                }
            } else {
                fileNameTv.text = items[position]?.actualFileName ?: "File $position"
            }

            deleteBtn.visibility = if (hideDelete) View.GONE else View.VISIBLE

            parentLayout.setOnClickListener {
                items[position]?.let { it1 -> callback.onView(it1) }
            }

            deleteBtn.setOnClickListener {
                items[position]?.let { it1 -> callback.onDelete(it1) }
            }
        }
    }

    fun addItem(fileItems: PostDocumentsResponse) {
        items.add(fileItems)
        notifyDataSetChanged()
    }

    fun addAll(fileItems: ArrayList<PostDocumentsResponse>) {
        items.addAll(fileItems)
        notifyDataSetChanged()
    }

    fun removeItem(item: PostDocumentsResponse) {
        items.remove(item)
        notifyDataSetChanged()
    }

    interface PickedDocsCallback {
        fun onDelete(item: PostDocumentsResponse)
        fun onView(item: PostDocumentsResponse)
    }
}