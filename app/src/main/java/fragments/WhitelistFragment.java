package fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.calber.hue.Hue;
import org.calber.hue.MainActivity;
import org.calber.hue.R;

import adapters.WhiteListAdapter;
import butterknife.ButterKnife;
import models.AllData;

/**
 * Created by calber on 29/2/16.
 */
public class WhitelistFragment extends HueFragment {

    public WhitelistFragment() {
    }

    public static WhitelistFragment newInstance(String title) {
        WhitelistFragment fragment = new WhitelistFragment();
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
        loadWhiteList(Hue.hueConfiguration);
        return rootView;
    }

    private void loadWhiteList(AllData configuration) {
        for(String l: configuration.config.whitelist.keySet()) {
            configuration.config.whitelist.get(l).id = l;
        }
        list.setAdapter(new WhiteListAdapter(getContext(), configuration.config.whitelist.values()));
        Log.d(Hue.TAG, configuration.toString());
    }

}
