package internship.may24;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText username,password;
    Button login;
    TextView signup,forgotPassword;
    ImageView passwordHide,passwordShow;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SQLiteDatabase db;

    SharedPreferences sp;

    ApiInterface apiInterface;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        db = openOrCreateDatabase("InternshipMay24.db",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY,USERNAME VARCHAR(100),NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT BIGINT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(20))";
        db.execSQL(tableQuery);

        username = findViewById(R.id.main_username);
        password = findViewById(R.id.main_password);

        login = findViewById(R.id.main_login);
        signup = findViewById(R.id.main_signup);
        forgotPassword = findViewById(R.id.main_forgot);

        forgotPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        passwordHide = findViewById(R.id.main_password_hidden);
        passwordShow = findViewById(R.id.main_password_show);

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

        //TextView
        //ImageView
        //Layout
        //View
        //EditText
        //RadioButton
        //Checkbox

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().trim().equals("")){
                    username.setError("Username Required");
                }
                else if(!username.getText().toString().trim().matches(emailPattern)){
                    username.setError("Valid Email Id Required");
                }
                else if(password.getText().toString().trim().equals("")){
                    password.setError("Password Required");
                }
                else if(password.getText().toString().trim().length()<6){
                    password.setError("Min. 6 Char Password Required");
                }
                else{
                    //doSqliteLogin(view);
                    if(new ConnectionDetector(MainActivity.this).networkConnected()){
                        //new doLogin().execute();
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage("Please Wait...");
                        pd.setCancelable(false);
                        pd.show();
                        doLoginRetrofit();
                    }
                    else{
                        new ConnectionDetector(MainActivity.this).networkDisconnected();
                    }
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(intent);*/
                new CommonMethod(MainActivity.this,SignupActivity.class);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);*/
                new CommonMethod(MainActivity.this, ForgotPasswordActivity.class);
            }
        });

    }

    private void doLoginRetrofit() {
        Call<GetLoginData> call = apiInterface.getLoginData(username.getText().toString(),password.getText().toString());
        call.enqueue(new Callback<GetLoginData>() {
            @Override
            public void onResponse(Call<GetLoginData> call, Response<GetLoginData> response) {
                pd.dismiss();
                if(response.code()==200){
                    if(response.body().status){
                        for(int i=0;i<response.body().userData.size();i++){
                            sp.edit().putString(ConstantSp.USERID,response.body().userData.get(i).userId).commit();
                            sp.edit().putString(ConstantSp.USERNAME,response.body().userData.get(i).userName).commit();
                            sp.edit().putString(ConstantSp.NAME,response.body().userData.get(i).name).commit();
                            sp.edit().putString(ConstantSp.EMAIL,response.body().userData.get(i).email).commit();
                            sp.edit().putString(ConstantSp.CONTACT,response.body().userData.get(i).contact).commit();
                            sp.edit().putString(ConstantSp.PASSWORD,"").commit();
                            sp.edit().putString(ConstantSp.GENDER,response.body().userData.get(i).gender).commit();
                            sp.edit().putString(ConstantSp.CITY,response.body().userData.get(i).city).commit();
                        }
                        new CommonMethod(MainActivity.this,response.body().message);
                        new CommonMethod(MainActivity.this,DashboardActivity.class);
                    }
                    else{
                        new CommonMethod(MainActivity.this,response.body().message);
                    }
                }
                else{
                    new CommonMethod(MainActivity.this,"Server Error Code : "+response.code());
                }
            }

            @Override
            public void onFailure(Call<GetLoginData> call, Throwable t) {
                pd.dismiss();
                Log.d("RESPONSE_FAIL",t.getMessage());
            }
        });
    }

    private void doSqliteLogin(View view) {
        String selectQuery = "SELECT * FROM USERS WHERE (USERNAME='"+username.getText().toString()+"' OR CONTACT='"+username.getText().toString()+"' OR EMAIL='"+username.getText().toString()+"') AND PASSWORD='"+password.getText().toString()+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){

            while(cursor.moveToNext()){
                String sUserId = cursor.getString(0);
                String sUserName = cursor.getString(1);
                String sName = cursor.getString(2);
                String sEmail = cursor.getString(3);
                String sContact = cursor.getString(4);
                String sPassword = cursor.getString(5);
                String sGender = cursor.getString(6);
                String sCity = cursor.getString(7);

                sp.edit().putString(ConstantSp.USERID,sUserId).commit();
                sp.edit().putString(ConstantSp.USERNAME,sUserName).commit();
                sp.edit().putString(ConstantSp.NAME,sName).commit();
                sp.edit().putString(ConstantSp.EMAIL,sEmail).commit();
                sp.edit().putString(ConstantSp.CONTACT,sContact).commit();
                sp.edit().putString(ConstantSp.PASSWORD,sPassword).commit();
                sp.edit().putString(ConstantSp.GENDER,sGender).commit();
                sp.edit().putString(ConstantSp.CITY,sCity).commit();
            }

            System.out.println("Login Successfully");
            Log.d("RESPONSE","Login Successfully");
            //Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
            new CommonMethod(MainActivity.this, "Login Successfully");
            //Snackbar.make(view,"Login Successfully",Snackbar.LENGTH_LONG).show();
            new CommonMethod(view,"Login Successfully");
                        /*Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
                        startActivity(intent);*/
            new CommonMethod(MainActivity.this,DashboardActivity.class);
        }
        else{
            new CommonMethod(MainActivity.this,"Invalid Credential");
        }
    }

    private class doLogin extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("email",username.getText().toString());
            hashMap.put("password",password.getText().toString());
            return new MakeServiceCall().MakeServiceCall(ConstantSp.LOGIN_URL,MakeServiceCall.POST,hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getBoolean("Status")){
                    JSONArray jsonArray = object.getJSONArray("UserData");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        sp.edit().putString(ConstantSp.USERID,jsonObject.getString("userId")).commit();
                        sp.edit().putString(ConstantSp.USERNAME,jsonObject.getString("userName")).commit();
                        sp.edit().putString(ConstantSp.NAME,jsonObject.getString("name")).commit();
                        sp.edit().putString(ConstantSp.EMAIL,jsonObject.getString("email")).commit();
                        sp.edit().putString(ConstantSp.CONTACT,jsonObject.getString("contact")).commit();
                        sp.edit().putString(ConstantSp.PASSWORD,"").commit();
                        sp.edit().putString(ConstantSp.GENDER,jsonObject.getString("gender")).commit();
                        sp.edit().putString(ConstantSp.CITY,jsonObject.getString("city")).commit();
                    }
                    new CommonMethod(MainActivity.this,object.getString("Message"));
                    new CommonMethod(MainActivity.this,DashboardActivity.class);
                }
                else{
                    new CommonMethod(MainActivity.this,object.getString("Message"));
                }
            } catch (JSONException e) {
                //throw new RuntimeException(e);
                Log.d("LOGIN_CATCH",e.getMessage().toString());
            }
        }
    }
}