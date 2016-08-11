package com.arenas.droidfan.setting;


import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.arenas.droidfan.AppContext;
import com.arenas.droidfan.R;
import com.arenas.droidfan.Util.Utils;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.login.LoginActivity;
import com.arenas.droidfan.notify.PushService;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                PushService.cancelPushService(AppContext.getContext());
                PushService.setServiceAlarm(AppContext.getContext());

            } else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener {

        private Preference loginout;
        private SwitchPreference switchNotify;
        private Preference versionCode;

        private FanFouDB mFanFouDB;
        private Context mContext;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));

            mContext = getActivity();

            mFanFouDB = FanFouDB.getInstance(mContext);

            loginout = findPreference("login_out");
            loginout.setOnPreferenceClickListener(this);

            switchNotify = (SwitchPreference)findPreference("notification");
            switchNotify.setOnPreferenceClickListener(this);

            versionCode = findPreference("version_code");
            versionCode.setSummary(Utils.getVersionCode());
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == loginout){
                mFanFouDB.deleteAll();
                AppContext.clearAccountInfo(mContext);
                Intent intent = new Intent(mContext , LoginActivity.class);
                startActivity(intent);
            }
            if (preference == switchNotify){
                if (PushService.isServiceAlarmOn(mContext)){
                    PushService.cancelPushService(mContext);
                }else {
                    PushService.setServiceAlarm(mContext);
                }
            }
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
