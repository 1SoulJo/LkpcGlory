package com.lkpc.android.app.glory.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.lkpc.android.app.glory.BuildConfig
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.constants.Notification
import com.lkpc.android.app.glory.constants.SharedPreference
import com.lkpc.android.app.glory.ui.qr_code.QrCodeScanActivity

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {

    private lateinit var pushGeneralPref: SwitchPreferenceCompat
    private lateinit var pushUrgentPref: SwitchPreferenceCompat
    private lateinit var adminPref: SwitchPreferenceCompat
    private lateinit var testPref: SwitchPreferenceCompat
    private lateinit var qrPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        pushGeneralPref =
            preferenceScreen.findPreference(SharedPreference.NOTIFICATION_TOPIC_GENERAL)!!
        pushGeneralPref.onPreferenceChangeListener = this

        pushUrgentPref =
            preferenceScreen.findPreference(SharedPreference.NOTIFICATION_TOPIC_URGENT)!!
        pushUrgentPref.onPreferenceChangeListener = this

        adminPref =
            preferenceScreen.findPreference(SharedPreference.ADMIN_MODE)!!
        adminPref.onPreferenceChangeListener = this

        testPref =
            preferenceScreen.findPreference(SharedPreference.NOTIFICATION_TOPIC_TEST)!!
        testPref.onPreferenceChangeListener = this
        testPref.isVisible = adminPref.isChecked

        qrPref = preferenceScreen.findPreference(SharedPreference.QR_SCANNER)!!
        qrPref.onPreferenceClickListener = this
        qrPref.isVisible = adminPref.isChecked
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            SharedPreference.NOTIFICATION_TOPIC_GENERAL -> {
                if (newValue as Boolean) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Notification.TOPIC_GENERAL)
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Notification.TOPIC_GENERAL)
                }
            }

            SharedPreference.NOTIFICATION_TOPIC_URGENT -> {
                if (newValue as Boolean) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Notification.TOPIC_URGENT)
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Notification.TOPIC_URGENT)
                }
            }

            SharedPreference.NOTIFICATION_TOPIC_TEST -> {
                if (newValue as Boolean) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Notification.TOPIC_TEST)
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Notification.TOPIC_TEST)
                }
            }

            SharedPreference.ADMIN_MODE -> {
                if (newValue as Boolean) {
                    showPasswordAlert()
                } else {
                    testPref.isVisible = false
                    qrPref.isVisible = false
                }
            }
        }

        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            SharedPreference.QR_SCANNER -> {
                val i = Intent(requireContext(), QrCodeScanActivity::class.java)
                startActivity(i)
            }
        }

        return true
    }

    private fun showPasswordAlert() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter password")

        val sp = requireActivity().getSharedPreferences(SharedPreference.QR_PREFERENCE, Context.MODE_PRIVATE)
        val password = sp.getString(SharedPreference.QR_KEY_PASSWORD, "")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        if (password != null && password.isNotEmpty()) {
            input.setText(password)
        }
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            if (BuildConfig.QR_PASS.equals(input.text.toString(), false)) {
                testPref.isVisible = true
                qrPref.isVisible = true
                sp.edit().putString(SharedPreference.QR_KEY_PASSWORD, input.text.toString()).apply()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}