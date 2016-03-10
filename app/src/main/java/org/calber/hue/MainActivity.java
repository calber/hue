package org.calber.hue;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import upnp.UPnPDeviceFinder;

public class MainActivity extends AppCompatActivity implements FragmentInteraction {

    public static final String FRAGMENTTITLE = "title";

    @Bind(R.id.wait)
    View wait;

    @Bind(R.id.root)
    View root;

    private ArrayList<HueFragment> fragments;
    private Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        navigator = new Navigator(getSupportFragmentManager(), R.id.root);

//        getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", "http://10.0.0.12/").commit();
//        getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", "148f61d2149f27f71a81b9ee15f51a23").commit();

        Hue.TOKEN = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("TOKEN", null);
        Hue.URL = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("URL", null);


        if (Hue.TOKEN == null) {
            createConnection();
        } else {
            apiConfigurationAll();
        }
    }

    private void apiConfigurationAll() {
        setWait(true);
        Hue.api = ApiBuilder.newInstance(Hue.URL);
        ApiController.apiAll().subscribe(configuration -> navigator.setRootFragment(PagerFragment.newInstance())
                , throwable -> {
                    setWait(false);
                    HttpException ex = null;
                    try {
                        ex = (HttpException) throwable;
                        // fall back, see if IP has changed
                        switch (ex.code()) {
                            case 403:
                                createConnection();
                                break;
                            default:
                                Snackbar.make(root, String.format(getString(R.string.nohubavailable), Hue.URL), Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.scan, v1 -> findConnection())
                                        .show();
                                break;
                        }
                    } catch (Exception e) {
                        Snackbar.make(root, String.format(getString(R.string.internal),e.getMessage()), Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.exit, v -> finish())
                                .show();
                    }
                });
    }


    private void createConnection() {
        setWait(true);
        new UPnPDeviceFinder().observe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    setWait(false);
                    Hue.URL = String.format("%s://%s/"
                            , device.getLocation().getProtocol()
                            , device.getLocation().getHost());
                    getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", Hue.URL).commit();
                    Hue.api = ApiBuilder.newInstance(Hue.URL);

                    Snackbar.make(root, String.format(getString(R.string.foundhub), device.getHost()), Snackbar.LENGTH_INDEFINITE).show();
                    ApiController.apiCreateUser(this)
                            .retryWhen(new ApiController.RetryWithDelay(10, 5000))
                            .subscribe(configuration -> {
                                ApiController.apiAll().subscribe(allData -> {
                                    Snackbar.make(root, R.string.connected, Snackbar.LENGTH_SHORT).show();
                                    navigator.setRootFragment(PagerFragment.newInstance());
                                }, t -> {
                                    Snackbar.make(root, R.string.failtoconnect, Snackbar.LENGTH_INDEFINITE)
                                            .setAction(R.string.exit, v1 -> finish())
                                            .show();
                                });
                            }, throwable -> {
                                Snackbar.make(root, R.string.failtoconnect, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.exit, v1 -> finish())
                                        .show();
                            });

                }, t -> {
                    setWait(false);
                    Snackbar.make(root, R.string.nohub, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.exit, v -> finish())
                            .show();

                });
    }


    public void findConnection() {
        setWait(true);
        new UPnPDeviceFinder().observe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    setWait(false);

                    Hue.URL = String.format("%s://%s/", device.getLocation().getProtocol(), device.getLocation().getHost());
                    getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", Hue.URL).commit();
                    Hue.api = ApiBuilder.newInstance(Hue.URL);

                    Snackbar.make(root, "Found HUB: " + device.getHost(), Snackbar.LENGTH_LONG).show();
                    ApiController.apiAll().subscribe(allData -> {
                        Snackbar.make(root, R.string.connected, Snackbar.LENGTH_SHORT).show();
                        navigator.setRootFragment(PagerFragment.newInstance());
                    }, t -> {
                        Snackbar.make(root, R.string.nohubavailable, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.exit, v1 -> finish())
                                .show();
                    });
                }, t -> {
                    setWait(false);
                    Snackbar.make(root, R.string.nohubavailable, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.exit, v -> finish())
                            .show();
                });
    }

    @Override
    public View getRootView() {
        return navigator.getActiveFragment().getView().findViewById(R.id.coord);
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
