package internship.may24;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText username,name,email,contact,password,confirmPassword;
    ImageView passwordHide,passwordShow,confirmPasswordHide,confirmPasswordShow;
    Button signup;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    //RadioButton male,female;
    RadioGroup gender;
    Spinner city;
    String[] cityArray = {"Select City","Ahmedabad","Vadodara","Surat","Rajkot","Gandhinagar"};

    CheckBox terms;
    String sCity = "";
    String sGender = "";

    SQLiteDatabase db;

    ApiInterface apiInterface;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        db = openOrCreateDatabase("InternshipMay24.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY,USERNAME VARCHAR(100),NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT BIGINT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(20))";
        db.execSQL(tableQuery);

        username = findViewById(R.id.signup_username);
        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        contact = findViewById(R.id.signup_contact);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);

        passwordHide = findViewById(R.id.signup_password_hidden);
        passwordShow = findViewById(R.id.signup_password_show);

        confirmPasswordShow = findViewById(R.id.signup_confirm_password_show);
        confirmPasswordHide = findViewById(R.id.signup_confirm_password_hidden);

        /*male = findViewById(R.id.signup_male);
        female = findViewById(R.id.signup_female);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(SignupActivity.this,"Male");
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(SignupActivity.this,"Female");
            }
        });*/

        gender = findViewById(R.id.signup_gender);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                sGender = radioButton.getText().toString();
                new CommonMethod(SignupActivity.this,sGender);
            }
        });

        city = findViewById(R.id.signup_city);
        ArrayAdapter adapter = new ArrayAdapter(SignupActivity.this, android.R.layout.simple_list_item_checked,cityArray);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    sCity = "";
                }
                else {
                    sCity = cityArray[i];
                    new CommonMethod(SignupActivity.this, cityArray[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        terms = findViewById(R.id.signup_terms);

        signup = findViewById(R.id.signup_button);

        passwordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordShow.setVisibility(View.GONE);
                passwordHide.setVisibility(View.VISIBLE);
                password.setTransformationMethod(null);
            }
        });

        passwordHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordShow.setVisibility(View.VISIBLE);
                passwordHide.setVisibility(View.GONE);
                password.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        confirmPasswordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmPasswordShow.setVisibility(View.GONE);
                confirmPasswordHide.setVisibility(View.VISIBLE);
                confirmPassword.setTransformationMethod(null);
            }
        });

        confirmPasswordHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmPasswordShow.setVisibility(View.VISIBLE);
                confirmPasswordHide.setVisibility(View.GONE);
                confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().trim().equals("")){
                    username.setError("Username Required");
                }
                else if(name.getText().toString().trim().equals("")){
                    name.setError("Name Required");
                }
                else if(email.getText().toString().trim().equals("")){
                    email.setError("Email Id Required");
                }
                else if(!email.getText().toString().trim().matches(emailPattern)){
                    email.setError("Valid Email Id Required");
                }
                else if(contact.getText().toString().trim().equals("")){
                    contact.setError("Contact No. Required");
                }
                else if(password.getText().toString().trim().equals("")){
                    password.setError("Password Required");
                }
                else if(password.getText().toString().trim().length()<6){
                    password.setError("Min 6 Char. Password Required");
                }
                else if(confirmPassword.getText().toString().trim().equals("")){
                    confirmPassword.setError("Confirm Password Required");
                }
                else if(confirmPassword.getText().toString().trim().length()<6){
                    confirmPassword.setError("Min 6 Char. Confirm Password Required");
                }
                else if(!password.getText().toString().trim().matches(confirmPassword.getText().toString().trim())){
                    confirmPassword.setError("Password Does Not Match");
                }
                else if(gender.getCheckedRadioButtonId()== -1){
                    new CommonMethod(SignupActivity.this,"Please Select Gender");
                }
                else if(sCity.equals("")){
                    new CommonMethod(SignupActivity.this,"Please Select City");
                }
                else if(!terms.isChecked()){
                    new CommonMethod(SignupActivity.this,"Please Accept Terms & Conditions");
                }
                else{
                    //doSqliteSignup(view);
                    if(new ConnectionDetector(SignupActivity.this).networkConnected()){
                        //new doSignup().execute();
                        pd = new ProgressDialog(SignupActivity.this);
                        pd.setMessage("Please Wait...");
                        pd.setCancelable(false);
                        pd.show();
                        doSignupRetrofit();
                    }
                    else{
                        new ConnectionDetector(SignupActivity.this).networkDisconnected();
                    }
                }
            }
        });

    }

    private void doSignupRetrofit() {
        Call<GetSignupData> call = apiInterface.getSignupData(username.getText().toString(),name.getText().toString(),email.getText().toString(),contact.getText().toString(),password.getText().toString(),sGender,sCity);
        call.enqueue(new Callback<GetSignupData>() {
            @Override
            public void onResponse(Call<GetSignupData> call, Response<GetSignupData> response) {
                pd.dismiss();
                if(response.code()==200){
                    if(response.body().status){
                        new CommonMethod(SignupActivity.this, response.body().message);
                        onBackPressed();
                    }
                    else{
                        new CommonMethod(SignupActivity.this, response.body().message);
                    }
                }
                else{
                    new CommonMethod(SignupActivity.this,"Server Error Code : "+response.code());
                }
            }

            @Override
            public void onFailure(Call<GetSignupData> call, Throwable t) {
                pd.dismiss();
                Log.d("RESPONSE_FAIL",t.getMessage());
            }
        });
    }

    private void doSqliteSignup(View view) {
        String selectQuery = "SELECT * FROM USERS WHERE USERNAME = '"+username.getText().toString()+"' OR EMAIL='"+email.getText().toString()+"' OR CONTACT='"+contact.getText().toString()+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            new CommonMethod(SignupActivity.this, "Username/Email Id/Contact No. Already Registered");
            new CommonMethod(view, "Username/Email Id/Contact No. Already Registered");
        }
        else {
            String insertQuery = "INSERT INTO USERS VALUES(NULL,'" + username.getText().toString() + "','" + name.getText().toString() + "','" + email.getText().toString() + "','" + contact.getText().toString() + "','" + password.getText().toString() + "','" + sGender + "','" + sCity + "')";
            db.execSQL(insertQuery);
            new CommonMethod(SignupActivity.this, "Signup Successfully");
            new CommonMethod(view, "Signup Successfully");
            onBackPressed();
        }
    }

    private class doSignup extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SignupActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("username",username.getText().toString());
            hashMap.put("name",name.getText().toString());
            hashMap.put("email",email.getText().toString());
            hashMap.put("contact",contact.getText().toString());
            hashMap.put("password",password.getText().toString());
            hashMap.put("gender",sGender);
            hashMap.put("city",sCity);
            return new MakeServiceCall().MakeServiceCall(ConstantSp.SIGNUP_URL,MakeServiceCall.POST,hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getBoolean("Status")){
                    new CommonMethod(SignupActivity.this, object.getString("Message"));
                    onBackPressed();
                }
                else{
                    new CommonMethod(SignupActivity.this, object.getString("Message"));
                }
            } catch (JSONException e) {
                //throw new RuntimeException(e);
                Log.d("RESPONSE_CATCH",e.getMessage().toString());
            }
        }
    }
}