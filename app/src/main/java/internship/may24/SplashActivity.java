package internship.may24;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sp;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        imageView = findViewById(R.id.splash_image);

        Glide
                .with(SplashActivity.this)
                .asGif()
                .load("https://camo.githubusercontent.com/ab0f4cfe426b751c7c655e9d65d36918e3c43cde001cd9a9212515b43cc1acdc/68747470733a2f2f63646e2e6472696262626c652e636f6d2f75736572732f343338323431322f73637265656e73686f74732f31353633333237352f6d656469612f30383561303134656265626465373365356364353130633933393431663439612e676966")
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sp.getString(ConstantSp.USERID,"").equalsIgnoreCase("")){
                    new CommonMethod(SplashActivity.this,MainActivity.class);
                    finish();
                }
                else{
                    new CommonMethod(SplashActivity.this,DashboardActivity.class);
                    finish();
                }
            }
        },3000);

    }
}