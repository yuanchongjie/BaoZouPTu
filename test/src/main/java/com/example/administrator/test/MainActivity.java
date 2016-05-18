package com.example.administrator.test;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity  {

    private ContentFragment mWeixin;
    void createDialog(){
        Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置,
         * 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        dialog.setContentView(R.layout.dialog_layout);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        final Button bn=(Button)findViewById(R.id.button);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               System.out.print("asddddddddddddddddddddddddddddddddddddddddd");
                P.le("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            }
        });
        //createDialog();
    }
}

/**

 15 public class ConfirmDialog extends Dialog {
 16
 17     private Context context;
 18     private String title;
 19     private String confirmButtonText;
 20     private String cacelButtonText;
 21     private ClickListenerInterface clickListenerInterface;
 22
 23     public interface ClickListenerInterface {
 24
 25         public void doConfirm();
 26
 27         public void doCancel();
 28     }
 29
 30     public ConfirmDialog(Context context, String title, String confirmButtonText, String cacelButtonText) {
 31         super(context, R.style.MyDialog);
 32         this.context = context;
 33         this.title = title;
 34         this.confirmButtonText = confirmButtonText;
 35         this.cacelButtonText = cacelButtonText;
 36     }
 37
 38     @Override
 39     protected void onCreate(Bundle savedInstanceState) {
 41         super.onCreate(savedInstanceState);
 43         init();
 44     }
 45
 46     public void init() {
 47         LayoutInflater inflater = LayoutInflater.from(context);
 48         View view = inflater.inflate(R.layout.confirm_dialog, null);
 49         setContentView(view);
 50
 51         TextView tvTitle = (TextView) view.findViewById(R.id.title);
 52         TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
 53         TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
 54
 55         tvTitle.setText(title);
 56         tvConfirm.setText(confirmButtonText);
 57         tvCancel.setText(cacelButtonText);
 58
 59         tvConfirm.setOnClickListener(new clickListener());
 60         tvCancel.setOnClickListener(new clickListener());
 61
 62         Window dialogWindow = getWindow();
 63         WindowManager.LayoutParams lp = dialogWindow.getAttributes();
 64         DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
 65         lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
 66         dialogWindow.setAttributes(lp);
 67     }
 68
 69     public void setClicklistener(ClickListenerInterface clickListenerInterface) {
 70         this.clickListenerInterface = clickListenerInterface;
 71     }
 72
 73     private class clickListener implements View.OnClickListener {
 74         @Override
 75         public void onClick(View v) {
 76             // TODO Auto-generated method stub
 77             int id = v.getId();
 78             switch (id) {
 79             case R.id.confirm:
 80                 clickListenerInterface.doConfirm();
 81                 break;
 82             case R.id.cancel:
 83                 clickListenerInterface.doCancel();
 84                 break;
 85             }
 86         }
 87
 88     };
 89
 90 }

 复制代码

 在如上空间构造代码中，由于控件的"确认"和"取消"逻辑与实际的应用场景有关，因此，控件中通过定义内部接口来实现。



 在需要使用此控件的地方，进行如下形式调用：


 复制代码
 1 public static void Exit(final Context context) {
 2         final ConfirmDialog confirmDialog = new ConfirmDialog(context, "确定要退出吗?", "退出", "取消");
 3         confirmDialog.show();
 4         confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
 5             @Override
 6             public void doConfirm() {
 7                 // TODO Auto-generated method stub
 8                 confirmDialog.dismiss();
 9                 //toUserHome(context);
 10                 AppManager.getAppManager().AppExit(context);
 11             }
 12
 13             @Override
 14             public void doCancel() {
 15                 // TODO Auto-generated method stub
 16                 confirmDialog.dismiss();
 17             }
 18         });
 19     }
*/