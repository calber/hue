package fragments;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import org.calber.hue.R;
import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;

/**
 * Created by calber on 29/2/16.
 */
public class HueFragment extends Fragment {

    @Nullable
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    public interface OnItemSelected {
        void onDataReady(Object object, int position);
        void onDataRemoved(Object object, int position);
    }
}
