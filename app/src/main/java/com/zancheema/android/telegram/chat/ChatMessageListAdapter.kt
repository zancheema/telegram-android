package com.zancheema.android.telegram.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.databinding.ChatMessageMineBinding
import com.zancheema.android.telegram.databinding.ChatMessageOtherBinding

class ChatMessageListAdapter :
    ListAdapter<ChatMessage, ChatMessageListAdapter.ViewHolder>(ChatMessageDiffUtil()) {


    sealed class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private class MyChatMessageViewHolder(
            private val binding: ChatMessageMineBinding
        ) : ViewHolder(binding.root) {

            override fun bind(chatMessage: ChatMessage) {
                binding.chatMessage = chatMessage
            }
        }

        private class OtherChatMessageViewHolder(
            private val binding: ChatMessageOtherBinding
        ) : ViewHolder(binding.root) {

            override fun bind(chatMessage: ChatMessage) {
                binding.chatMessage = chatMessage
            }
        }

        abstract fun bind(chatMessage: ChatMessage)

        companion object {
            // ViewHolder Factory
            fun from(binding: ViewDataBinding): ViewHolder {
                return when (binding) {
                    is ChatMessageMineBinding -> MyChatMessageViewHolder(binding)
                    is ChatMessageOtherBinding -> OtherChatMessageViewHolder(binding)
                    else -> error("Invalid Chat Message list item")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == 1) ChatMessageMineBinding.inflate(inflater, parent, false)
        else ChatMessageOtherBinding.inflate(inflater, parent, false)
        return ViewHolder.from(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isMine) 1 else 0
    }
}

class ChatMessageDiffUtil : DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}