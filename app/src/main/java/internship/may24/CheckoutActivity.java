package internship.may24;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    EditText name, email, contact, address, pincode;
    Button checkout;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    //RadioButton male,female;
    RadioGroup payVia;
    Spinner city;
    String[] cityArray = {"Select City", "Ahmedabad", "Vadodara", "Surat", "Rajkot", "Gandhinagar"};

    String sCity = "";
    String sPayVia = "";

    SQLiteDatabase db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

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


        name = findViewById(R.id.checkout_name);
        email = findViewById(R.id.checkout_email);
        contact = findViewById(R.id.checkout_contact);
        address = findViewById(R.id.checkout_address);
        pincode = findViewById(R.id.checkout_pincode);

        name.setText(sp.getString(ConstantSp.NAME,""));
        email.setText(sp.getString(ConstantSp.EMAIL,""));
        contact.setText(sp.getString(ConstantSp.CONTACT,""));

        payVia = findViewById(R.id.checkout_payvia);
        payVia.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                sPayVia = radioButton.getText().toString();
                new CommonMethod(CheckoutActivity.this, sPayVia);
            }
        });

        city = findViewById(R.id.checkout_city);
        ArrayAdapter adapter = new ArrayAdapter(CheckoutActivity.this, android.R.layout.simple_list_item_checked, cityArray);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    sCity = "";
                } else {
                    sCity = cityArray[i];
                    new CommonMethod(CheckoutActivity.this, cityArray[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sCity = sp.getString(ConstantSp.CITY,"");
        int iCityPosition = 0;
        for(int i=0;i<cityArray.length;i++){
            if(cityArray[i].equalsIgnoreCase(sCity)){
                iCityPosition=i;
                break;
            }
        }
        city.setSelection(iCityPosition);

        checkout = findViewById(R.id.checkout_button);
        checkout.setText("Pay Now : "+ConstantSp.PRICE_SYMBOL+sp.getString(ConstantSp.TOTAL_AMOUNT,""));
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().equals("")) {
                    name.setError("Name Required");
                } else if (email.getText().toString().trim().equals("")) {
                    email.setError("Email Id Required");
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                } else if (contact.getText().toString().trim().equals("")) {
                    contact.setError("Contact No. Required");
                } else if (address.getText().toString().trim().equals("")) {
                    address.setError("Address Required");
                } else if (pincode.getText().toString().trim().equals("")) {
                    pincode.setError("Pincode Required");
                } else if (pincode.getText().toString().trim().length() < 6) {
                    pincode.setError("6 Char. Pincode Required");
                } else if (sCity.equals("")) {
                    new CommonMethod(CheckoutActivity.this, "Please Select City");
                } else if (payVia.getCheckedRadioButtonId() == -1) {
                    new CommonMethod(CheckoutActivity.this, "Please Select Gender");
                } else {
                    if(sPayVia.equalsIgnoreCase("COD")) {
                        String insertQuery = "INSERT INTO TBL_ORDER VALUES(NULL,'" + sp.getString(ConstantSp.USERID, "") + "','" + name.getText().toString() + "','" + email.getText().toString() + "','" + contact.getText().toString() + "','" + address.getText().toString() + "','" + sCity + "','" + pincode.getText().toString() + "','" + sPayVia + "','','" + sp.getString(ConstantSp.TOTAL_AMOUNT, "") + "')";
                        db.execSQL(insertQuery);

                        String selectQuery = "SELECT MAX(ORDERID) FROM TBL_ORDER LIMIT 1";
                        Cursor cursor = db.rawQuery(selectQuery,null);
                        if(cursor.getCount()>0){
                            while (cursor.moveToNext()){
                                String updateCartQuery = "UPDATE CART SET ORDERID='"+cursor.getString(0)+"' WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
                                db.execSQL(updateCartQuery);
                            }
                        }

                        new CommonMethod(CheckoutActivity.this, "Order Placed Successfully");
                        new CommonMethod(view, "Order Placed Successfully");
                        new CommonMethod(CheckoutActivity.this, DashboardActivity.class);
                        finish();
                    }
                    else{
                        startPayment();
                    }
                }
            }
        });

        //startPayment();

    }

    private void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        checkout.setKeyID("rzp_test_xsiOz9lYtWKHgF");

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put("name", getResources().getString(R.string.app_name));
            options.put("description", "Reference No. #123456");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            options.put("image", R.drawable.icon);
            //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            //options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(Integer.parseInt(sp.getString(ConstantSp.TOTAL_AMOUNT,""))*100));//pass amount in currency subunits
            options.put("prefill.email", sp.getString(ConstantSp.EMAIL,""));
            options.put("prefill.contact",sp.getString(ConstantSp.CONTACT,""));
            JSONObject retryObj = new JSONObject();
            //retryObj.put("enabled", true);
            //retryObj.put("max_count", 4);
            //options.put("retry", retryObj);
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("RESPONSE_PAYMENT_CATCH", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Log.d("RESPONSE_PAYMENT_SUCCESS",s);
        String insertQuery = "INSERT INTO TBL_ORDER VALUES(NULL,'" + sp.getString(ConstantSp.USERID, "") + "','" + name.getText().toString() + "','" + email.getText().toString() + "','" + contact.getText().toString() + "','" + address.getText().toString() + "','" + sCity + "','" + pincode.getText().toString() + "','" + sPayVia + "','"+s+"','" + sp.getString(ConstantSp.TOTAL_AMOUNT, "") + "')";
        db.execSQL(insertQuery);

        String selectQuery = "SELECT MAX(ORDERID) FROM TBL_ORDER LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                String updateCartQuery = "UPDATE CART SET ORDERID='"+cursor.getString(0)+"' WHERE USERID='"+sp.getString(ConstantSp.USERID,"")+"' AND ORDERID='0'";
                db.execSQL(updateCartQuery);
            }
        }
        new CommonMethod(CheckoutActivity.this, "Order Placed Successfully");
        new CommonMethod(CheckoutActivity.this, DashboardActivity.class);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d("RESPONSE_PAYMENT_ERROR",s);
    }
}