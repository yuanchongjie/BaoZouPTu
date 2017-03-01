package a.baozouptu.ptu.tietu;

import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

import javax.inject.Named;
import javax.inject.Singleton;

import a.baozouptu.R;
import a.baozouptu.ptu.tietu.onlineTietu.TietuRecyclerAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by LiuGuicen on 2017/2/9 0009.
 */
@Module
public class TietuModule {
    TietuPresenter tietuPresenter;
    TietuRecyclerAdapter expressionAdapter;
    TietuRecyclerAdapter propertyAdapter;

    public TietuModule(TietuFragment tietuFragment) {
        expressionAdapter = new TietuRecyclerAdapter(tietuFragment.getActivity());
        expressionAdapter.setTietuIds(Arrays.asList(
                R.mipmap.expression1,
                R.mipmap.expression2,
                R.mipmap.expression3_laotou,
                R.mipmap.expression4
        ));
        propertyAdapter = new TietuRecyclerAdapter(tietuFragment.getActivity());
        propertyAdapter.setTietuIds(Arrays.asList(
                R.mipmap.property1,
                R.mipmap.property2,
                R.mipmap.property3,
                R.mipmap.property_jinmao

        ));

        tietuPresenter = new TietuPresenter(tietuFragment);
        tietuPresenter.setExpressionAdapter(expressionAdapter);
        tietuPresenter.setPropertyAdapter(propertyAdapter);
    }

    @Provides
    TietuContract.TietuPresenter providesTietuPresenter() {
        return tietuPresenter;
    }

    @Provides
    @Named("expression")
    TietuRecyclerAdapter provideExpressionAdapter() {
        return expressionAdapter;
    }

    @Provides
    @Named("property")
    TietuRecyclerAdapter providePropertyAdapter() {
        return propertyAdapter;
    }
}
