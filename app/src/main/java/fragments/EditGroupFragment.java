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
public class EditGroupFragment extends HueFragment {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ckgroup)
    ViewGroup ckgroup;
    @Bind((R.id.save))
    Button save;
    @Bind((R.id.delete))
    Button delete;
    @Bind((R.id.groupname))
    EditText groupname;
    @Bind((R.id.groupnamehint))
    TextInputLayout groupnamehint;

    private AppCompatActivity act;
    private Collection<CheckBox> cklist = new ArrayList<>();
    private Group group = new Group();

    public EditGroupFragment() {
    }

    public static EditGroupFragment newInstance(Group group) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("group", group);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.newgroup, container, false);
        ButterKnife.bind(this, rootView);

        act = ((AppCompatActivity) getActivity());
        act.setSupportActionBar(toolbar);
        act.getSupportActionBar().setDisplayShowTitleEnabled(false);
        act.getSupportActionBar().setDisplayShowCustomEnabled(false);
        act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        group = (Group) this.getArguments().get("group");
        toolbar.setTitle("Create new group");

        if (group != null) {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> delete());
            groupname.setText(group.name);
            toolbar.setTitle("Edit group");
        }

        listener.setWait(false);
        toolbar.setNavigationOnClickListener(v -> listener.getNavigator().goOneBack());

        save.setOnClickListener(v -> {
            checkAndSave();
        });

        buildCheckBox(inflater, Hue.hueConfiguration.lights, group);

        return rootView;
    }

    private void checkAndSave() {

        closeKeyboard();

        if (group == null) group = new Group();

        if (groupname.getText().length() == 0) {
            groupnamehint.setError("Provide a name for the group");
            return;
        } else groupnamehint.setError(null);

        group.name = groupname.getText().toString();
        group.lights = new ArrayList<>();
        for (CheckBox c : cklist) {
            if (c.isChecked())
                group.lights.add(c.getTag().toString());
        }

        if (group.lights.isEmpty()) {
            Snackbar.make(listener.getRootView(), "Select at least one light!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (group == null)
            ApiController.apiGroup(group).subscribe(
                    responseObjectses -> listener.getNavigator().goOneBack(),
                    throwable -> Snackbar.make(listener.getRootView(), "Failed to create group", Snackbar.LENGTH_LONG).show());
        else
            ApiController.apiSetGroup(group).subscribe(
                    responseObjectses -> listener.getNavigator().goOneBack(),
                    throwable -> Snackbar.make(listener.getRootView(), "Failed to modify group", Snackbar.LENGTH_LONG).show());
    }


    private void buildCheckBox(LayoutInflater inflater, HashMap<String, Light> lights, Group group) {
        for (Light light : lights.values()) {
            CheckBox ck = (CheckBox) inflater.inflate(R.layout.checkbox, null);
            ck.setText(light.name);
            ck.setTag(light.id);
            if (group != null && group.lights.contains(light.id))
                ck.setChecked(true);
            cklist.add(ck);
            ckgroup.addView(ck);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventChange(Change event) {
    }

    private void delete() {
        Snackbar.make(listener.getRootView(), String.format("Group %s deleted", group.name), Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> listener.getNavigator().goOneBack())
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == DISMISS_EVENT_TIMEOUT)
                            ApiController.apiDeleteGroup(group.id).subscribe(
                                    r -> listener.getNavigator().goOneBack(),
                                    t -> Snackbar.make(listener.getRootView(), "Failed to delete", Snackbar.LENGTH_LONG).show()
                            );
                    }
                }).show();
    }
}
