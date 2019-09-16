package com.moufee.boilerfit.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.moufee.boilerfit.FriendsList
import com.moufee.boilerfit.FriendsProfilePicture
import com.moufee.boilerfit.R
import com.moufee.boilerfit.ui.dining.DiningCourtMenuFragment
import com.moufee.boilerfit.ui.dining.DiningFragment
import com.moufee.boilerfit.ui.settings.SettingsActivity
import com.moufee.boilerfit.util.NotificationWorker
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface Communicator {
    fun respond(name: String)
}
const val KEY_GOTO = "goto"
const val MENUS = "menus"
const val COREC = "corec"


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, Communicator {

    @Inject
    lateinit var mFirebaseAuth: FirebaseAuth

    @Inject
    lateinit var mDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var mSharedPreferences: SharedPreferences


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return mDispatchingAndroidInjector
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is DiningCourtMenuFragment) {
            supportFragmentManager.popBackStack()
        }
        when (item.itemId) {
            R.id.navigation_home -> {
                if (currentFragment !is HomePageFragment)
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, HomePageFragment.newInstance())
                            .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_menus -> {
                if (currentFragment !is DiningFragment)
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, DiningFragment.newInstance())
                            .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_corec -> {
                if (currentFragment !is CorecFragment) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, CorecFragment.newInstance())
                            .commit()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.friends_list -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FriendsList.newInstance())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        mSharedPreferences.edit().putString("last_launch_date", DateTime.now().toString())
                .apply()
        setContentView(R.layout.activity_main)

        if (mFirebaseAuth.currentUser == null) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
            return
        }

        scheduleNotificationWorker()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        when {
            intent.getStringExtra(KEY_GOTO) == MENUS -> navigation.selectedItemId = R.id.navigation_menus
            intent.getStringExtra(KEY_GOTO) == COREC -> navigation.selectedItemId = R.id.navigation_corec
            supportFragmentManager.findFragmentById(R.id.fragment_container) == null -> navigation.selectedItemId = R.id.navigation_home
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_action_log_out -> {
                mFirebaseAuth.signOut()
                // could remove this and listen for auth state change instead
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
            R.id.menu_action_settings -> {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun scheduleNotificationWorker() {
        val notificationTaskBuilder = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 1, TimeUnit.DAYS)
        val notificationWork = notificationTaskBuilder.build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("NotificationWorker", ExistingPeriodicWorkPolicy.KEEP, notificationWork)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when {
            intent?.getStringExtra(KEY_GOTO) == MENUS -> navigation.selectedItemId = R.id.navigation_menus
            intent?.getStringExtra(KEY_GOTO) == COREC -> navigation.selectedItemId = R.id.navigation_corec
        }
    }
    override fun respond(name: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FriendsProfilePicture.newInstance(name))
                .commit()
    }
}
