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
import fragments.HueFragment;
import models.Group;
import models.Light;
import models.State;

/**
 * Created by calber on 28/2/16.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final HueFragment.OnItemSelected listener;
    private List<Group> items = new ArrayList<>();

    public GroupAdapter(Context context, Collection<Group> items, HueFragment.OnItemSelected listener) {
        inflater = LayoutInflater.from(context);
        this.items = new ArrayList(items);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.groupcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        h.name.setText(items.get(position).name);
        h.lights.setText(String.format("%d lights",items.get(position).lights.size()));
        h.position = position;

        Group group = items.get(position);
        Light light = Hue.hueConfiguration.lights.get(group.lights.get(0));
        h.bri.setProgress(light.state.bri);

        h.on.setOnClickListener(v -> setScene(group, 254));
        h.off.setOnClickListener(v -> setScene(group, 0));
        h.bri.setOnSeekBarChangeListener(new HueSeekBarChangeListener(items.get(position)));

    }

    private static void setScene(Group s, int bri) {
        Log.d(Hue.TAG, "State set:" + s.name);
        ApiController.apiSetScene(s.id, new State(bri))
                .subscribe(o -> Log.d(Hue.TAG, o.toString()), t -> Log.e(Hue.TAG, t.toString()));

    }


    private static class HueSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private final Group group;
        int globalProgress;

        public HueSeekBarChangeListener(Group group) {
            this.group = group;
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
            setScene(group,globalProgress);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.lights)
        TextView lights;
        @Bind(R.id.on)
        Button on;
        @Bind(R.id.off)
        Button off;
        @Bind(R.id.bri)
        SeekBar bri;

        public int position;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

}
