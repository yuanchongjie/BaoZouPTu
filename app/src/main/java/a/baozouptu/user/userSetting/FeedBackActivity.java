package a.baozouptu.user.userSetting;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import a.baozouptu.R;
import a.baozouptu.common.BaseActivity;
import a.baozouptu.common.util.Util;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import a.baozouptu.common.util.CustomToast;

import static java.util.ResourceBundle.clearCache;

public class FeedbackActivity extends BaseActivity {
    String lastComment;
    private EditText contactEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    private void initView() {
        final EditText commentEdit = (EditText) findViewById(R.id.feedback_comment);
        contactEdit = (EditText) findViewById(R.id.feedback_contact_edit);
        Button btnCommit = (Button) findViewById(R.id.feedback_btn_commit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnCommit.setBackground(Util.getDrawable(R.drawable.rip_blue_cornor_backgound));
        }else{
            btnCommit.setBackground(Util.getDrawable(R.drawable.background_round_corner_blue));
        }
        ImageView btnReturn = (ImageView) findViewById(R.id.feedback_return_btn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.feedback_return_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCommit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Util.DoubleClick.isDoubleClick()) return;
                        if (commentEdit.getText() == null) return;
                        commitComment(commentEdit.getText().toString());
                    }
                }
        );
    }

    private void commitComment(String comment) {
//        检查
        if (comment.trim().isEmpty()) {//没有输入内容不提交
            return;
        } else if (comment.length() > 500) {//太长
            comment = comment.substring(0, 500);
        } else if (comment.equals(lastComment)) {
            CustomToast.makeText(this, "反馈已提交", Toast.LENGTH_SHORT).show();
            return;
        }
        lastComment = comment;
        final Comment commentObj = new Comment(comment);
//        附加信息
        if (contactEdit.getText() != null)
            commentObj.setContact(contactEdit.getText().toString());

//        提交
        commentObj.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    CustomToast.makeText(FeedbackActivity.this, "提交成功，感谢您的反馈！", Toast.LENGTH_SHORT).show();
                } else {
                    CustomToast.makeText(FeedbackActivity.this, "记录成功，感谢您的反馈！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
