package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        h.name.setText(items.get(position).name);
        Light light = items.get(position);

        setLighButtonState(h, light.state.on, light.state.bri);
        h.bri.setProgress(light.state.bri);

        h.on.setOnClickListener(v -> ApiController.apiLigthSwitch(light.id, 254)
                .subscribe(r -> {
                    Log.d(Hue.TAG, r.toString());
                }, throwable -> {
                    Log.e(Hue.TAG, "", throwable);

                }));
        h.off.setOnClickListener(v -> ApiController.apiLigthSwitch(light.id, 0)
                .subscribe(r -> {
                    Log.d(Hue.TAG, r.toString());
                }, throwable -> {
                    Log.e(Hue.TAG, "", throwable);
                }));
        h.bri.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int globalProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                globalProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ApiController.apiLigthSwitch(light.id, globalProgress).subscribe(r -> {
                    Log.d(Hue.TAG, r.toString());
                }, throwable -> {
                    Log.e(Hue.TAG, "", throwable);
                });
            }
        });
    }

    private void setLighButtonState(ViewHolder h, boolean state, Integer bri) {
        if (state) {
            h.on.setActivated(true);
            h.off.setActivated(false);
        } else {
            h.on.setActivated(false);
            h.off.setActivated(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.on)
        Button on;
        @Bind(R.id.off)
        Button off;
        @Bind(R.id.bri)
        SeekBar bri;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
