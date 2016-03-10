package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.calber.hue.Hue;
import org.calber.hue.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import api.ApiController;
import butterknife.Bind;
import butterknife.ButterKnife;
import models.Light;
import rx.Subscription;

/**
 * Created by calber on 28/2/16.
 */
public class LightAdapter extends RecyclerView.Adapter<LightAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Light> items = new ArrayList<>();

    public LightAdapter(Context context, Collection<Light> items) {
        inflater = LayoutInflater.from(context);
        this.items = new ArrayList(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lightcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Light light = items.get(position);
        h.name.setText(light.name);
        h.type.setText(light.type);

        setLighButtonState(h, light.state.on, light.state.bri);
        h.bri.setProgress(light.state.bri);

        h.on.setOnClickListener(v -> lightSwitch(light,254));
        h.off.setOnClickListener(v -> lightSwitch(light,0));
        h.bri.setOnSeekBarChangeListener(new HueSeekBarChangeListener(light));
    }


    private static Subscription lightSwitch(Light light, int bri) {
        return ApiController.apiLigthSwitch(light.id, bri)
                .subscribe(r -> {
                    Log.d(Hue.TAG, r.toString());
                }, throwable -> {
                    Log.e(Hue.TAG, "", throwable);

                });
    }

    private void setLighButtonState(ViewHolder h, boolean state, Integer bri) {
        h.brilabel.setText(String.format("%.0f%%",(bri/254.0) * 100.0));
        if (bri > 250) {
            h.on.setActivated(true);
            h.off.setActivated(false);
        } else if(bri < 5) {
            h.on.setActivated(false);
            h.off.setActivated(true);
        } else {
            h.on.setActivated(false);
            h.off.setActivated(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static class HueSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private final Light light;
        int globalProgress;

        public HueSeekBarChangeListener(Light light) {
            this.light = light;
            globalProgress = 0;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            globalProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            lightSwitch(light,globalProgress);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.brilabel)
        TextView brilabel;
        @Bind(R.id.type)
        TextView type;
        @Bind(R.id.on)
        ImageButton on;
        @Bind(R.id.off)
        ImageButton off;
        @Bind(R.id.bri)
        SeekBar bri;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
