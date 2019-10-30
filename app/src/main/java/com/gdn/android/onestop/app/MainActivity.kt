package com.gdn.android.onestop.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.gdn.android.onestop.R
import com.gdn.android.onestop.base.BaseActivity
import com.gdn.android.onestop.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId


class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_fragment) as NavHostFragment

        val navController = host.navController
        setActionBar(navController)
        setupBottomNavMenu(navController)



    }


    private fun setActionBar(navController: NavController){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_fragment).navigateUp(appBarConfiguration)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav: BottomNavigationView = findViewById(R.id.nav_bottom_main)
        bottomNav.setupWithNavController(navController)
    }

}