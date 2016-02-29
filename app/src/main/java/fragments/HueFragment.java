package fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import org.calber.hue.MainActivity;
import org.calber.hue.R;

import butterknife.Bind;

/**
 * Created by calber on 29/2/16.
 */
public class HueFragment extends Fragment implements FragmentInteraction{

    @Bind(R.id.list)
    RecyclerView list;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public String getTitle() {
        return this.getArguments().getString(MainActivity.FRAGMENTTITLE);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        list.setAdapter(adapter);
    }
}
