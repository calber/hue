
package fragments;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class Navigator {
    @NonNull
    protected final FragmentManager mFragmentManager;

    @IdRes
    protected final int mDefaultContainer;

    public Navigator(@NonNull final FragmentManager fragmentManager, @IdRes final int defaultContainer) {
        mFragmentManager = fragmentManager;
        mDefaultContainer = defaultContainer;
    }

    public Fragment getActiveFragment() {
        try {
            if (mFragmentManager.getBackStackEntryCount() == 0) {
                return mFragmentManager.getFragments().get(0);
            }
            String tag = mFragmentManager
                    .getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
            return mFragmentManager.findFragmentByTag(tag);
        } catch (Exception e) {
            return null;
        }
    }

    public void goTo(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                        .addToBackStack(getName(fragment))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .hide(getActiveFragment())
                        .add(mDefaultContainer, fragment, getName(fragment))
                        .commitAllowingStateLoss();
    }

    protected String getName(final Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    public void setRootFragment(final Fragment startFragment) {
        if (getSize() > 0) {
            this.clearHistory();
        }
        this.replaceFragment(startFragment);
    }

    private void replaceFragment(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                        .replace(mDefaultContainer, fragment, getName(fragment))
                        .commitAllowingStateLoss();
    }

    public void goOneBack() {
        mFragmentManager.popBackStackImmediate();
        mFragmentManager.beginTransaction()
                        .show(getActiveFragment());
    }

    public int getSize() {
        return mFragmentManager.getBackStackEntryCount();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void gotToTheRootFragmentBack() {
        for (int i = 0; i <= mFragmentManager.getBackStackEntryCount(); ++i) {
            goOneBack();
        }
    }

    public void clearHistory() {
        while (mFragmentManager.popBackStackImmediate()) ;
    }
}
