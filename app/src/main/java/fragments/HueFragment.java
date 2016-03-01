package fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import org.calber.hue.R;

import butterknife.Bind;

/**
 * Created by calber on 29/2/16.
 */
public class HueFragment extends Fragment {

    @Bind(R.id.list)
    RecyclerView list;
    protected FragmentInteraction listener;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            listener = (FragmentInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentInteraction");
        }
    }

    public interface OnItemSelected {
        void onDataReady(Object object, int position);
        void onDataRemoved(Object object, int position);
    }
}
