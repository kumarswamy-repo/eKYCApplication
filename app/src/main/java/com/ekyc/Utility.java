package com.ekyc;

import android.os.Environment;

import java.io.File;

/**
 * Created by kumara on 30/10/18.
 */

public class Utility {
    static File externalStorage = Environment.getExternalStorageDirectory();
    static String BASE_URL = "https://ekycdemo.tigerlabs.io/api/v1/";
    final static String PAN_DIR = "pan";
    final static String PASSPORT_DIR = "passport";
    final static String AADHAR_DIR = "aadhar";
    public static String PREF_NAME = "ekyc_pref";


    public static File getPanCardStorageDir(){
        File panDir = new File(externalStorage,PAN_DIR);
        if(!panDir.exists())
            panDir.mkdir();
        if(panDir.exists())
            return panDir;
        else
            return null;
    }

    public static File getPassportStorageDir(){
        File passportDir = new File(externalStorage,PASSPORT_DIR);
        if(!passportDir.exists())
            passportDir.mkdir();
        if(passportDir.exists())
            return passportDir;
        else
            return null;
    }

    public static File getAadharStorageDir(){
        File aadharDir = new File(externalStorage,AADHAR_DIR);
        if(!aadharDir.exists())
            aadharDir.mkdir();
        if(aadharDir.exists())
            return aadharDir;
        else
            return null;
    }

    public static String getPanCardSavingPath(){
        File panDir = getPanCardStorageDir();
        panDir.deleteOnExit();
        panDir = getPanCardStorageDir();
        if(panDir == null)
            return "";
        else {
            String fileName = "front.jpg";
            File panFile = new File(panDir,fileName);
            return panFile.getAbsolutePath();
        }
    }

    public static String getPanCardSelfieSavingPath(){
        File panDir = getPanCardStorageDir();
        if(panDir == null)
            return "";
        else {
            String fileName = "selfie.jpg";
            File selfieFile = new File(panDir,fileName);
            return selfieFile.getAbsolutePath();
        }
    }

    public static String getPassportFrontSavingPath(){
        File passportDir = getPassportStorageDir();
        passportDir.deleteOnExit();
        passportDir = getPassportStorageDir();
        if(passportDir == null)
            return "";
        else {
            String fileName = "front.jpg";
            File passportFile = new File(passportDir,fileName);
            return passportFile.getAbsolutePath();
        }
    }

    public static String getPassportBackSavingPath(){
        File passportDir = getPassportStorageDir();
        if(passportDir == null)
            return "";
        else {
            String fileName = "back.jpg";
            File passportFile = new File(passportDir,fileName);
            return passportFile.getAbsolutePath();
        }
    }

    public static String getPassportSelfieSavingPath(){
        File passportDir = getPassportStorageDir();
        if(passportDir == null)
            return "";
        else {
            String fileName = "selfie.jpg";
            File selfieFile = new File(passportDir,fileName);
            return selfieFile.getAbsolutePath();
        }
    }

    public static String getAadharFrontSavingPath(){
        File aadharDir = getAadharStorageDir();
        aadharDir.deleteOnExit();
        aadharDir = getAadharStorageDir();
        if(aadharDir == null)
            return "";
        else {
            String fileName = "front.jpg";
            File aadharFile = new File(aadharDir,fileName);
            return aadharFile.getAbsolutePath();
        }
    }

    public static String getAadharBackSavingPath(){
        File aadharDir = getAadharStorageDir();
        if(aadharDir == null)
            return "";
        else {
            String fileName = "back.jpg";
            File aadharFile = new File(aadharDir,fileName);
            return aadharFile.getAbsolutePath();
        }
    }

    public static String getAadharSelfieSavingPath(){
        File aadharDir = getAadharStorageDir();
        if(aadharDir == null)
            return "";
        else {
            String fileName = "selfie.jpg";
            File selfieFile = new File(aadharDir,fileName);
            return selfieFile.getAbsolutePath();
        }
    }

    public static File getPicSavedDir(int picType){
        switch (picType)
        {
            case 2:{
                return getAadharStorageDir();
            }
            case 1:{
                return getPanCardStorageDir();
            }
            case 0:{
                return getPassportStorageDir();
            }
        }
        return null;
    }

    public static String getSelfieSavingPath(int picType){
        switch (picType)
        {
            case 2:{
                return getAadharSelfieSavingPath();
            }
            case 1:{
                return getPanCardSelfieSavingPath();
            }
            case 0:{
                return getPassportSelfieSavingPath();
            }
        }
        return "";
    }
}
