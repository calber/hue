package fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.calber.hue.Hue;
import org.calber.hue.MainActivity;
import org.calber.hue.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.ItemTouchHelperCallback;
import adapters.OnStartDragListener;
import adapters.WhiteListAdapter;
import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import models.AllData;
import models.Change;
import models.Whitelist;

/**
 * Created by calber on 29/2/16.
 */
public class WhitelistFragment extends HueFragment implements OnStartDragListener, HueFragment.OnItemSelected {
    @Bind(R.id.toolbar)
    Toolbar toolbar;


    private ItemTouchHelper mItemTouchHelper;
    private AppCompatActivity act;

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
        View rootView = inflater.inflate(R.layout.devicelist, container, false);
        ButterKnife.bind(this, rootView);

        act = ((AppCompatActivity) getActivity());

        act.setSupportActionBar(toolbar);
        act.getSupportActionBar().setDisplayShowTitleEnabled(false);
        act.getSupportActionBar().setDisplayShowCustomEnabled(false);
        act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(Hue.hueConfiguration.config.name);

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
        toolbar.setNavigationOnClickListener(v -> listener.getNavigator().goOneBack());

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
                                        EventBus.getDefault().post(new Change());
                                    },
                                    t -> {
                                        Snackbar.make(listener.getRootView(), "Failed to delete", Snackbar.LENGTH_LONG).show();
                                        EventBus.getDefault().post(new Change());
                                    });
                    }
                }).show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeEvent(Change event) {
        loadWhiteList(Hue.hueConfiguration);
    }


}
