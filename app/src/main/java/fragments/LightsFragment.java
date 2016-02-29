package fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.calber.hue.MainActivity;
import org.calber.hue.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by calber on 29/2/16.
 */
public class LightsFragment extends HueFragment  {
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
        View rootView = inflater.inflate(R.layout.lights, container, false);
        ButterKnife.bind(this, rootView);
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        return rootView;
    }
}
