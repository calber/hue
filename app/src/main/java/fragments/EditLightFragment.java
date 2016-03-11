package fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.calber.hue.R;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import models.Change;
import models.Light;

/**
 * Created by calber on 29/2/16.
 */
public class EditLightFragment extends HueFragment {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind((R.id.save))
    Button save;
    @Bind((R.id.lightname))
    EditText lightname;

    private AppCompatActivity act;
    private Light light;

    public EditLightFragment() {
    }

    public static EditLightFragment newInstance(Light light) {
        EditLightFragment fragment = new EditLightFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("light",light);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.editlight, container, false);
        ButterKnife.bind(this, rootView);

        light = (Light) this.getArguments().get("light");

        act = ((AppCompatActivity) getActivity());

        act.setSupportActionBar(toolbar);
        act.getSupportActionBar().setDisplayShowTitleEnabled(false);
        act.getSupportActionBar().setDisplayShowCustomEnabled(false);
        act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listener.setWait(false);
        toolbar.setTitle("Edit light");
        toolbar.setNavigationOnClickListener(v -> listener.getNavigator().goOneBack());

        save.setOnClickListener(v -> {});

        lightname.setText(light.name);

        return rootView;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventChange(Change event) {
    }

}
