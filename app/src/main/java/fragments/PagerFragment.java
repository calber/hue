package fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.calber.hue.Hue;
import org.calber.hue.MainActivity;
import org.calber.hue.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import models.Change;

/**
 * Created by calber on 29/2/16.
 */
public class PagerFragment extends HueFragment implements ViewPager.OnPageChangeListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.pager)
    ViewPager container;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.fab)
    FloatingActionButton fab;


    private ArrayList<Fragment> fragments;
    private AppCompatActivity act;

    public PagerFragment() {
    }

    public static PagerFragment newInstance() {
        PagerFragment fragment = new PagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager, parent, false);
        ButterKnife.bind(this, rootView);

        act = ((AppCompatActivity) getActivity());

        act.setSupportActionBar(toolbar);
        act.getSupportActionBar().setDisplayShowTitleEnabled(false);
        act.getSupportActionBar().setDisplayShowCustomEnabled(false);
        toolbar.setTitle(Hue.hueConfiguration.config.name);

        fragments = new ArrayList<>();
        fragments.add(LightsFragment.newInstance("LIGHTS"));
        fragments.add(GroupsFragment.newInstance("GROUPS"));
        fragments.add(SceneFragment.newInstance("SCENES"));

        container.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));
        container.addOnPageChangeListener(this);
        tabs.setupWithViewPager(container);

        return rootView;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getArguments().getString(MainActivity.FRAGMENTTITLE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 0:
                listener.getFab().show();
                listener.getFab().setOnClickListener(v -> ApiController.apiSeachLigths()
                        .subscribe(r -> searchingNewLights()
                                , t -> Snackbar.make(listener.getRootView(), "Failed", Snackbar.LENGTH_LONG).show()));
                break;
            case 1:
                listener.getFab().hide();
                break;
            case 2:
                listener.getFab().hide();
                break;
            default:
                listener.getFab().hide();
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Subscribe
    public void onEventChange(Change event) {
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
                                .subscribe(configuration -> EventBus.getDefault().post(new Change()),
                                        t -> Snackbar.make(listener.getRootView(), "Failed", Snackbar.LENGTH_LONG).show())
                        ;
                        super.onDismissed(snackbar, event);
                    }
                })
                .show();
    }

}
