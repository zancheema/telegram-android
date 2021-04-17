package com.zancheema.android.telegram.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.zancheema.android.telegram.EventObserver
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.chats.ChatsFragmentDirections.Companion.actionChatsFragmentToChatFragment
import com.zancheema.android.telegram.databinding.ChatsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private val viewModel by viewModels<ChatsViewModel>()

    private lateinit var viewDataBinding: ChatsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = ChatsFragmentBinding.inflate(inflater, container, false)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setUpChatList()
        setUpVisibility()
        setUpNavigation()
        setUpToolbar()
    }

    private fun setUpToolbar() {
        val navController = findNavController()
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.mainDrawerLayout)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        NavigationUI.setupWithNavController(
            viewDataBinding.toolbar,
            navController,
            appBarConfiguration
        )
    }

    private fun setUpNavigation() {
        viewModel.openChatsEvent.asLiveData().observe(viewLifecycleOwner, EventObserver { chat ->
            findNavController().navigate(actionChatsFragmentToChatFragment(chat))
        })
    }

    private fun setUpVisibility() {
        viewModel.emptyChatsEvent.asLiveData().observe(viewLifecycleOwner, EventObserver { empty ->
            viewDataBinding.apply {
                tvEmptyChats.visibility = if (empty) View.VISIBLE else View.GONE
                chatsList.visibility = if (empty) View.GONE else View.VISIBLE
            }
        })
    }

    private fun setUpChatList() {
        val adapter = ChatListAdapter(viewModel)
        viewDataBinding.chatsList.adapter = adapter
        viewModel.chats.asLiveData().observe(viewLifecycleOwner) { chats ->
            adapter.submitList(chats)
        }
    }
}