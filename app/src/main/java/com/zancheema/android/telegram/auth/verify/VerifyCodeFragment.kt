package com.zancheema.android.telegram.auth.verify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zancheema.android.telegram.EventObserver
import com.zancheema.android.telegram.auth.verify.VerifyCodeFragmentDirections.Companion.actionVerifyCodeFragmentToRegisterFragment
import com.zancheema.android.telegram.chats.ChatsFragmentDirections.Companion.actionGlobalChatsFragment
import com.zancheema.android.telegram.databinding.VerifyCodeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    private val viewModel by viewModels<VerifyCodeViewModel>()
    private lateinit var viewDataBinding: VerifyCodeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = VerifyCodeFragmentBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setUpArgs()
        setUpNavigation()
    }

    private fun setUpNavigation() {
        viewModel.showRegistrationEvent.observe(viewLifecycleOwner, EventObserver { phoneNumber ->
            findNavController().navigate(actionVerifyCodeFragmentToRegisterFragment(phoneNumber))
        })
        viewModel.showChatsEvent.observe(viewLifecycleOwner, EventObserver {
            if (it) findNavController().navigate(actionGlobalChatsFragment())
        })
    }

    private fun setUpArgs() {
        val args = VerifyCodeFragmentArgs.fromBundle(requireArguments())
        viewModel.phoneNumber.value = args.phoneNumber
        viewModel.verificationId.value = args.verificationId
    }
}