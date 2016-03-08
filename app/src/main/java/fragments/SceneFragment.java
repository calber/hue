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

import adapters.SceneAdapter;
import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import models.AllData;
import models.Change;
import models.Scene;
import models.State;

/**
 * Created by calber on 29/2/16.
 */
public class SceneFragment extends HueFragment implements HueFragment.OnItemSelected {
    @Bind(R.id.list)
    RecyclerView list;

    public SceneFragment() {
    }

    public static SceneFragment newInstance(String title) {
        SceneFragment fragment = new SceneFragment();
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
        loadScenes(Hue.hueConfiguration);
        return rootView;

    }

    @Override
    public void onDataReady(Object object, int position) {
        Scene s = (Scene) object;

        ApiController.apiSetScene("0", new State(s,254))
                .subscribe(o -> Log.d(Hue.TAG, o.toString()), t -> Log.e(Hue.TAG, t.toString()));

    }

    @Override
    public void onDataRemoved(Object object, int position) {
    }


    private void loadScenes(AllData configuration) {
        for(String l: configuration.scenes.keySet()) {
            configuration.scenes.get(l).id = l;
        }
        list.setAdapter(new SceneAdapter(getContext(), configuration.scenes.values(),this));
        Log.d(Hue.TAG, configuration.toString());
    }

    @Subscribe
    public void onEvent(Change event) {
    }

}
