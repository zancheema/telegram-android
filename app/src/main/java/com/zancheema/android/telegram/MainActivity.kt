package com.zancheema.android.telegram

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.zancheema.android.telegram.auth.AuthFragmentDirections.Companion.actionGlobalAuthFragment
import com.zancheema.android.telegram.register.RegisterFragmentDirections.Companion.actionGlobalRegisterFragment
import com.zancheema.android.telegram.util.CommonBindings.setPhotoUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val drawerLayout = findViewById<DrawerLayout>(R.id.mainDrawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        setUpNavViewHeader(navView)
        setUpDrawer(navController, drawerLayout, navView)
        setUpNavigation(navController)
    }

    private fun setUpNavViewHeader(navView: NavigationView) {
        val header = navView.inflateHeaderView(R.layout.header_side_bar)

        val tvUserName = header.findViewById<TextView>(R.id.tvUsername)
        val tvPhoneNumber = header.findViewById<TextView>(R.id.tvPhoneNumber)
        val ivProfile = header.findViewById<ImageView>(R.id.ivProfile)

        viewModel.currentUser.asLiveData().observe(this) {
            it?.let { user ->
                tvUserName.text = user.fullName
                tvPhoneNumber.text = user.phoneNumber
                setPhotoUrl(ivProfile, user.photoUrl)
            }
        }
    }

    /** set up navigation based on the user authentication status */
    private fun setUpNavigation(navController: NavController) {
        viewModel.authStateEvent.asLiveData().observe(this, EventObserver { state ->

            when (state) {
                MainViewModel.AuthState.LOGGED_OUT -> navController.navigate(
                    actionGlobalAuthFragment()
                )
                MainViewModel.AuthState.LOGGED_IN -> navController.navigate(
                    actionGlobalRegisterFragment(
                        viewModel.getCurrentUserPhoneNumber()
                    )
                )
            }
        })
    }

    /**
     * Set up drawer layout with the toolbar
     */
    private fun setUpDrawer(
        navController: NavController,
        drawerLayout: DrawerLayout,
        navView: NavigationView
    ) {
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val lockMode = when (isAuthDestination(destination.id)) {
                true -> DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                else -> DrawerLayout.LOCK_MODE_UNLOCKED
            }
            drawerLayout.setDrawerLockMode(lockMode)
        }
    }

    private fun isAuthDestination(destinationId: Int): Boolean {
        return destinationId == R.id.authFragment
                || destinationId == R.id.verifyCodeFragment
                || destinationId == R.id.registerFragment
    }
}