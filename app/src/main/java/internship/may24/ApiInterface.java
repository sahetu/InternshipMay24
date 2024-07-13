package internship.may24;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("signup.php")
    Call<GetSignupData> getSignupData(
            @Field("username") String username,
            @Field("name") String name,
            @Field("email") String email,
            @Field("contact") String contact,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("city") String city
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<GetLoginData> getLoginData(@Field("email") String email,@Field("password") String password);

    @FormUrlEncoded
    @POST("updateProfile.php")
    Call<GetSignupData> updateProfileData(
            @Field("username") String username,
            @Field("name") String name,
            @Field("email") String email,
            @Field("contact") String contact,
            @Field("gender") String gender,
            @Field("city") String city,
            @Field("userId") String userId
    );

    @FormUrlEncoded
    @POST("deleteProfile.php")
    Call<GetSignupData> deleteProfileData(@Field("userId") String userId);
}