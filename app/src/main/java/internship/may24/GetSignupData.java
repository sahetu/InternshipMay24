package internship.may24;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSignupData {

    @SerializedName("Status")
    @Expose
    public Boolean status;
    @SerializedName("Message")
    @Expose
    public String message;

}
