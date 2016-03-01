package fragments;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * Created by calber on 29/2/16.
 */
public interface FragmentInteraction {
    void findConnection();
    View getRootView();
    FloatingActionButton getFab();
    void setWait(boolean flag);
}
