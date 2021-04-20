package com.zancheema.android.telegram.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.databinding.ListItemContactBinding

class ContactListAdapter(
    private val viewModel: ContactsViewModel
) : ListAdapter<UserDetail, ContactListAdapter.ViewHolder>(UserDetailDiffUtil()) {

    class ViewHolder private constructor(
        private val binding: ListItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(detail: UserDetail, viewModel: ContactsViewModel) {
            binding.userDetail = detail
            binding.viewModel = viewModel

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemContactBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }
}

class UserDetailDiffUtil : DiffUtil.ItemCallback<UserDetail>() {
    override fun areItemsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
        return oldItem.phoneNumber == newItem.phoneNumber
    }

    override fun areContentsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
        return oldItem == newItem
    }
}
