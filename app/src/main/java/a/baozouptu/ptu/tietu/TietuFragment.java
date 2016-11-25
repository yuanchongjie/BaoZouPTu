package a.baozouptu.ptu.tietu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.chosePicture.ChosePictureActivity;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TietuStepData;
import a.baozouptu.ptu.view.PtuView;

/**
 * Created by Administrator on 2016/7/1.
 *
 */
public class TietuFragment extends Fragment implements BaseFunction {
    private static String TAG = "TietuFragment";
    private TietuFrameLayout tietuLayout;
    Context mContext;
    List<Integer> tietuIds = new ArrayList<>();

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerAdapter tietuAdapter;

    private LinearLayout more;

    private PtuView ptuView;

    public void setTietuLayout(TietuFrameLayout tietuLayout) {
        this.tietuLayout = tietuLayout;
        tietuLayout.setOnTietuRemoveListener(
                new TietuFrameLayout.TietuChangeListener() {
                    @Override
                    public void onTietuRemove(FloatImageView view) {
                        removeFloatImageView(view);
                    }
                });
    }

    private void loadTietuPath() {
        if (tietuIds.size() != 0) return;
        tietuIds.addAll(Arrays.asList(
                R.mipmap.biaoqing2,
                R.mipmap.biaoqing7,
                R.mipmap.biaoqing5,
                R.mipmap.biaoqing4,
                R.mipmap.biaoqing10,
                R.mipmap.biaoqing9,
                R.mipmap.meng2,
                R.mipmap.baojian,
                R.mipmap.huanggua,
                R.mipmap.kuaibo,
                R.mipmap.latiao1,
                R.mipmap.latiao2,
                R.mipmap.dog1,
                R.mipmap.dog2,
                R.mipmap.dog3,
                R.mipmap.biaoqing1,
                R.mipmap.biaoqing3,
                R.mipmap.biaoqing6,
                R.mipmap.biaoqing8,
                R.mipmap.meng1,
                R.mipmap.touxie,
                R.mipmap.xiaoqian1,
                R.mipmap.xiaoqing2,
                R.mipmap.xiongmao,
                R.mipmap.jinmao));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        loadTietuPath();
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tietu, container, false);

        tietuAdapter = new RecyclerAdapter(mContext, tietuIds);
        tietuAdapter.setOnItemClickListener(new RecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Integer data) {
                /*
//                处理设置贴图失败的情况，暂不实现
                boolean flag=false;
                while(!floatImageView.setBitmapAndInit(data)&&tietuIds.fixed_size()>0)
                {
                    flag=true;
                    int id=tietuIds.indexOf(data);
                    tietuIds.remove(data);
                    if(tietuIds.fixed_size()==0){
                        ((ViewGroup)floatImageView.getParent()).removeView(floatImageView);
                        Toast.makeText(mContext,"贴图加载失败了！",Toast.LENGTH_SHORT).show();
                        tietuAdapter.notifyDataSetChanged();
                        return;
                    }
                    else{
                        data=tietuIds.get(id%tietuIds.fixed_size());
                    }
                }
                if(flag) {
                    Toast.makeText(mContext,"贴图加载失败了！",Toast.LENGTH_SHORT).show();
                    tietuAdapter.notifyDataSetChanged();
                }*/
                addTietuById(data);

                int clickPosition = tietuIds.indexOf(data);
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (clickPosition == lastPosition) {//将下一个隐藏的item移出来
                    int[] location = new int[2];
                    view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
                    recyclerView.smoothScrollBy(view.getWidth() + location[0] + view.getWidth() - more.getLeft() + 10, 0);
                }
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_list_tietu);
        recyclerView.setBackgroundColor(Color.BLACK);
        layoutManager = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tietuAdapter);

        more = (LinearLayout) view.findViewById(R.id.function_tietu_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChosePictureActivity.class);
                intent.setAction("tietu");
                startActivityForResult(intent, 1);
            }
        });
        setOnclick();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Util.P.le(TAG);
        String path = data.getStringExtra("pic_path");
        addTietuByPath(path);
    }

    /**
     * 添加一个tietu，
     */
    private void addTietuByPath(String path) {
        Bitmap srcBitmap = TietuSizeControler.getSrcBitmap(path);
        FloatImageView floatImageView = new FloatImageView(mContext);
        floatImageView.setAdjustViewBounds(true);
        floatImageView.setImageBitmapAndPath(srcBitmap, path);
        FrameLayout.LayoutParams params = TietuSizeControler.getFeatParams(srcBitmap.getWidth(), srcBitmap.getHeight(),
                ptuView.getPicBound());
        tietuLayout.addView(floatImageView, params);
    }

    /**
     * 添加一个tietu，
     */
    private void addTietuById(Integer id) {
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), id);
        FloatImageView floatImageView = new FloatImageView(mContext);
        floatImageView.setAdjustViewBounds(true);
        floatImageView.setImageBitmapAndId(srcBitmap, id);
        FrameLayout.LayoutParams params = TietuSizeControler.getFeatParams(srcBitmap.getWidth(), srcBitmap.getHeight(),
                ptuView.getPicBound());
        tietuLayout.addView(floatImageView, params);
    }

    private void removeFloatImageView(FloatImageView view) {
        tietuLayout.removeView(view);
    }

    private void setOnclick() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        tietuAdapter.setScroll(false);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        tietuAdapter.setScroll(true);
                        break;
                }
            }
        });
    }

    @Override
    public void smallRepeal() {
        if (tietuLayout.getChildCount() > 0)
            tietuLayout.removeViewAt(tietuLayout.getChildCount() - 1);
    }

    public void smallRedo() {

    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    /**
     * 获取结果，因为会有多个贴图，所以返回的 {@link TietuStepData} 里面放的是{@link StepData}的链表
     *
     * @return {@link StepData}
     */
    @Override
    public StepData getResultData(float ratio) {
        TietuStepData tsd = new TietuStepData(PtuUtil.EDIT_TIETU);
        int count = tietuLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            FloatImageView fiv = (FloatImageView) tietuLayout.getChildAt(i);
//获取每个tietu的范围
            RectF boundRectInPic = new RectF();
            float[] temp = PtuUtil.getLocationAtPicture(fiv.getLeft() + FloatImageView.pad, fiv.getTop() + FloatImageView.pad,
                    ptuView.getSrcRect(), ptuView.getDstRect());
            boundRectInPic.left = temp[0];
            boundRectInPic.top = temp[1];

            temp = PtuUtil.getLocationAtPicture(fiv.getRight() - FloatImageView.pad, fiv.getBottom() - FloatImageView.pad,
                    ptuView.getSrcRect(), ptuView.getDstRect());
            boundRectInPic.right = temp[0];
            boundRectInPic.bottom = temp[1];
            TietuStepData.OneTietu oneTietu;

            if (fiv.getPicPath() != null)//是路径的形式
                oneTietu = new TietuStepData.OneTietu(fiv.getPicPath(), boundRectInPic, fiv.getRotation());
            else
                oneTietu = new TietuStepData.OneTietu(fiv.getPicId(), boundRectInPic, fiv.getRotation());
            tsd.addOneTietu(oneTietu);
        }
        return tsd;

    }

    @Override
    public void releaseResource() {
        int count = tietuLayout.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            ((FloatImageView) tietuLayout.getChildAt(i)).releaseResourse();
            tietuLayout.removeViewAt(i);
        }
    }

    public void setPtuView(PtuView ptuView) {
        this.ptuView = ptuView;
    }

    public void addBigStep(StepData sd) {
        TietuStepData ttsd = (TietuStepData) sd;
        Iterator<TietuStepData.OneTietu> iterator = ttsd.iterator();
        while (iterator.hasNext()) {
            TietuStepData.OneTietu oneTietu = iterator.next();
            if (oneTietu.getPicPath() != null)
                ptuView.addBitmap(TietuSizeControler.getSrcBitmap(oneTietu.getPicPath()),
                        oneTietu.getBoundRectInPic(), oneTietu.getRotateAngle());
            else {
                Util.P.le(TAG,"lastId= "+oneTietu.getPicId());
                Bitmap tietuBm = BitmapFactory.decodeResource(mContext.getResources(), oneTietu.getPicId());
                ptuView.addBitmap(tietuBm,
                        oneTietu.getBoundRectInPic(), oneTietu.getRotateAngle());
            }
        }

    }
}
