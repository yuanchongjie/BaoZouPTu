package a.baozouptu.ptu.tietu;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import a.baozouptu.ptu.tietu.onlineTietu.PriorTietuManager;
import a.baozouptu.ptu.tietu.onlineTietu.TietuRecyclerAdapter;
import a.baozouptu.ptu.tietu.onlineTietu.tietu_material;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by LiuGuicen on 2017/2/9 0009.
 */

public class TietuPresenter implements TietuContract.TietuPresenter {
    TietuContract.TietuView view;


    TietuRecyclerAdapter expressionAdapter;
    TietuRecyclerAdapter propertyAdapter;


    public TietuPresenter(TietuContract.TietuView view) {
        this.view = view;
    }

    public void setExpressionAdapter(TietuRecyclerAdapter expressionAdapter) {
        this.expressionAdapter = expressionAdapter;
    }

    public void setPropertyAdapter(TietuRecyclerAdapter propertyAdapter) {
        this.propertyAdapter = propertyAdapter;
    }

    @Override
    public void start() {

    }

    @Override
    public void prepareTietuByCategory(final String cateGory) {
        Observable
                .create(new Observable.OnSubscribe<List<tietu_material>>() {

                    @Override
                    public void call(Subscriber<? super List<tietu_material>> subscriber) {
                        PriorTietuManager.queryTietuByCategory(cateGory, subscriber);
                    }
                })
                .subscribe(new Subscriber<List<tietu_material>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
//                        CustomToast.makeText("网络出错，不能获取贴图", Toast.LENGTH_SHORT).show();
                        if (cateGory == tietu_material.CATEGORY_EXPRESSION) {
                            expressionAdapter.setTietuMaterials(new ArrayList<tietu_material>());
                            view.showExpressionList();
                        } else if (cateGory == tietu_material.CATEGORY_PROPERTY) {
                            propertyAdapter.setTietuMaterials(new ArrayList<tietu_material>());
                            view.showPropertyList();
                        }
                    }

                    @Override
                    public void onNext(List<tietu_material> tietuMaterials) {
                        Log.e("TAG", "onNext: 获取到的贴图数量" + tietuMaterials.size());

                        if (cateGory == tietu_material.CATEGORY_EXPRESSION) {
                            expressionAdapter.setTietuMaterials(tietuMaterials);
                            view.showExpressionList();
                        } else if (cateGory == tietu_material.CATEGORY_PROPERTY) {
                            propertyAdapter.setTietuMaterials(tietuMaterials);
                            view.showPropertyList();
                        }
                    }
                })
        ;
    }

}
