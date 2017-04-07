/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.wifi.details;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.applications.LayoutPreference;
import com.android.settings.core.PreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.wifi.AccessPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Detail page for the currently connected wifi network.
 *
 * <p>The AccessPoint should be saved to the intent Extras when launching this class via
 * {@link AccessPoint#saveWifiState(Bundle)} in order to properly render this page.
 */
public class WifiNetworkDetailsFragment extends DashboardFragment {
    private static final String TAG = "WifiNetworkDetailsFrg";

    // XML KEYS
    private static final String KEY_FORGET_BUTTON = "forget_button";

    private AccessPoint mAccessPoint;
    private Button mForgetButton;
    private WifiDetailPreferenceController mWifiDetailPreferenceController;

    @Override
    public void onAttach(Context context) {
        mAccessPoint = new AccessPoint(context, getArguments());
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Header Title set automatically from launching Preference

        LayoutPreference forgetPreference = ((LayoutPreference) findPreference(KEY_FORGET_BUTTON));
        forgetPreference.setVisible(mWifiDetailPreferenceController.canForgetNetwork());
        mForgetButton = (Button) forgetPreference.findViewById(R.id.button);
        mForgetButton.setText(R.string.forget);
        mForgetButton.setOnClickListener(view -> forgetNetwork());
    }

    private void forgetNetwork() {
        mMetricsFeatureProvider.action(getActivity(), MetricsProto.MetricsEvent.ACTION_WIFI_FORGET);
        mWifiDetailPreferenceController.forgetNetwork();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.WIFI_NETWORK_DETAILS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.wifi_network_details_fragment;
    }

    @Override
    protected List<PreferenceController> getPreferenceControllers(Context context) {
        mWifiDetailPreferenceController = new WifiDetailPreferenceController(
                mAccessPoint,
                context.getSystemService(ConnectivityManager.class),
                context,
                this,
                getLifecycle(),
                context.getSystemService(WifiManager.class));

        ArrayList<PreferenceController> controllers = new ArrayList(1);
        controllers.add(mWifiDetailPreferenceController);
        return controllers;
    }
}
