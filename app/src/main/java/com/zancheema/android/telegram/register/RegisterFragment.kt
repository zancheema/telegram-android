package com.zancheema.android.telegram.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.google.android.material.snackbar.Snackbar
import com.zancheema.android.telegram.EventObserver
import com.zancheema.android.telegram.chats.ChatsFragmentDirections.Companion.actionGlobalChatsFragment
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.databinding.RegisterFragmentBinding
import com.zancheema.android.telegram.util.setUpSnackbarNullable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel by viewModels<RegisterViewModel>()

    private lateinit var viewDataBinding: RegisterFragmentBinding

    @Inject
    lateinit var contentProvider: AppContentProvider

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
        val navController = contentProvider.findNavController(this)

        viewModel.userRegisteredEvent.asLiveData().observe(viewLifecycleOwner, EventObserver {
            if (it) navController.navigate(actionGlobalChatsFragment())
        })

        viewModel.userRegisteredEvent.asLiveData()
            .observe(viewLifecycleOwner, EventObserver { registered ->
                if (registered) navController.navigate(actionGlobalChatsFragment())
            })
    }

    private fun setUpArgs() {
        val args = RegisterFragmentArgs.fromBundle(requireArguments())
        viewModel.phoneNumber = args.phoneNumber
    }
}