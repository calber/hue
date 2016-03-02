package org.calber.hue;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import api.ApiBuilder;
import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.FragmentInteraction;
import fragments.HueFragment;
import fragments.Navigator;
import fragments.PagerFragment;
import fragments.WhitelistFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import upnp.UPnPDeviceFinder;

public class MainActivity extends AppCompatActivity implements FragmentInteraction {

    public static final String FRAGMENTTITLE = "title";

    @Bind(R.id.root)
    View root;
    @Bind(R.id.wait)
    View wait;

    private ArrayList<HueFragment> fragments;
    private Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menudevices:
                navigator.goTo(WhitelistFragment.newInstance("WHITELIST"));
                break;
        }
        return true;
    }


    private void apiConfigurationAll() {
        Hue.api = ApiBuilder.newInstance(Hue.URL);
        ApiController.apiAll()
                .subscribe(configuration -> {
                    navigator.setRootFragment(PagerFragment.newInstance());
                }, throwable -> {
                    // fall back, see if IP has changed
                    Snackbar.make(root, "Failed to connect to HUB", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SCAN AGAIN", v -> findConnection())
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

                    Snackbar.make(root, "Found HUB: " + device.getHost() + " press Connect on HUB to start", Snackbar.LENGTH_INDEFINITE)
                            .setAction("CONNECT", v -> {
                                ApiController.apiCreateUser(this)
                                        .subscribe(configuration -> {
                                            navigator.setRootFragment(PagerFragment.newInstance());
                                            //loadPager(Hue.hueConfiguration);
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
    @Nullable
    public FloatingActionButton getFab() {
        return (FloatingActionButton) root.findViewById(R.id.fab);
    }

    @Override
    public void setWait(boolean flag) {
        if (flag) {
            wait.setVisibility(View.VISIBLE);
        } else {
            wait.setVisibility(View.GONE);
        }
    }

    @Override
    public Navigator getNavigator() {
        return navigator;
    }
}
