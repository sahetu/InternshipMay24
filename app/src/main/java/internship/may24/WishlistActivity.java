package internship.may24;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<WishlistList> arrayList;
    SQLiteDatabase db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

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

        recyclerView = findViewById(R.id.wishlist_recyclerview);
        //Set As List
        recyclerView.setLayoutManager(new LinearLayoutManager(WishlistActivity.this));
        //Set As a Grid
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //Set As a Scroll
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM WISHLIST WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0) {
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
                WishlistList list = new WishlistList();
                list.setWishlistId(cursor.getString(0));

                String productQuery = "SELECT * FROM PRODUCT WHERE PRODUCTID='"+cursor.getString(2)+"'";
                Cursor cursorProduct = db.rawQuery(productQuery,null);
                while (cursorProduct.moveToNext()){
                    list.setProductId(cursorProduct.getString(0));
                    list.setCategoryId(cursorProduct.getString(2));
                    list.setSubCategoryId(cursorProduct.getString(1));
                    list.setName(cursorProduct.getString(3));
                    list.setImage(cursorProduct.getString(4));
                    list.setPrice(cursorProduct.getString(5));
                    list.setDescription(cursorProduct.getString(6));
                }
                arrayList.add(list);
            }
            //CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,namerArray,imageArray);
            WishlistAdapter adapter = new WishlistAdapter(WishlistActivity.this, arrayList,sp,db);
            recyclerView.setAdapter(adapter);
        }
        else{
            new CommonMethod(WishlistActivity.this,"No Any Product Added In Wishlist");
        }

    }
}