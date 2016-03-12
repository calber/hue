package org.calber.hue;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static final int REQUESTPERMISSION = 1;

    @Bind(R.id.wait)
    View wait;

    @Bind(R.id.root)
    View root;

    private ArrayList<HueFragment> fragments;
    private Navigator navigator;
    private boolean canstart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        navigator = new Navigator(getSupportFragmentManager(), R.id.root);

        if(BuildConfig.BUILD_TYPE.equals("nonetwork")) {
            getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", "http://10.0.0.12/").commit();
            getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", "dee4feb2224a44f254acf32ed98dd7").commit();
        }

        Hue.TOKEN = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("TOKEN", null);
        Hue.URL = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("URL", null);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            canstart = hasPermissionInManifest(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!canstart) return;

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
                        switch (ex.code()) {
                            case 403:
                                // device rejected, re register
                                Snackbar.make(root, R.string.pressconnect, Snackbar.LENGTH_INDEFINITE).show();
                                createConnection();
                                break;
                            default:
                                // fall back, see if IP has changed
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

    /*
    register user
     */

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

    /*
    assume user is registered only scan for IP change
     */

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
                        // user is rejected, forget and restart
                        Snackbar.make(root, R.string.nohubavailable, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.scanagain, v1 -> createConnection())
                                .show();
                    });
                }, t -> {
                    // No hub found!!
                    setWait(false);
                    Snackbar.make(root, R.string.nohubavailable, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.exit, v -> finish())
                            .show();
                });
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean flag = true;
        for (int c : grantResults) {
            if (c < 0) flag = false;
        }
        canstart = flag;
        onResume();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermissionInManifest(Context context) {
        boolean flag = true;

        List<String> deniedPermission = new ArrayList<>();
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final List<String> declaredPermisisons = Arrays.asList(packageInfo.requestedPermissions);
            for (String p : declaredPermisisons) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(p);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (!deniedPermission.isEmpty()) {
            requestPermissions(deniedPermission.toArray(new String[deniedPermission.size()]), MainActivity.REQUESTPERMISSION);
            flag = false;
        }
        return flag;
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
