package com.gdn.android.onestop.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gdn.android.onestop.R
import com.gdn.android.onestop.base.BaseActivity
import com.gdn.android.onestop.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


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

        navController.addOnDestinationChangedListener{ _, destination, _ ->

            when(destination.id){
                R.id.ideaDetailFragment, R.id.ideaCreateFragment
                -> findViewById<BottomNavigationView>(R.id.nav_bottom_main).visibility = View.GONE
                else -> findViewById<BottomNavigationView>(R.id.nav_bottom_main).visibility = View.VISIBLE

            }
//            if(destination.id == R.id.ideaDetailFragment){
//                findViewById<BottomNavigationView>(R.id.nav_bottom_main).visibility = View.GONE
//            }
//            else{
//                findViewById<BottomNavigationView>(R.id.nav_bottom_main).visibility = View.VISIBLE
//            }
        }
    }


    private fun setActionBar(navController: NavController){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_fragment).navigateUp(appBarConfiguration)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav: BottomNavigationView = findViewById(R.id.nav_bottom_main)
        bottomNav.setupWithNavController(navController)
    }

}