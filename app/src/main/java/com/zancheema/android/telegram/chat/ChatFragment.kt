package com.zancheema.android.telegram.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.databinding.ChatFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val viewModel by viewModels<ChatViewModel>()

    private lateinit var viewDataBinding: ChatFragmentBinding

    @Inject
    lateinit var contentProvider: AppContentProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = ChatFragmentBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        return viewDataBinding.root
    }

    @FlowPreview
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setUpArgs()
        setUpMessageList()
        setUpNavigation()
    }

    @FlowPreview
    private fun setUpMessageList() {
        val adapter = ChatMessageListAdapter()
        viewDataBinding.messageList.adapter = adapter
        viewModel.chatMessages.asLiveData().observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
        }
    }

    private fun setUpNavigation() {
        val navController = contentProvider.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        viewDataBinding.toolbar
            .setupWithNavController(navController, appBarConfiguration)
    }

    private fun setUpArgs() {
        val args = ChatFragmentArgs.fromBundle(requireArguments())
        viewModel.setChatRoomId(args.chatRoomId)
        viewModel.userDetail.asLiveData().observe(viewLifecycleOwner) { detail ->
            detail?.let { viewDataBinding.userDetail = it }
        }
    }
}