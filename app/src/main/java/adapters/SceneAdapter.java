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
import models.Scene;

/**
 * Created by calber on 28/2/16.
 */
public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Scene> items = new ArrayList<>();

    public SceneAdapter(Context context, Collection<Scene> items) {
        inflater = LayoutInflater.from(context);
        this.items = new ArrayList(items);
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
        Scene group = items.get(position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.lights)
        TextView lights;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
