package com.zancheema.android.telegram

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.chats.ChatsFragmentDirections.Companion.actionChatsFragmentToAuthFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        setUpDrawer(navController)

        /** show login instead of chats of user is not logged in already */
        if (Firebase.auth.currentUser == null) {
            navController.navigate(actionChatsFragmentToAuthFragment())
        }
    }

    /**
     * Set up drawer layout with the toolbar
     */
    private fun setUpDrawer(navController: NavController) {
        val toolbar = findViewById<Toolbar>(R.id.toolbarMain)
        val drawerLayout = findViewById<DrawerLayout>(R.id.mainDrawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val (toolbarVisibility, drawerLockMode) = when (isAuthDestination(destination.id)) {
                true -> Pair(GONE, DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                else -> Pair(VISIBLE, DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            toolbar.visibility = toolbarVisibility
            drawerLayout.setDrawerLockMode(drawerLockMode)
        }
    }

    private fun isAuthDestination(destinationId: Int): Boolean {
        return destinationId == R.id.authFragment
                || destinationId == R.id.verifyCodeFragment
                || destinationId == R.id.registerFragment
    }
}