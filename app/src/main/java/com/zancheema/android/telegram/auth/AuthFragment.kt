package com.zancheema.android.telegram.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.EventObserver
import com.zancheema.android.telegram.auth.AuthFragmentDirections.Companion.actionAuthFragmentToVerifyCodeFragment
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.databinding.AuthFragmentBinding
import com.zancheema.android.telegram.register.RegisterFragmentDirections.Companion.actionGlobalRegisterFragment
import com.zancheema.android.telegram.util.EspressoIdlingResource
import com.zancheema.android.telegram.util.setUpSnackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val viewModel by viewModels<AuthViewModel>()

    private lateinit var viewDataBinding: AuthFragmentBinding

    @Inject
    lateinit var contentProvider: AppContentProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = AuthFragmentBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setUpSnackbar()
        setUpVerification()
    }

    private fun setUpVerification() {
        viewModel.signInEvent.observe(viewLifecycleOwner, EventObserver { phoneNumber ->
            verifyPhoneNumber(phoneNumber)
        })
    }

    private fun setUpSnackbar() {
        requireView().setUpSnackbar(
            viewLifecycleOwner,
            viewModel.invalidCredentialsEvent,
            Snackbar.LENGTH_SHORT
        )
    }

    private fun verifyPhoneNumber(phoneNumber: String) {
        val navController = contentProvider.findNavController(this)

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    EspressoIdlingResource.decrement()
                    val action = actionAuthFragmentToVerifyCodeFragment(
                        phoneNumber,
                        verificationId
                    )
                    navController.navigate(action)
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Firebase.auth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener { task ->
                            EspressoIdlingResource.decrement()
                            if (task.isSuccessful) {
                                viewModel.phoneNumber.value?.let { phoneNumber ->
                                    navController.navigate(
                                        actionGlobalRegisterFragment(phoneNumber)
                                    )
                                }
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    TODO("Not implemented yet")
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    TODO("Not implemented yet")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        EspressoIdlingResource.increment()
    }
}