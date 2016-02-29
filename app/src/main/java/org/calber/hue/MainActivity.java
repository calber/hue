package org.calber.hue;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import api.ApiBuilder;
import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.FragmentInteraction;
import fragments.GroupsFragment;
import fragments.HueFragment;
import fragments.LightsFragment;
import fragments.WhitelistFragment;
import models.RequestUser;
import models.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import upnp.UPnPDeviceFinder;

public class MainActivity extends AppCompatActivity implements FragmentInteraction {

    public static final String FRAGMENTTITLE = "title";

    @Bind(R.id.root)
    View root;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.container)
    ViewPager container;
    @Bind(R.id.tabs)
    TabLayout tabs;

    private ArrayList<HueFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Hue.TOKEN = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("TOKEN", null);
        Hue.URL = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("URL", null);
        if (Hue.TOKEN == null) {
            createConnection();
        } else {
            apiConfigurationAll();
        }
    }

    private void apiCreateUser() {
        Hue.api.createUser(new RequestUser("calberhue#" + Hue.androidId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> processRegistrationResult(response.get(0))
                        , throwable -> {
                            Snackbar.make(root, "Fail to create user!", Snackbar.LENGTH_LONG).show();
                            Log.e(Hue.TAG, "failed", throwable);
                        });
    }

    private void apiConfigurationAll() {
        Hue.api = ApiBuilder.newInstance(Hue.URL);
        Hue.api.all(Hue.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configuration -> {
                    Hue.hueConfiguration = configuration;

                    fragments = new ArrayList<>();
                    fragments.add(LightsFragment.newInstance("LIGHTS"));
                    fragments.add(GroupsFragment.newInstance("GROUPS"));
                    fragments.add(WhitelistFragment.newInstance("WHITELIST"));

                    container.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
                    tabs.setupWithViewPager(container);

                }, throwable -> {
                    // fall back, see if IP has changed
                    Snackbar.make(root,"Failed to connect to HUB",Snackbar.LENGTH_INDEFINITE)
                            .setAction("SCAN AGAIN",v -> findConnection())
                            .show();
                    Log.e(Hue.TAG, "", throwable);
                });
    }

    private void createConnection() {
        new UPnPDeviceFinder().observe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    Hue.URL = String.format("%s://%s/"
                            , device.getLocation().getProtocol()
                            , device.getLocation().getHost());
                    getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", Hue.URL).commit();
                    Hue.api = ApiBuilder.newInstance(Hue.URL);

                    Snackbar.make(root, "Found HUB: " + device.getFriendlyName() + " press Connect on HUB", Snackbar.LENGTH_INDEFINITE)
                            .setAction("CONNECT", v -> apiCreateUser())
                            .show();
                }, t -> {
                    Snackbar.make(root, "Failed to find Hue HUB on this network", Snackbar.LENGTH_INDEFINITE).show();
                });
    }


    private void processRegistrationResult(Response response) {
        if (response.success != null) {
            Hue.TOKEN = response.success.username;
            getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", response.success.username).commit();
            apiConfigurationAll();
            Snackbar.make(root, response.success.username, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(root, response.error.description, Snackbar.LENGTH_LONG).show();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getArguments().getString(FRAGMENTTITLE);
        }
    }

    @Override
    public void findConnection() {
        new UPnPDeviceFinder().observe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    Hue.URL = String.format("%s://%s/"
                            , device.getLocation().getProtocol()
                            , device.getLocation().getHost());
                    getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", Hue.URL).commit();
                    Hue.api = ApiBuilder.newInstance(Hue.URL);
                    Snackbar.make(root, "Found HUB: " + device.getFriendlyName(), Snackbar.LENGTH_LONG)
                            .show();
                    apiConfigurationAll();
                }, t -> {
                    Snackbar.make(root, "Failed to find Hue HUB on this network", Snackbar.LENGTH_INDEFINITE)
                            .setAction("EXIT", v -> finish())
                            .show();
                });
    }

    @Override
    public View getRootView() {
        return root;
    }


}
