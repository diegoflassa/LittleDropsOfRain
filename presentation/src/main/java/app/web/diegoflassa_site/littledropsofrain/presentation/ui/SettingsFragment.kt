/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.presentation.ui

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import app.web.diegoflassa_site.littledropsofrain.R
import app.web.diegoflassa_site.littledropsofrain.domain.preferences.MyOnSharedPreferenceChangeListener

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mpcl: SharedPreferences.OnSharedPreferenceChangeListener

    companion object {
        const val LOGGED_USER_EMAIL_KEY = "LOGGED_USER_EMAIL_KEY"
        const val SUBSCRIBED_LANGUAGE_KEY = "SUBSCRIBED_LANGUAGE_KEY"
        @Suppress("Unused")
        private val TAG = SettingsFragment::class.simpleName
        fun newInstance(): Fragment {
            return SettingsFragment()
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        registerPreferencesListener()
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val manager = requireActivity().packageManager
        val info = manager.getPackageInfo(requireContext().packageName, 0)

        val versionCode = PreferenceCategory(requireContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            versionCode.title = "Version Code: ${info.longVersionCode}"
        } else {
            versionCode.title = "Version Code: ${info.versionCode}"
        }
        preferenceScreen.addPreference(versionCode)

        val versionName = PreferenceCategory(requireContext())
        versionName.title = "Version Name: ${info.versionName}"
        preferenceScreen.addPreference(versionName)

        val permissions = PreferenceCategory(requireContext())
        permissions.title = "Permissions: ${info.permissions ?: "None"}"
        preferenceScreen.addPreference(permissions)

        updateUI()
    }

    private fun updateUI() {
    }

    override fun onDestroy() {
        unregisterPreferencesListener()
        super.onDestroy()
    }

    private fun registerPreferencesListener() {
        mpcl = MyOnSharedPreferenceChangeListener(requireContext())
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(
                mpcl
            )
    }

    private fun unregisterPreferencesListener() {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(
                mpcl
            )
    }
}
