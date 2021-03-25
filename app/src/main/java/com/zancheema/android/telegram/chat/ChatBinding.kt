package com.zancheema.android.telegram.chat

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zancheema.android.telegram.data.source.domain.ChatMessage

object ChatBinding {
    @BindingAdapter("chat_messages")
    @JvmStatic
    fun setChatMessages(recyclerView: RecyclerView, messages: List<ChatMessage>?) {
        if (messages == null) return
        if (recyclerView.adapter == null) {
            recyclerView.adapter = ChatMessagesListAdapter()
        }
        (recyclerView.adapter as ChatMessagesListAdapter).submitList(messages)
    }
}