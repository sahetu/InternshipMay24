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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String[] categoryIdArray  ={"1","1","2"};
    String[] subCategoryIdArray  ={"1","1","7"};
    String[] namerArray = {"Men Solid Casual White Shirt","Black Checked Regular Fit Casual Shirt","Men Cotton Plain Polo Neck Grey T Shirts"};
    int[] imageArray = {R.drawable.us_polo_shirt,R.drawable.uspolo_regular_fit,R.drawable.allen_tshirt};
    String[] descriptionArray = {
        "White and blue striped opaque casual shirt, has a spread collar, button placket, long regular sleeves, curved hem",
            "U S POLO ASSN BLACK CHECKED REGULAR FIT CASUAL SHIRT MEN",
            "Men Cotton Plain Polo Neck Allen Solly Grey T Shirts"
    };

    String[] priceArray = {"1999","1499","599"};

    ArrayList<ProductList> arrayList;

    SQLiteDatabase db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
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

        for(int i=0;i<namerArray.length;i++){
            String selectQuery = "SELECT * FROM PRODUCT WHERE NAME='"+namerArray[i]+"' AND SUBCATEGORYID='"+subCategoryIdArray[i]+"' AND CATEGORYID='"+categoryIdArray[i]+"'";
            Cursor cursor = db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){

            }
            else {
                String insertQuery = "INSERT INTO PRODUCT VALUES(NULL,'"+subCategoryIdArray[i]+"','"+categoryIdArray[i]+"','" + namerArray[i] + "','" + imageArray[i] + "','"+priceArray[i]+"','"+descriptionArray[i]+"')";
                db.execSQL(insertQuery);
            }
        }

        recyclerView = findViewById(R.id.product_recyclerview);
        //Set As List
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        //Set As a Grid
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //Set As a Scroll
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM PRODUCT WHERE SUBCATEGORYID='"+sp.getString(ConstantSp.SUB_CATEGORY_ID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0) {
            arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
                ProductList list = new ProductList();
                list.setId(cursor.getString(0));
                list.setCategoryId(cursor.getString(2));
                list.setSubCategoryId(cursor.getString(1));
                list.setName(cursor.getString(3));
                list.setImage(cursor.getString(4));
                list.setPrice(cursor.getString(5));
                list.setDescription(cursor.getString(6));
                arrayList.add(list);
            }
            //CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,namerArray,imageArray);
            ProductAdapter adapter = new ProductAdapter(ProductActivity.this, arrayList);
            recyclerView.setAdapter(adapter);
        }
        else{
            new CommonMethod(ProductActivity.this,"Product Not Found");
        }
    }
}