package internship.may24;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    SQLiteDatabase db;
    SharedPreferences sp;

    ImageView imageView;
    TextView name,price,description;

    ImageView wishlist,addCart;
    boolean isWishlist = false;

    LinearLayout cartLayout;
    ImageView plus,minus;
    TextView qty;

    int iQty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        db = openOrCreateDatabase("InternshipMay24.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY,USERNAME VARCHAR(100),NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT BIGINT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(20))";
        db.execSQL(tableQuery);

        String categoryTableQuery = "CREATE TABLE IF NOT EXISTS CATEGORY(CATEGORYID INTEGER PRIMARY KEY,NAME VARCHAR(100),IMAGE VARCHAR(100))";
        db.execSQL(categoryTableQuery);

        String subCategoryTableQuery = "CREATE TABLE IF NOT EXISTS SUBCATEGORY(SUBCATEGORYID INTEGER PRIMARY KEY,CATEGORYID VARCHAR(10),NAME VARCHAR(100),IMAGE VARCHAR(100))";
        db.execSQL(subCategoryTableQuery);

        String productTableQuery = "CREATE TABLE IF NOT EXISTS PRODUCT(PRODUCTID INTEGER PRIMARY KEY,SUBCATEGORYID VARCHAR(10),CATEGORYID VARCHAR(10),NAME VARCHAR(100),IMAGE VARCHAR(100),PRICE VARCHAR(20),DESCRIPTION TEXT)";
        db.execSQL(productTableQuery);

        String wishlistTableQuery = "CREATE TABLE IF NOT EXISTS WISHLIST(WISHLISTID INTEGER PRIMARY KEY, USERID VARCHAR(10), PRODUCTID VARCHAR(10))";
        db.execSQL(wishlistTableQuery);

        String cartTableQuery = "CREATE TABLE IF NOT EXISTS CART(CARTID INTEGER PRIMARY KEY, USERID VARCHAR(10),ORDERID VARCHAR(10), PRODUCTID VARCHAR(10), QTY VARCHAR(10))";
        db.execSQL(cartTableQuery);

        imageView = findViewById(R.id.product_detail_image);
        name = findViewById(R.id.product_detail_name);
        price = findViewById(R.id.product_detail_price);
        description = findViewById(R.id.product_detail_description);

        cartLayout = findViewById(R.id.product_detail_cart_layout);
        plus = findViewById(R.id.product_detail_plus);
        qty = findViewById(R.id.product_detail_qty);
        minus = findViewById(R.id.product_detail_minus);

        addCart = findViewById(R.id.product_detail_cart);

        String cartQuery = "SELECT * FROM CART WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND PRODUCTID='"+sp.getString(ConstantSp.PRODUCT_ID,"")+"' AND ORDERID='0'";
        Cursor cartCursor = db.rawQuery(cartQuery,null);
        if(cartCursor.getCount()>0){
            while (cartCursor.moveToNext()){
                iQty = Integer.parseInt(cartCursor.getString(4));
            }
            qty.setText(String.valueOf(iQty));
            addCart.setVisibility(View.GONE);
            cartLayout.setVisibility(View.VISIBLE);
        }
        else{
            addCart.setVisibility(View.VISIBLE);
            cartLayout.setVisibility(View.GONE);
        }

        String selectQuery = "SELECT * FROM PRODUCT WHERE PRODUCTID='"+sp.getString(ConstantSp.PRODUCT_ID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                name.setText(cursor.getString(3));
                price.setText(ConstantSp.PRICE_SYMBOL+cursor.getString(5));
                description.setText(cursor.getString(6));
                imageView.setImageResource(Integer.parseInt(cursor.getString(4)));
            }
        }
        else{
            new CommonMethod(ProductDetailActivity.this,"Product Not Found");
        }


        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectCartQuery = "SELECT * FROM CART WHERE PRODUCTID='"+sp.getString(ConstantSp.PRODUCT_ID,"")+"' AND USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
                Cursor cursor1 = db.rawQuery(selectCartQuery,null);
                if(cursor1.getCount()>0){
                    new CommonMethod(ProductDetailActivity.this,"Product Already Added In Cart");
                }
                else{
                    String cartQuery = "INSERT INTO CART VALUES (NULL,'"+sp.getString(ConstantSp.USERID,"")+"','0','"+sp.getString(ConstantSp.PRODUCT_ID,"")+"','1')";
                    db.execSQL(cartQuery);
                    iQty = 1;
                    addCart.setVisibility(View.GONE);
                    cartLayout.setVisibility(View.VISIBLE);
                    qty.setText(String.valueOf(iQty));
                    new CommonMethod(ProductDetailActivity.this,"Product Added In Cart");
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty += 1;
                updateCart(iQty);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQty -= 1;
                if(iQty<=0){
                    String selectCartQuery = "SELECT * FROM CART WHERE PRODUCTID='"+sp.getString(ConstantSp.PRODUCT_ID,"")+"' AND USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
                    Cursor cursor1 = db.rawQuery(selectCartQuery,null);
                    if(cursor1.getCount()>0){
                        while (cursor1.moveToNext()) {
                            String deleteQuery = "DELETE FROM CART WHERE CARTID='" + cursor1.getString(0) + "'";
                            db.execSQL(deleteQuery);
                            iQty = 0;
                            addCart.setVisibility(View.VISIBLE);
                            cartLayout.setVisibility(View.GONE);
                        }
                    }
                }
                else {
                    updateCart(iQty);
                }
            }
        });


        wishlist = findViewById(R.id.product_detail_wishlist);

        String wishlistSelectQuery = "SELECT * FROM WISHLIST WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND PRODUCTID='"+sp.getString(ConstantSp.PRODUCT_ID,"")+"'";
        Cursor cursor1 = db.rawQuery(wishlistSelectQuery,null);
        if(cursor1.getCount()>0){
            wishlist.setImageResource(R.drawable.wishlist_fill);
            isWishlist = true;
        }
        else{
            wishlist.setImageResource(R.drawable.wishlist_empty);
            isWishlist = false;
        }

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWishlist){
                    String deleteQuery = "DELETE FROM WISHLIST WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND PRODUCTID='"+sp.getString(ConstantSp.PRODUCT_ID,"")+"'";
                    db.execSQL(deleteQuery);
                    wishlist.setImageResource(R.drawable.wishlist_empty);
                    isWishlist = false;
                }
                else {
                    String insertQuery = "INSERT INTO WISHLIST VALUES(NULL,'" + sp.getString(ConstantSp.USERID, "") + "','" + sp.getString(ConstantSp.PRODUCT_ID, "") + "')";
                    db.execSQL(insertQuery);
                    wishlist.setImageResource(R.drawable.wishlist_fill);
                    isWishlist = true;
                }
            }
        });

    }

    private void updateCart(int iQty) {
        String selectCartQuery = "SELECT * FROM CART WHERE PRODUCTID='"+sp.getString(ConstantSp.PRODUCT_ID,"")+"' AND USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
        Cursor cursor1 = db.rawQuery(selectCartQuery,null);
        if(cursor1.getCount()>0){
            while (cursor1.moveToNext()) {
                String updateQuery = "UPDATE CART SET QTY = '" + iQty + "' WHERE CARTID='" + cursor1.getString(0) + "' ";
                db.execSQL(updateQuery);
            }
        }
        qty.setText(String.valueOf(iQty));
    }
}