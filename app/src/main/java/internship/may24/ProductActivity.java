package internship.may24;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

    String[] categoryIdArray  ={"1","1","2","5","5","5","5","5","5"};
    String[] subCategoryIdArray  ={"1","1","7","10","10","10","10","10","10"};
    String[] namerArray = {"Men Solid Casual White Shirt","Black Checked Regular Fit Casual Shirt","Men Cotton Plain Polo Neck Grey T Shirts","Men Revolution 6 Running Shoes","Men Surface Grip Walking Shoes","Men Quest Road Running Shoes","Men Wisefoma Running Shoes","Men Scorch Runner V2","Unisex Badminton Indoor Shoes"};
    int[] imageArray = {R.drawable.us_polo_shirt,R.drawable.uspolo_regular_fit,R.drawable.allen_tshirt,R.drawable.nike_1,R.drawable.red_tape,R.drawable.nike_2,R.drawable.adidas,R.drawable.puma_1,R.drawable.puma_2};
    String[] descriptionArray = {
        "White and blue striped opaque casual shirt, has a spread collar, button placket, long regular sleeves, curved hem",
            "U S POLO ASSN BLACK CHECKED REGULAR FIT CASUAL SHIRT MEN",
            "Men Cotton Plain Polo Neck Allen Solly Grey T Shirts","Men Revolution 6 Running Shoes","Men Surface Grip Walking Shoes","Men Quest Road Running Shoes","Men Wisefoma Running Shoes","Men Scorch Runner V2","Unisex Badminton Indoor Shoes"
    };

    String[] priceArray = {"1999","1499","599","1799","1529","1799","2399","1849","1999"};

    String[] brandArray = {"U.S.Polo","U.S.Polo","Allen Solly","NIKE","Red Tape","Nike","ADIDAS","Puma","Puma"};

    ArrayList<ProductList> arrayList;

    SQLiteDatabase db;
    SharedPreferences sp;

    Spinner spinner;
    String[] brandsArray = {"All","U.S.Polo","Allen Solly","NIKE","Red Tape","ADIDAS","Puma"};

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

        String productTableQuery = "CREATE TABLE IF NOT EXISTS PRODUCT(PRODUCTID INTEGER PRIMARY KEY,SUBCATEGORYID VARCHAR(10),CATEGORYID VARCHAR(10),NAME VARCHAR(100),IMAGE VARCHAR(100),PRICE VARCHAR(20),DESCRIPTION TEXT,BRAND VARCHAR(20))";
        db.execSQL(productTableQuery);

        String wishlistTableQuery = "CREATE TABLE IF NOT EXISTS WISHLIST(WISHLISTID INTEGER PRIMARY KEY, USERID VARCHAR(10), PRODUCTID VARCHAR(10))";
        db.execSQL(wishlistTableQuery);

        String cartTableQuery = "CREATE TABLE IF NOT EXISTS CART(CARTID INTEGER PRIMARY KEY, USERID VARCHAR(10),ORDERID VARCHAR(10), PRODUCTID VARCHAR(10), QTY VARCHAR(10))";
        db.execSQL(cartTableQuery);

        for(int i=0;i<namerArray.length;i++){
            String selectQuery = "SELECT * FROM PRODUCT WHERE NAME='"+namerArray[i]+"' AND SUBCATEGORYID='"+subCategoryIdArray[i]+"' AND CATEGORYID='"+categoryIdArray[i]+"'";
            Cursor cursor = db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){

            }
            else {
                String insertQuery = "INSERT INTO PRODUCT VALUES(NULL,'"+subCategoryIdArray[i]+"','"+categoryIdArray[i]+"','" + namerArray[i] + "','" + imageArray[i] + "','"+priceArray[i]+"','"+descriptionArray[i]+"','"+brandArray[i]+"')";
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

        spinner = findViewById(R.id.product_brand);
        ArrayAdapter brandAdapter = new ArrayAdapter(ProductActivity.this, android.R.layout.simple_list_item_1,brandsArray);
        spinner.setAdapter(brandAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    setData("");
                }
                else{
                    setData(brandsArray[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setData(String sBrand) {
        String selectQuery;
        if(sBrand.equalsIgnoreCase("")) {
            selectQuery = "SELECT * FROM PRODUCT WHERE SUBCATEGORYID='" + sp.getString(ConstantSp.SUB_CATEGORY_ID, "") + "'";
        }
        else{
            selectQuery = "SELECT * FROM PRODUCT WHERE SUBCATEGORYID='" + sp.getString(ConstantSp.SUB_CATEGORY_ID, "") + "' AND BRAND LIKE '%"+sBrand+"%'";
        }
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
                list.setBrand(cursor.getString(7));

                String wishlistSelectQuery = "SELECT * FROM WISHLIST WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND PRODUCTID='"+cursor.getString(0)+"'";
                Cursor cursor1 = db.rawQuery(wishlistSelectQuery,null);
                if(cursor1.getCount()>0){
                    list.setWishlist(true);
                }
                else{
                    list.setWishlist(false);
                }

                arrayList.add(list);
            }
            //CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,namerArray,imageArray);
            ProductAdapter adapter = new ProductAdapter(ProductActivity.this, arrayList,db);
            recyclerView.setAdapter(adapter);
        }
        else{
            new CommonMethod(ProductActivity.this,"Product Not Found");
        }
    }
}