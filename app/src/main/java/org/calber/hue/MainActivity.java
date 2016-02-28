package org.calber.hue;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import models.RequestUser;
import models.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.connect)
    Button connect;
    @Bind(R.id.root)
    View root;

    private Api api;
    private String TOKEN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        TOKEN = getSharedPreferences("Hue", Context.MODE_PRIVATE).getString("TOKEN", null);
        if (TOKEN == null) {
            createConnection();
        }


    }

    private void createConnection() {
        new UPnPDeviceFinder().observe().first(device -> device.getProperties().containsKey("upnp_hue-bridgeid"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    connect.setText("connect: " + device.getLocation().getHost());
                    connect.setEnabled(true);
                    api = ApiBuilder.newInstance(String.format("%s://%s/"
                            , device.getLocation().getProtocol()
                            , device.getLocation().getHost()));
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
        //getSharedPreferences("Hue", Context.MODE_PRIVATE).edit().putString("TOKEN", response.getUser().username).commit();
        if (response.success != null)
            Snackbar.make(root, response.success.username, Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(root, response.error.description, Snackbar.LENGTH_LONG).show();

    }
}
