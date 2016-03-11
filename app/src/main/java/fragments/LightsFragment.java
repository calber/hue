package fragments;

import android.os.Bundle;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import models.AllData;
import models.Change;
import models.Light;

/**
 * Created by calber on 29/2/16.
 */
public class LightsFragment extends HueFragment implements HueFragment.OnItemSelected {
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

        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        loadLight(Hue.hueConfiguration);
        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeEvent(Change event) {
        loadLight(Hue.hueConfiguration);
    }

    private void loadLight(AllData configuration) {
        for (String l : configuration.lights.keySet()) {
            configuration.lights.get(l).id = l;
        }
        list.setAdapter(new LightAdapter(getContext(), configuration.lights.values(), this));
        Log.d(Hue.TAG, configuration.toString());
    }

    @Override
    public void onDataReady(Object object, int position) {
        Light l = (Light) object;
        listener.getNavigator().goTo(EditLightFragment.newInstance(l));
    }

    @Override
    public void onDataRemoved(Object object, int position) {
    }
}
