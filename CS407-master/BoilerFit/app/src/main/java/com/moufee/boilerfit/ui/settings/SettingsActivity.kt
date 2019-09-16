package com.moufee.boilerfit.ui.settings

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.moufee.boilerfit.R
import com.moufee.boilerfit.repository.UserRepository
import com.moufee.boilerfit.util.NotificationHelper
import dagger.android.AndroidInjection
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject

const val KEY_PREF_BREAKFAST_NOTIFY_TIME = "pref_breakfast_notification_time"
const val KEY_PREF_LUNCH_NOTIFY_TIME = "pref_lunch_notification_time"
const val KEY_PREF_DINNER_NOTIFY_TIME = "pref_dinner_notification_time"
const val KEY_PREF_COREC_NOTIFY_TIME = "pref_corec_notification_time"
const val KEY_PREF_ACTIVITIES = "pref_favorite_activities"
const val KEY_PREF_ALLERGIES = "pref_allergies"
const val KEY_PREF_PASSWORD = "pref_password"
const val KEY_PREF_GEOFENCE = "pref_geofence_notifications"
const val TAG = "SettingsActivity"
private const val REQUEST_CODE_LOCATION = 12345


class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val GALLERY_INTENT = 2

    @Inject
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.single_fragment_activity)
        supportFragmentManager.beginTransaction()
                .replace(R.id.single_fragment_container, SettingsFragment())
                .commit()
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        when (key) {
            KEY_PREF_BREAKFAST_NOTIFY_TIME, KEY_PREF_LUNCH_NOTIFY_TIME, KEY_PREF_DINNER_NOTIFY_TIME -> {
                val time: LocalTime = DateTimeFormat.forPattern("HH:mm").parseLocalTime(sharedPreferences?.getString(key, ""))
                var nextAlarm: DateTime = time.toDateTimeToday()
                if (nextAlarm.isBeforeNow) {
                    nextAlarm = nextAlarm.plusDays(1)
                }
                var code = 0
                when (key) {
                    KEY_PREF_BREAKFAST_NOTIFY_TIME -> code = 1
                    KEY_PREF_LUNCH_NOTIFY_TIME -> code = 2
                    KEY_PREF_DINNER_NOTIFY_TIME -> code = 3
                }
                notificationHelper.scheduleMenuNotification(nextAlarm, code)
            }

            KEY_PREF_GEOFENCE -> {
                if (sharedPreferences?.getBoolean(KEY_PREF_GEOFENCE, false) == true) {
                    requestPermissions()
                    notificationHelper.createGeofences()
                } else {
                    notificationHelper.removeGeofences()
                }
            }

            KEY_PREF_ALLERGIES -> {
                val allergies = sharedPreferences?.getStringSet(KEY_PREF_ALLERGIES, null)
                val allergiesMap = HashMap<String, Boolean>()
                allergies?.forEach { allergiesMap[it] = true }
                userRepository.getUserCallback { user ->
                    if (user != null) {
                        user.allergies = allergiesMap
                        userRepository.updateUser(user)
                    }
                }
            }
            KEY_PREF_COREC_NOTIFY_TIME -> {
                val time: LocalTime = DateTimeFormat.forPattern("HH:mm").parseLocalTime(sharedPreferences?.getString(key, ""))
                var nextAlarm: DateTime = time.toDateTimeToday()
                if (nextAlarm.isBeforeNow) {
                    nextAlarm = nextAlarm.plusDays(1)
                }
                notificationHelper.scheduleCorecNotification(nextAlarm)
            }

            KEY_PREF_ACTIVITIES -> {
                val activities = sharedPreferences?.getStringSet(KEY_PREF_ACTIVITIES, null)
                val activitiesList = activities?.toList()
                userRepository.getUserCallback { user ->
                    if (user != null) {
                        user.favoriteActivities = activitiesList
                        userRepository.updateUser(user)
                    }
                }

            }

            KEY_PREF_PASSWORD -> {
                val password = sharedPreferences?.getString(key, null)
                userRepository.updatePassword(password)
                //ideally, we would never store it in the first place
                sharedPreferences?.edit()?.remove(key)?.apply()
            }
        }
    }

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, Array<String>(1) { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_LOCATION)
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notificationHelper.createGeofences()
                } else {
                }
                return
            }
        }
    }
}
