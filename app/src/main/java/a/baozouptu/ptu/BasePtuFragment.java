package a.baozouptu.ptu;

import android.app.Fragment;

/**
 * Created by LiuGuicen on 2017/2/28 0028.
 */

public abstract class BasePtuFragment extends Fragment implements BasePtuFunction {
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void clear() {
    }

}
