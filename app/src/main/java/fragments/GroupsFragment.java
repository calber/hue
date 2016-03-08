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

import adapters.GroupAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import models.AllData;
import models.Change;

/**
 * Created by calber on 29/2/16.
 */
public class GroupsFragment extends HueFragment implements HueFragment.OnItemSelected {
    @Bind(R.id.list)
    RecyclerView list;

    public GroupsFragment() {
    }

    public static GroupsFragment newInstance(String title) {
        GroupsFragment fragment = new GroupsFragment();
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
        loadGroups(Hue.hueConfiguration);
        return rootView;

    }

    private void loadGroups(AllData configuration) {
        for (String l : configuration.groups.keySet()) {
            configuration.groups.get(l).id = l;
        }
        list.setAdapter(new GroupAdapter(getContext(), configuration.groups.values(), this));
        Log.d(Hue.TAG, configuration.toString());
    }

    @Override
    public void onDataReady(Object object, int position) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventChange(Change event) {
        loadGroups(Hue.hueConfiguration);
    }

    @Override
    public void onDataRemoved(Object object, int position) {

    }
}
