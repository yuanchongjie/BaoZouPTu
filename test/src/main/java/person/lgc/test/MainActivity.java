package person.lgc.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView=(ImageView) findViewById(R.id.show_pic_file);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"x= "+imageView.getX()+" Y= "+imageView.getY(),Toast.LENGTH_LONG).show();
                imageView.setRotation((imageView.getRotation()+15)%180);
            }
        });
    }

}