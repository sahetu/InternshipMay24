package internship.may24;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class OrderDetailActivity extends AppCompatActivity {

    TextView orderId,amount,name,address,payvia;

    SharedPreferences sp;
    SQLiteDatabase db;

    RecyclerView recyclerView;

    ArrayList<OrderProductList> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

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

        orderId = findViewById(R.id.order_detail_id);
        amount = findViewById(R.id.order_detail_amount);
        name = findViewById(R.id.order_detail_name);
        address = findViewById(R.id.order_detail_address);
        payvia = findViewById(R.id.order_detail_payvia);

        String selectQuery = "SELECT * FROM TBL_ORDER WHERE ORDERID='"+sp.getString(ConstantSp.ORDER_ID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                orderId.setText("ORDER ID : "+cursor.getString(0));
                name.setText(cursor.getString(2));
                address.setText(cursor.getString(5)+","+cursor.getString(6)+"-"+cursor.getString(7));
                if(cursor.getString(8).equalsIgnoreCase("COD")){
                    payvia.setText(cursor.getString(8));
                }
                else{
                    payvia.setText(cursor.getString(8)+" ("+cursor.getString(9)+")");
                }
                amount.setText(ConstantSp.PRICE_SYMBOL+cursor.getString(10));
            }
        }

        recyclerView = findViewById(R.id.order_detail_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String orderQuery = "SELECT * FROM CART WHERE ORDERID='"+sp.getString(ConstantSp.ORDER_ID,"")+"'";
        Cursor cursor1 = db.rawQuery(orderQuery,null);
        if(cursor1.getCount()>0){
            arrayList = new ArrayList<>();
            while (cursor1.moveToNext()){
                OrderProductList list = new OrderProductList();
                list.setCartId(cursor1.getString(0));
                String productQuery = "SELECT * FROM PRODUCT WHERE PRODUCTID='"+cursor1.getString(3)+"'";
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
                list.setQty(cursor1.getString(4));
                arrayList.add(list);
            }
            OrderDetailAdapter adapter = new OrderDetailAdapter(OrderDetailActivity.this,arrayList);
            recyclerView.setAdapter(adapter);
        }

    }
}