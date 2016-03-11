package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.calber.hue.R;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import fragments.HueFragment;
import fragments.WhitelistFragment;
import models.Whitelist;

/**
 * Created by calber on 28/2/16.
 */
public class WhiteListAdapter extends RecyclerView.Adapter<WhiteListAdapter.ViewHolder>  {

    private final LayoutInflater inflater;
    private final HueFragment.OnItemSelected listener;
    private List<Whitelist> items = new ArrayList<>();

    public WhiteListAdapter(WhitelistFragment fragment, Collection<Whitelist> items) {
        inflater = LayoutInflater.from(fragment.getContext());
        listener = fragment;
        this.items = new ArrayList(items);
        Collections.sort(this.items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.devicecard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        PrettyTime t = new PrettyTime(Locale.getDefault());
        h.position = position;

        h.name.setText(items.get(position).name);
        h.created.setText("Last used: " + t.format(items.get(position).lastuse));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.created)
        TextView created;

        public int position;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnLongClickListener(v -> {
                listener.onDataRemoved(items.get(position),position);
                return true;
            });
        }
    }

}
