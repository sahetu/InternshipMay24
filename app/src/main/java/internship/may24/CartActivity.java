package internship.may24;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    public static TextView total;
    TextView checkout;

    public static RelativeLayout dataLayout,emptyLayout;

    RecyclerView recyclerView;
    ArrayList<CartList> arrayList;
    SQLiteDatabase db;
    SharedPreferences sp;

    public static int iCartTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
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

        total = findViewById(R.id.cart_total);
        checkout = findViewById(R.id.cart_checkout);

        dataLayout = findViewById(R.id.cart_data_layout);
        emptyLayout = findViewById(R.id.cart_empty_layout);

        recyclerView = findViewById(R.id.cart_recyclerview);
        //Set As List
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        //Set As a Grid
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //Set As a Scroll
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM CART WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0) {
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
                CartList list = new CartList();
                list.setCartId(cursor.getString(0));

                String productQuery = "SELECT * FROM PRODUCT WHERE PRODUCTID='"+cursor.getString(3)+"'";
                Cursor cursorProduct = db.rawQuery(productQuery,null);
                while (cursorProduct.moveToNext()){
                    list.setProductId(cursorProduct.getString(0));
                    list.setCategoryId(cursorProduct.getString(2));
                    list.setSubCategoryId(cursorProduct.getString(1));
                    list.setName(cursorProduct.getString(3));
                    list.setImage(cursorProduct.getString(4));
                    list.setPrice(cursorProduct.getString(5));
                    list.setDescription(cursorProduct.getString(6));
                    //iCartTotal = iCartTotal + Integer.parseInt(cursorProduct.getString(5))* Integer.parseInt(cursor.getString(4));
                    iCartTotal += Integer.parseInt(cursorProduct.getString(5))* Integer.parseInt(cursor.getString(4));
                }
                list.setQty(cursor.getString(4));
                arrayList.add(list);
            }
            //CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,namerArray,imageArray);
            CartAdapter adapter = new CartAdapter(CartActivity.this, arrayList,sp,db);
            recyclerView.setAdapter(adapter);
            total.setText("Total : "+ConstantSp.PRICE_SYMBOL+String.valueOf(iCartTotal));

            dataLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

        }
        else{
            //new CommonMethod(CartActivity.this,"No Any Product Added In Wishlist");
            dataLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

    }
}