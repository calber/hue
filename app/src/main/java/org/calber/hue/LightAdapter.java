package org.calber.hue;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import models.Light;
import models.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        View view = inflater.inflate(R.layout.light, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        h.name.setText(items.get(position).name);
        Light light = items.get(position);

        setLighButtonState(h, light.state.on, light.state.bri);

        h.on.setOnClickListener(v -> ApiBuilder.getInstance().lightSwitch(Hue.TOKEN, light.id, new State(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                    setLighButtonState(h,true, light.state.bri);
                    Log.d(Hue.TAG, r.toString());
                }, throwable -> {
                    Log.e(Hue.TAG, "", throwable);

                }));
        h.off.setOnClickListener(v -> ApiBuilder.getInstance().lightSwitch(Hue.TOKEN, light.id, new State(false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                    setLighButtonState(h,false, light.state.bri);
                    Log.d(Hue.TAG, r.toString());
                },throwable -> {
                    Log.e(Hue.TAG, "", throwable);
                }));
        h.half.setOnClickListener(v -> ApiBuilder.getInstance().lightSwitch(Hue.TOKEN, light.id, new State(50))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                    setLighButtonState(h,true, 128);
                    Log.d(Hue.TAG, r.toString());
                },throwable -> {
                    Log.e(Hue.TAG, "", throwable);
                }));
    }

    private void setLighButtonState(ViewHolder h, boolean state, Integer bri) {
        if(state && bri == 254) {
            h.on.setActivated(true);
            h.off.setActivated(false);
            h.half.setActivated(false);
        } else if (!state) {
            h.on.setActivated(false);
            h.off.setActivated(true);
            h.half.setActivated(false);
        } else {
            h.on.setActivated(false);
            h.off.setActivated(false);
            h.half.setActivated(true);
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
        @Bind(R.id.half)
        Button half;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
