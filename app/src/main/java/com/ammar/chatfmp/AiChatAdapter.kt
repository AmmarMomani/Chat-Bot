package com.ammar.chatfmp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AiChatAdapter(private val messageModelList: List<AiChat>) :
    RecyclerView.Adapter<AiChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.aichat_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageModelList[position]

        if (message.sentBy == AiChat.SENT_BY_ME) {
            holder.topChatLayout.visibility = View.VISIBLE
            holder.topText.text = message.message
            holder.bottomChatLayout.visibility = View.GONE
            holder.username.text = "Me"
            holder.senderImage.setImageResource(R.drawable.ic_person)
        } else {
            holder.topChatLayout.visibility = View.GONE
            holder.bottomText.text = message.message
            holder.bottomChatLayout.visibility = View.VISIBLE
            holder.username.text = "QuickerBoot"
            holder.senderImage.setImageResource(R.drawable.ic_robot)
        }
    }

    override fun getItemCount(): Int {
        return messageModelList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topChatLayout: LinearLayout = itemView.findViewById(R.id.top_chatview)
        val bottomChatLayout: LinearLayout = itemView.findViewById(R.id.bottom_chatview)
        val topText: TextView = itemView.findViewById(R.id.top_text)
        val bottomText: TextView = itemView.findViewById(R.id.bottom_text)
        val username: TextView = itemView.findViewById(R.id.txt_name)
        val senderImage: ImageView = itemView.findViewById(R.id.profile_image)
    }
}
