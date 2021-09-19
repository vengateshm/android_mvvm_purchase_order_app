package com.android.purchaseorder.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.purchaseorder.R
import com.android.purchaseorder.databinding.ActivityMainBinding
import com.android.purchaseorder.presentation.fragments.MoreFragment
import com.android.purchaseorder.presentation.fragments.OrderListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    private lateinit var orderLiFragment: Fragment
    private lateinit var moreFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        orderLiFragment = OrderListFragment.newInstance()
        moreFragment = MoreFragment.newInstance()

        setCurrentFragment(orderLiFragment)
        supportActionBar?.title = getString(R.string.orders)

        activityMainBinding.bottomMenu.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mnuOrders -> {
                    setCurrentFragment(orderLiFragment)
                    supportActionBar?.title = getString(R.string.orders)
                }
                R.id.mnuMore -> {
                    setCurrentFragment(moreFragment)
                    supportActionBar?.title = getString(R.string.more)
                }
                else -> {
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }
    }
}