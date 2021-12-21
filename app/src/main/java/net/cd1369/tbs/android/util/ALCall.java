package net.cd1369.tbs.android.util;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;

public abstract class ALCall implements AppBarLayout.OnOffsetChangedListener {
    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int totalScrollRange = appBarLayout.getTotalScrollRange();

        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED, 0F);
            }
            mCurrentState = State.EXPANDED;
        } else {
            if (Math.abs(i) >= totalScrollRange) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED, 1F);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                float progress = Math.abs(i) * 1F / totalScrollRange;
                onStateChanged(appBarLayout, State.IDLE, progress);

                mCurrentState = State.IDLE;
            }
        }
    }

    protected abstract void onStateChanged(@NonNull AppBarLayout appBarLayout, @NonNull State collapsed, @NonNull float progress);

}
