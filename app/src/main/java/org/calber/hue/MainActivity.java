package org.calber.hue;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import models.AllData;
import models.Light;
import models.RequestUser;
import models.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.connect)
    FloatingActionButton connect;
    @Bind(R.id.root)
    View root;
    @Bind(R.id.list)
    RecyclerView list;

    private Api api;
    private String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));

        Hue.TOKEN = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("TOKEN", null);
        if (Hue.TOKEN == null) {
            createConnection();
        } else {
            createLightList();
        }
    }

    private void createLightList() {
        URL = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("URL", null);
        api = ApiBuilder.newInstance(URL);
        api.all(Hue.TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configuration -> {
                    loadLight(configuration);
                }, throwable -> {
                    Log.e(Hue.TAG, "", throwable);
                });
    }

    private void loadLight(AllData configuration) {
        for(String l: configuration.lights.keySet()) {
            configuration.lights.get(l).id = l;
        }

        list.setAdapter(new LightAdapter(this, configuration.lights.values()));
        Log.d(Hue.TAG, configuration.toString());
    }

    private void createConnection() {
        new UPnPDeviceFinder().observe().first(device -> device.getProperties().containsKey("upnp_hue-bridgeid"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    //bridge.setText(device.getLocation().getHost());
                    connect.setVisibility(View.VISIBLE);
                    URL = String.format("%s://%s/"
                            , device.getLocation().getProtocol()
                            , device.getLocation().getHost());
                    getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("URL", URL).commit();

                    api = ApiBuilder.newInstance(URL);
                    Snackbar.make(root, "device " + device.getHost(), Snackbar.LENGTH_LONG).show();
                });

        connect.setOnClickListener(v -> {
            api.createUser(new RequestUser("calberhue#" + Hue.androidId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> processRegistrationResult(response.get(0))
                            , throwable -> {
                                Snackbar.make(root, "failed", Snackbar.LENGTH_LONG).show();
                                Log.e(Hue.TAG, "failed", throwable);
                            });
        });
    }

    private void processRegistrationResult(Response response) {
        if (response.success != null) {
            Hue.TOKEN = response.success.username;
            getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", response.success.username).commit();
            connect.setVisibility(View.GONE);
            createLightList();
            Snackbar.make(root, response.success.username, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(root, response.error.description, Snackbar.LENGTH_LONG).show();
        }
    }
}
