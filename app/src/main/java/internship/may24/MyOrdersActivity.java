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

public class MyOrdersActivity extends AppCompatActivity {

    SharedPreferences sp;
    SQLiteDatabase db;

    RecyclerView recyclerView;
    ArrayList<MyOrderList> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

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

        String orderTableQuery = "CREATE TABLE IF NOT EXISTS TBL_ORDER(ORDERID INTEGER PRIMARY KEY, USERID VARCHAR(10),NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT VARCHAR(10),ADDRESS TEXT, CITY VARCHAR(50),PINCODE BIGINT(6),PAYVIA VARCHAR(10), TRANSACTIONID VARCHAR(50),TOTALAMOUNT VARCHAR(20))";
        db.execSQL(orderTableQuery);

        recyclerView = findViewById(R.id.my_orders_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM TBL_ORDER WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' ORDER BY ORDERID DESC";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                MyOrderList list = new MyOrderList();
                list.setOrderId(cursor.getString(0));
                list.setName(cursor.getString(2));
                list.setAddress(cursor.getString(5));
                list.setCity(cursor.getString(6));
                list.setPincode(cursor.getString(7));
                list.setPayVia(cursor.getString(8));
                list.setTransactionId(cursor.getString(9));
                list.setTotalAmount(cursor.getString(10));
                arrayList.add(list);
            }
            MyOrderAdapter adapter = new MyOrderAdapter(MyOrdersActivity.this,arrayList);
            recyclerView.setAdapter(adapter);
        }

    }
}