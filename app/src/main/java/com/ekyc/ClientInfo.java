package com.ekyc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumara on 31/10/18.
 */

public class ClientInfo {
    @SerializedName("name")
    String name;
    @SerializedName("surname")
    String surname;
    @SerializedName("nationality")
    String nationality;
    @SerializedName("dob")
    String dob;
    @SerializedName("father")
    String fatherName = "false";
    @SerializedName("mother")
    String motherName = "false";
    @SerializedName("sex")
    String sex = "false";
    @SerializedName("pan")
    String pan = "false";
    @SerializedName("passport")
    String passport = "false";
    @SerializedName("aadhar")
    String aadhar = "false";
    @SerializedName("address")
    String address = "false";
}
