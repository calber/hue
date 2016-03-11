package fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by calber on 29/2/16.
 */
public class HueFragment extends Fragment {

    protected FragmentInteraction listener;
    protected View rootView;

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

    protected void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    public interface OnItemSelected {
        void onDataReady(Object object, int position);
        void onDataRemoved(Object object, int position);
    }
}
