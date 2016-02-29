package org.calber.hue;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import adapters.LightAdapter;
import api.Api;
import api.ApiBuilder;
import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.GroupsFragment;
import fragments.HueFragment;
import fragments.LightsFragment;
import fragments.WhitelistFragment;
import models.AllData;
import models.RequestUser;
import models.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import upnp.UPnPDeviceFinder;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENTTITLE = "title";
    public static final int LIGHTS = 0;
    @Bind(R.id.connect)
    FloatingActionButton connect;
    @Bind(R.id.root)
    View root;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.container)
    ViewPager container;
    @Bind(R.id.tabs)
    TabLayout tabs;

    private Api api;
    private String URL;

    private ArrayList<HueFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fragments = new ArrayList<>();
        fragments.add(LightsFragment.newInstance("LIGHTS"));
        fragments.add(GroupsFragment.newInstance("GROUPS"));
        fragments.add(WhitelistFragment.newInstance("WHITELIST"));

        container.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(container);

        Hue.TOKEN = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("TOKEN", null);
        URL = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("URL", null);
        if (Hue.TOKEN == null) {
            createConnection();
        } else {
            apiCreateLightList();
        }
    }

    private void apiCreateUser() {
        api.createUser(new RequestUser("calberhue#" + Hue.androidId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> processRegistrationResult(response.get(LIGHTS))
                        , throwable -> {
                            Snackbar.make(root, "Fail to create user!", Snackbar.LENGTH_LONG).show();
                            Log.e(Hue.TAG, "failed", throwable);
                        });
    }

    private void apiCreateLightList() {
        api = ApiBuilder.newInstance(URL);
        api.all(Hue.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configuration -> {
                    loadLight(configuration);
                    loadGroups(configuration);
                    loadWhitelist(configuration);
                }, throwable -> {
                    // fall back, see if IP has changed
                    Snackbar.make(root,"Failed to connect to HUB",Snackbar.LENGTH_INDEFINITE)
                            .setAction("SCAN AGAIN",v -> findConnection())
                            .show();
                    Log.e(Hue.TAG, "", throwable);
                });
    }

    private void loadWhitelist(AllData configuration) {

    }

    private void loadGroups(AllData configuration) {

    }

    private void loadLight(AllData configuration) {
        for(String l: configuration.lights.keySet()) {
            configuration.lights.get(l).id = l;
        }
        fragments.get(LIGHTS).setAdapter(new LightAdapter(this, configuration.lights.values()));
        Log.d(Hue.TAG, configuration.toString());
    }

    private void createConnection() {
        new UPnPDeviceFinder().observe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    URL = String.format("%s://%s/"
                            , device.getLocation().getProtocol()
                            , device.getLocation().getHost());
                    getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", URL).commit();
                    api = ApiBuilder.newInstance(URL);
                    Snackbar.make(root, "Found HUB: " + device.getFriendlyName() + " press Connect on HUB", Snackbar.LENGTH_INDEFINITE)
                            .setAction("CONNECT",v -> apiCreateUser())
                            .show();
                },t -> {
                    Snackbar.make(root, "Failed to find Hue HUB on this network", Snackbar.LENGTH_INDEFINITE).show();
                });
    }

    private void findConnection() {
        new UPnPDeviceFinder().observe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    URL = String.format("%s://%s/"
                            , device.getLocation().getProtocol()
                            , device.getLocation().getHost());
                    getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", URL).commit();
                    api = ApiBuilder.newInstance(URL);
                    Snackbar.make(root, "Found HUB: " + device.getFriendlyName(), Snackbar.LENGTH_LONG)
                            .show();
                    apiCreateLightList();
                },t -> {
                    Snackbar.make(root, "Failed to find Hue HUB on this network", Snackbar.LENGTH_INDEFINITE)
                            .setAction("EXIT",v -> finish())
                            .show();
                });
    }

    private void processRegistrationResult(Response response) {
        if (response.success != null) {
            Hue.TOKEN = response.success.username;
            getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", response.success.username).commit();
            connect.setVisibility(View.GONE);
            apiCreateLightList();
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
            return fragments.get(position).getTitle();
        }
    }


}
