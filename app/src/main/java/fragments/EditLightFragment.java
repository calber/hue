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
import android.widget.EditText;
import android.widget.TextView;

import org.calber.hue.R;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import api.ApiController;
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
    @Bind((R.id.delete))
    Button delete;
    @Bind((R.id.lightname))
    EditText lightname;
    @Bind((R.id.model))
    TextView model;
    @Bind((R.id.type))
    TextView type;
    @Bind((R.id.lightnamehint))
    TextInputLayout lightnamehint;

    private AppCompatActivity act;
    private Light light;

    public EditLightFragment() {
    }

    public static EditLightFragment newInstance(Light light) {
        EditLightFragment fragment = new EditLightFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("light", light);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.editlight, container, false);
        ButterKnife.bind(this, rootView);

        act = ((AppCompatActivity) getActivity());
        act.setSupportActionBar(toolbar);
        act.getSupportActionBar().setDisplayShowTitleEnabled(false);
        act.getSupportActionBar().setDisplayShowCustomEnabled(false);
        act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        light = (Light) this.getArguments().get("light");
        toolbar.setTitle("Edit light");

        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(v -> delete());
        lightname.setText(light.name);
        model.setText(light.modelid);
        type.setText(light.type);

        listener.setWait(false);
        toolbar.setNavigationOnClickListener(v -> listener.getNavigator().goOneBack());

        save.setOnClickListener(v -> checkAndSave());
        delete.setOnClickListener(v -> delete());

        lightname.setText(light.name);

        return rootView;
    }

    private void checkAndSave() {
        closeKeyboard();
        if (lightname.getText().length() == 0) {
            lightnamehint.setError("Provide a name for the light");
            return;
        } else lightnamehint.setError(null);

        light.name = lightname.getText().toString();

        ApiController.apiSetLight(light).subscribe(
                responseObjectses -> listener.getNavigator().goOneBack(),
                throwable -> Snackbar.make(listener.getRootView(), "Failed to modify group", Snackbar.LENGTH_LONG).show());

    }

    private void delete() {
        Snackbar.make(listener.getRootView(), String.format("Light %s deleted", light.name), Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> listener.getNavigator().goOneBack())
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == DISMISS_EVENT_TIMEOUT)
                            ApiController.apiDeleteLight(light.id).subscribe(
                                    r -> listener.getNavigator().goOneBack(),
                                    t -> Snackbar.make(listener.getRootView(), "Failed to delete", Snackbar.LENGTH_LONG).show()
                            );
                    }
                }).show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventChange(Change event) {
    }

}
