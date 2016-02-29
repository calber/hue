package fragments;

import android.support.v7.widget.RecyclerView;

/**
 * Created by calber on 29/2/16.
 */
public interface FragmentInteraction {
    String getTitle();
    void setAdapter(RecyclerView.Adapter adapter);
}
