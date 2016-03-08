package fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.calber.hue.Hue;
import org.calber.hue.R;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import models.Change;
import models.Group;
import models.Light;

/**
 * Created by calber on 29/2/16.
 */
public class NewGroupFragment extends HueFragment {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ckgroup)
    ViewGroup ckgroup;
    @Bind((R.id.save))
    Button save;
    @Bind((R.id.groupname))
    EditText groupname;
    @Bind((R.id.groupnamehint))
    TextInputLayout groupnamehint;

    private AppCompatActivity act;
    private Collection<CheckBox> cklist = new ArrayList<>() ;

    public NewGroupFragment() {
    }

    public static NewGroupFragment newInstance() {
        NewGroupFragment fragment = new NewGroupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.newgroup, container, false);
        ButterKnife.bind(this, rootView);

        act = ((AppCompatActivity) getActivity());

        act.setSupportActionBar(toolbar);
        act.getSupportActionBar().setDisplayShowTitleEnabled(false);
        act.getSupportActionBar().setDisplayShowCustomEnabled(false);
        act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listener.setWait(false);
        toolbar.setTitle("Create new group");

        save.setOnClickListener(v -> {
            if(groupname.getText().length() == 0) {
                groupnamehint.setError("Provide a name for the group");
                return;
            } else groupnamehint.setError(null);

            Group g = new Group();
            g.name =  groupname.getText().toString();
            for(CheckBox c: cklist) {
                if(c.isChecked())
                    g.lights.add(c.getTag().toString());
            }

            if(g.lights.isEmpty()) {
                Snackbar.make(listener.getRootView(), "Select at least one light!", Snackbar.LENGTH_SHORT).show();
                return;
            }

            ApiController.apiGroup(g).subscribe(responseObjectses -> listener.getNavigator().goOneBack(),
                    throwable -> Snackbar.make(listener.getRootView(),"Failed to create group",Snackbar.LENGTH_LONG).show());
        });

        buildCheckBox(inflater,Hue.hueConfiguration.lights);

        return rootView;
    }


    private void buildCheckBox(LayoutInflater inflater, HashMap<String, Light> lights) {
        for (Light light : lights.values()) {
            CheckBox ck = (CheckBox) inflater.inflate(R.layout.checkbox,null);
            ck.setText(light.name);
            ck.setTag(light.id);
            cklist.add(ck);
            ckgroup.addView(ck);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventChange(Change event) {
    }

}
