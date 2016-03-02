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

import api.ApiBuilder;
import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.FragmentInteraction;
import fragments.GroupsFragment;
import fragments.HueFragment;
import fragments.LightsFragment;
import fragments.Navigator;
import fragments.SceneFragment;
import models.AllData;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import upnp.UPnPDeviceFinder;

public class MainActivity extends AppCompatActivity implements FragmentInteraction, ViewPager.OnPageChangeListener {

    public static final String FRAGMENTTITLE = "title";

    @Bind(R.id.root)
    View root;
    @Bind(R.id.wait)
    View wait;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.container)
    ViewPager container;
    @Bind(R.id.tabs)
    TabLayout tabs;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    private ArrayList<HueFragment> fragments;
    private Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        navigator = new Navigator(getSupportFragmentManager(),R.id.root);

//        getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", "http://10.0.0.12/").commit();
//        getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", "148f61d2149f27f71a81b9ee15f51a23").commit();

        Hue.TOKEN = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("TOKEN", null);
        Hue.URL = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("URL", null);

        setWait(true);

        if (Hue.TOKEN == null) {
            createConnection();
        } else {
            apiConfigurationAll();
        }
    }

    private void apiConfigurationAll() {
        Hue.api = ApiBuilder.newInstance(Hue.URL);
        ApiController.apiAll()
                .subscribe(configuration -> {
                    loadPager(configuration);
                }, throwable -> {
                    // fall back, see if IP has changed
                    Snackbar.make(root, "Failed to connect to HUB", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SCAN AGAIN", v -> findConnection())
                            .show();
                    Log.e(Hue.TAG, "", throwable);
                });
    }

    private void loadPager(AllData configuration) {
        fragments = new ArrayList<>();
        fragments.add(LightsFragment.newInstance("LIGHTS"));
        fragments.add(GroupsFragment.newInstance("GROUPS"));
        fragments.add(SceneFragment.newInstance("SCENES"));
        //fragments.add(WhitelistFragment.newInstance("WHITELIST"));

        container.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        container.addOnPageChangeListener(this);
        tabs.setupWithViewPager(container);

        toolbar.setTitle(configuration.config.name);
        setWait(false);
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

                    Snackbar.make(root, "Found HUB: " + device.getHost() + " press Connect on HUB to start", Snackbar.LENGTH_INDEFINITE)
                            .setAction("CONNECT", v -> {
                                ApiController.apiCreateUser(this)
                                        .subscribe(configuration -> {
                                            loadPager(Hue.hueConfiguration);
                                        }, throwable -> {
                                            // fall back, see if IP has changed
                                            Snackbar.make(root, "Failed to connect to HUB", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("SCAN AGAIN", v1 -> findConnection())
                                                    .show();
                                            Log.e(Hue.TAG, "", throwable);
                                        });
                            })
                            .show();
                }, t -> {
                    Snackbar.make(root, "Failed to find Hue HUB on this network", Snackbar.LENGTH_INDEFINITE).show();
                });
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    @Override
    public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public void setWait(boolean flag) {
        if (flag) {
            container.setVisibility(View.GONE);
            wait.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.VISIBLE);
            wait.setVisibility(View.GONE);
        }
    }
}
