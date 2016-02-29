package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.calber.hue.R;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import models.Whitelist;

/**
 * Created by calber on 28/2/16.
 */
public class WhiteListAdapter extends RecyclerView.Adapter<WhiteListAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Whitelist> items = new ArrayList<>();

    public WhiteListAdapter(Context context, Collection<Whitelist> items) {
        inflater = LayoutInflater.from(context);
        this.items = new ArrayList(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.devicecard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        PrettyTime t = new PrettyTime(Locale.getDefault());

        h.name.setText(items.get(position).name);
        h.created.setText(t.format(items.get(position).created));
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
