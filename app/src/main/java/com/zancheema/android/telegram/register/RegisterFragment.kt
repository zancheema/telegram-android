package com.zancheema.android.telegram.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.zancheema.android.telegram.EventObserver
import com.zancheema.android.telegram.chats.ChatsFragmentDirections.Companion.actionGlobalChatsFragment
import com.zancheema.android.telegram.databinding.RegisterFragmentBinding
import com.zancheema.android.telegram.util.setUpSnackbarNullable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var uiJob: Job? = null
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var viewDataBinding: RegisterFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = RegisterFragmentBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setUpArgs()
        setUpNavigation()
        setUpSnackbar()
    }

    private fun setUpSnackbar() {
        requireView().setUpSnackbarNullable(
            viewLifecycleOwner,
            viewModel.messageText.asLiveData(),
            Snackbar.LENGTH_SHORT
        )
    }

    private fun setUpNavigation() {
        viewModel.userRegisteredEvent.asLiveData().observe(viewLifecycleOwner, EventObserver {
            if (it) findNavController().navigate(actionGlobalChatsFragment())
        })
    }

    override fun onStart() {
        super.onStart()
        uiJob = lifecycleScope.launch {
            viewModel.userRegisteredEvent.collect { event ->
                event?.let {
                    it.getContentIfNotHandled()?.let { registered ->
                        if (registered) findNavController().navigate(actionGlobalChatsFragment())
                    }
                }
            }
        }
    }

    override fun onStop() {
        uiJob?.cancel()
        super.onStop()
    }

    private fun setUpArgs() {
        val args = RegisterFragmentArgs.fromBundle(requireArguments())
        viewModel.phoneNumber = args.phoneNumber
    }
}