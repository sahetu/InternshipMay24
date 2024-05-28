package internship.may24;

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

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String[] namerArray = {"Shirts","T-Shirts","Jeans","Shorts & Trousers","Casual Shoes","Infant Essentials"};
    int[] imageArray = {R.drawable.shirts,R.drawable.tshirts,R.drawable.jeans,R.drawable.shorts,R.drawable.causual_shoes,R.drawable.infant};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.category_recyclerview);

        //Set As List
        //recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));

        //Set As a Grid
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        //Set As a Scroll
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this,namerArray,imageArray);
        recyclerView.setAdapter(adapter);

    }
}