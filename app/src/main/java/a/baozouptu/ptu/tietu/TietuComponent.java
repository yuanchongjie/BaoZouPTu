package a.baozouptu.ptu.tietu;

import dagger.Component;

/**
 * Created by LiuGuicen on 2017/2/9 0009.
 */
@Component(modules = TietuModule.class)
public interface TietuComponent {
    void inject(TietuFragment tietuFragment);
}
