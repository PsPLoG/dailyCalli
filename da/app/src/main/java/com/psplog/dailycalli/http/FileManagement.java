package com.psplog.dailycalli.http;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by PsPLoG on 2015-08-29.
 */
public class FileManagement {
    public String token;
    File dirfile,loginFile;
    String path;

    public FileManagement(Context context) {
        dirfile = context.getDir("myFile", Activity.MODE_PRIVATE);
        String path = dirfile.getAbsolutePath();
        loginFile = new File(path+"/logindata.txt");
    }
    public boolean loadLoginData()
    {
        int i;
        String logindata="";
        try {
            FileInputStream fis = new FileInputStream(loginFile);
            while((i=fis.read())!=-1){
                logindata+=(char)i;
            }
            Log.e("main","uf"+logindata);
            if(logindata.trim().equals(""))return false;
        } catch (FileNotFoundException e) {
            Log.e("FileIo","로그인 정보없음");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        token = logindata;
        return true;
    }

    public void saveLoginData() {
        byte bf[];
        if(token!=null)
            bf=token.getBytes();
        else
            return;
        try {
            FileOutputStream fos = new FileOutputStream(loginFile,false);
            fos.write(bf);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 로그인데이터를 제거
    public void removeLoginData()
    {
        byte bf[];
        bf=("").getBytes();
        try {
            FileOutputStream fos = new FileOutputStream(loginFile,false);
            fos.write(bf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getToken()
    {
        return token;
    }

}

