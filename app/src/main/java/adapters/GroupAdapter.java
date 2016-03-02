package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.calber.hue.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.HueFragment;
import models.Group;

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
        public int position;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onDataReady(items.get(position),position);
        }
    }

}
