package internship.may24;

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

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String[] namerArray = {"Shirts","T-Shirts","Jeans","Shorts & Trousers","Casual Shoes","Infant Essentials"};
    int[] imageArray = {R.drawable.shirts,R.drawable.tshirts,R.drawable.jeans,R.drawable.shorts,R.drawable.causual_shoes,R.drawable.infant};

    ArrayList<CategoryList> arrayList;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        db = openOrCreateDatabase("InternshipMay24.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY,USERNAME VARCHAR(100),NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT BIGINT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(20))";
        db.execSQL(tableQuery);

        String categoryTableQuery = "CREATE TABLE IF NOT EXISTS CATEGORY(CATEGORYID INTEGER PRIMARY KEY,NAME VARCHAR(100),IMAGE VARCHAR(100))";
        db.execSQL(categoryTableQuery);

        for(int i=0;i<namerArray.length;i++){
            String selectQuery = "SELECT * FROM CATEGORY WHERE NAME='"+namerArray[i]+"'";
            Cursor cursor = db.rawQuery(selectQuery,null);
            if(cursor.getCount()>0){

            }
            else {
                String insertQuery = "INSERT INTO CATEGORY VALUES(NULL,'" + namerArray[i] + "','" + imageArray[i] + "')";
                db.execSQL(insertQuery);
            }
        }

        recyclerView = findViewById(R.id.category_recyclerview);
        //Set As List
        //recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
        //Set As a Grid
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //Set As a Scroll
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM CATEGORY";
        Cursor cursor = db.rawQuery(selectQuery,null);
        arrayList = new ArrayList<>();
        while (cursor.moveToNext()){
            CategoryList list = new CategoryList();
            list.setCategoryId(cursor.getString(0));
            list.setName(cursor.getString(1));
            list.setImage(cursor.getString(2));
            arrayList.add(list);
        }
        //CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,namerArray,imageArray);
        CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,arrayList);
        recyclerView.setAdapter(adapter);

    }
}