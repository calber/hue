package fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.calber.hue.Hue;
import org.calber.hue.MainActivity;
import org.calber.hue.R;

import adapters.ItemTouchHelperCallback;
import adapters.OnStartDragListener;
import adapters.WhiteListAdapter;
import api.ApiController;
import butterknife.ButterKnife;
import models.AllData;
import models.Whitelist;

/**
 * Created by calber on 29/2/16.
 */
public class WhitelistFragment extends HueFragment implements OnStartDragListener, HueFragment.OnItemSelected {
    private ItemTouchHelper mItemTouchHelper;

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
        for (String l : configuration.config.whitelist.keySet()) {
            configuration.config.whitelist.get(l).id = l;
        }
        WhiteListAdapter adapter = new WhiteListAdapter(this, configuration.config.whitelist.values());
        list.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(list);

        Log.d(Hue.TAG, configuration.toString());
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDataReady(Object object, int position) {

    }

    @Override
    public void onDataRemoved(Object object, int position) {
        Whitelist w = (Whitelist) object;
        Snackbar.make(listener.getRootView(), "Device removed", Snackbar.LENGTH_LONG)
                .setAction("CANCEL", v -> loadWhiteList(Hue.hueConfiguration))
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == DISMISS_EVENT_TIMEOUT)
                            ApiController.apiDeleteUser(w).subscribe(
                                    r -> {
                                        Snackbar.make(listener.getRootView(), "Device deleted", Snackbar.LENGTH_LONG).show();
                                        loadWhiteList(Hue.hueConfiguration);
                                    },
                                    t -> {
                                        Snackbar.make(listener.getRootView(), "Failed to delete", Snackbar.LENGTH_LONG).show();
                                        loadWhiteList(Hue.hueConfiguration);
                                    });
                    }
                }).show();

    }
}
