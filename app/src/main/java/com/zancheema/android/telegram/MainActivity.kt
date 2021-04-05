package com.zancheema.android.telegram

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.zancheema.android.telegram.auth.FirebaseUserLiveData
import com.zancheema.android.telegram.chats.ChatsFragmentDirections.Companion.actionChatsFragmentToAuthFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbarMain)
        val drawerLayout = findViewById<DrawerLayout>(R.id.mainDrawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.visibility = if (isAuthDestination(destination.id)) GONE else VISIBLE
        }

        FirebaseUserLiveData().observe(this) { user ->
            if (user == null) {
                navController.navigate(actionChatsFragmentToAuthFragment())
            }
        }
    }

    private fun isAuthDestination(destinationId: Int): Boolean {
        return destinationId == R.id.authFragment
                || destinationId == R.id.verifyCodeFragment
                || destinationId == R.id.registerFragment
    }
}