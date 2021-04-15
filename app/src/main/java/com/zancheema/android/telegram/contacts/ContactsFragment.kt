package com.zancheema.android.telegram.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.databinding.ContactsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private val viewModel by viewModels<ContactsViewModel>()

    private lateinit var viewDataBinding: ContactsFragmentBinding

    @Inject
    lateinit var contentProvider: AppContentProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = ContactsFragmentBinding.inflate(inflater, container, false)
        return viewDataBinding.root
    }

    @FlowPreview
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setUpContactList()

        checkSelfPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_READ_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setUpContacts()
        }
    }

    private fun checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setUpContacts()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                RC_READ_CONTACTS
            )
        }
    }

    private fun setUpContacts() {
        val phoneNumbers = contentProvider.getContactPhoneNumbers(requireActivity().contentResolver)
        viewModel.setContactNumbers(phoneNumbers)
    }

    @FlowPreview
    private fun setUpContactList() {
        val adapter = ContactListAdapter()
        viewDataBinding.contactList.adapter = adapter
        viewModel.userDetails.asLiveData().observe(viewLifecycleOwner) { userDetails ->
            adapter.submitList(userDetails)
        }
    }

    companion object {
        const val RC_READ_CONTACTS = 100
    }
}