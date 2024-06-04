package internship.may24;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    TextView name;
    SharedPreferences sp;

    Button profile,logout,category,wishlist,cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        name = findViewById(R.id.dashboard_name);

        name.setText("Welcome "+sp.getString(ConstantSp.NAME,""));

        profile = findViewById(R.id.dashboard_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(DashboardActivity.this, ProfileActivity.class);
            }
        });

        logout = findViewById(R.id.dashboard_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle("Logout!");
                builder.setMessage("Are You Sure Want To Logout!");
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //sp.edit().remove(ConstantSp.USERID).commit();
                        sp.edit().clear().commit();
                        new CommonMethod(DashboardActivity.this,MainActivity.class);
                        finish();
                    }
                });
                builder.show();
            }
        });

        category = findViewById(R.id.dashboard_category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(DashboardActivity.this, CategoryActivity.class);
            }
        });

        wishlist = findViewById(R.id.dashboard_wishlist);
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(DashboardActivity.this, WishlistActivity.class);
            }
        });

        cart = findViewById(R.id.dashboard_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(DashboardActivity.this, CartActivity.class);
            }
        });

    }
}