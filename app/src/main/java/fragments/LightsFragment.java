package fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.calber.hue.Hue;
import org.calber.hue.MainActivity;
import org.calber.hue.R;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.LightAdapter;
import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import models.AllData;
import models.Change;

/**
 * Created by calber on 29/2/16.
 */
public class LightsFragment extends HueFragment {
    @Bind(R.id.list)
    RecyclerView list;

    public LightsFragment() {
    }

    public static LightsFragment newInstance(String title) {
        LightsFragment fragment = new LightsFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.FRAGMENTTITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        ButterKnife.bind(this, rootView);

        listener.getFab().setOnClickListener(v ->
                ApiController.apiSeachLigths()
                        .subscribe(r -> searchingNewLights()
                                , t -> Snackbar.make(listener.getRootView(), "Failed", Snackbar.LENGTH_LONG).show()));

        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        loadLight(Hue.hueConfiguration);
        return rootView;
    }

    private void searchingNewLights() {
        listener.setWait(true);
        listener.getFab().hide();
        Snackbar.make(listener.getRootView(), "Searching for new lights", Snackbar.LENGTH_INDEFINITE)
                .setDuration(60000)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        listener.setWait(false);
                        listener.getFab().show();
                        ApiController.apiAll()
                                .subscribe(configuration -> {
                                    loadLight(configuration);
                                }, t -> Snackbar.make(listener.getRootView(), "Failed", Snackbar.LENGTH_LONG).show());
                        super.onDismissed(snackbar, event);
                    }
                })
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeEvent(Change event) {
        loadLight(Hue.hueConfiguration);
    }


    private void loadLight(AllData configuration) {
        for (String l : configuration.lights.keySet()) {
            configuration.lights.get(l).id = l;
        }
        list.setAdapter(new LightAdapter(getContext(), configuration.lights.values()));
        Log.d(Hue.TAG, configuration.toString());
    }

}
