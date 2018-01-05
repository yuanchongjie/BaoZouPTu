package a.baozouptu.ptu.text;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import a.baozouptu.R;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.Util;
import a.baozouptu.common.view.HorizontalListView;
import a.baozouptu.network.FileDownloader;

/**
 * Created by LiuGuicen on 2017/1/20 0020.
 * 注意使用弱引用的方式持有，外部都是弱引用，不要在内部被反向向持有了
 * 注意这里contentView的监听器是持有TypefacePopWindow的，contentView被window持有，
 * <p>
 * <p>window消失之后监听器当做强引用方式回收，这时相当于TypefacePopWindow没被引用了那样回收
 * 既FunctionPopWindowBuilder还在，TypefacePopWindow相当于不存在了
 */

public class TypefacePopWindow {
    private Context acContext;
    FunctionPopWindowBuilder textPopupBuilder;
    private final FloatTextView floatTextView;

    private int lastFontId = 0;
    private ArrayList<Typeface> typefaceList;

    TypefacePopWindow(Context acContext, FunctionPopWindowBuilder textPopupBuilder, FloatTextView floatTextView) {
        this.acContext = acContext;
        this.textPopupBuilder = textPopupBuilder;
        this.floatTextView = floatTextView;
        initTypeface();
    }


    private void initTypeface() {
        if (typefaceList == null) {
            typefaceList = new ArrayList<>();
            typefaceList.add(null);
        }
        for (int i = 1; i < FileDownloader.typefaceNames.size(); i++) {
            try {
                Typeface typeface = Typeface.createFromFile(AllData.zitiDir + FileDownloader.typefaceNames.get(i));
                typefaceList.add(typeface);
            } catch (Exception e) {
                typefaceList.add(null);
                //如果是损坏的文件，删除它
                File file = new File(AllData.zitiDir + FileDownloader.typefaceNames.get(i));
                if (file.exists())
                    file.delete();
            }
        }
    }

    View createTypefacePopWindow() {

        View contentView = LayoutInflater.from(acContext).inflate(R.layout.popwindow_text_typeface, null);
        HorizontalListView horizontalListView = (HorizontalListView) contentView.findViewById(R.id.hList_text_type);

        horizontalListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return FileDownloader.typefaceChinese.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(acContext);
                textView.setTextSize(25);
                /**设置颜色*/
                if (position == lastFontId) {
                    textView.setTextColor(Util.getColor(R.color.text_checked_color));
                } else {
                    textView.setTextColor(Util.getColor(R.color.text_default_color));
                }

                //设置字体
                textView.setTag(FileDownloader.typefaceNames.get(position));
                if (position == 0) {
                    textView.setTextSize(30);//注意这里，英文字号增大了一些
                } else {
                    Typeface typeface = typefaceList.get(position);
                    if (typeface != null)
                        textView.setTypeface(typeface);
                }

//其他的属性
                textView.setGravity(Gravity.CENTER);
                textView.setText(FileDownloader.typefaceChinese.get(position));
                HorizontalListView.LayoutParams layoutParams = new HorizontalListView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER);

                return textView;
            }
        });
        horizontalListView.setOnItemClickListener(
                new HorizontalListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            textPopupBuilder.curTypeface = Typeface.MONOSPACE;
                            floatTextView.setTypeface(textPopupBuilder.curTypeface);
                            floatTextView.updateSize();
                        } else {
                            try {
                                textPopupBuilder.curTypeface = typefaceList.get(position);
                                if (typefaceList.get(position) == null) {  //如果字体不存在，就进行下载，下载完成之后更新typeface列表，以及视图
                                    FileDownloader.getInstance().downloadZiti(acContext, FileDownloader.typefaceNames.get(position)
                                            , typefaceList, (TextView) view);
                                    return;
                                } else {
                                    floatTextView.setTypeface(textPopupBuilder.curTypeface);
                                    floatTextView.updateSize();
                                }
                            } catch (Exception e) {

                            }
                        }
                        //切换颜色
                        if (lastFontId != position) {
                            ((TextView) view).setTextColor(Util.getColor(R.color.text_checked_color));
                            TextView textView = (TextView) ((HorizontalListView)
                                    view.getParent()).findViewWithTag(FileDownloader.typefaceNames.get(lastFontId));
                            textView.setTextColor(Util.getColor(R.color.text_default_color));
                            lastFontId = position;
                        }
                    }
                }
        );
        horizontalListView.setDividerWidth(Util.dp2Px(10));
        return contentView;
    }
}
