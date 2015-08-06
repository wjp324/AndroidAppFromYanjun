package com.futcore.restaurant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Base64;

public class FutCoreDb
{
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_SETTINGS = "create table if not exists accounts (id integer primary key autoincrement, " + "username text, password text);";

    private static final String SETTINGS_TABLE = "accounts";
    private static final String DATABASE_NAME = "futcore";

    //company info
    private static final String CREATE_TABLE_COMPANYINFO = "create table if not exists company_info (id integer primary key autoincrement, company_name text, address text, url text, note text);";
    private static final String COMPANYINFO_TABLE = "company_info";

    private static final String CREATE_TABLE_PERSONINFO = "create table if not exists person_info (id integer primary key autoincrement, fname text, title text, telephone text, email text);";
    private static final String PERSONINFO_TABLE = "person_info";
    

    private SQLiteDatabase db;

    protected static final String PASSWORD_SECRET = Config.DB_SECRET;

    private Context context;

    public FutCoreDb(Context ctx)
    {
        this.context = ctx;

        try {
            db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        } catch (SQLiteException e) {
            db = null;
            return;
        }
        db.execSQL(CREATE_TABLE_SETTINGS);
        db.execSQL(CREATE_TABLE_COMPANYINFO);
        db.execSQL(CREATE_TABLE_PERSONINFO);
    }

    

    public Map<String,Object> getCompanyInfo()
    {
        Cursor c = db.query(COMPANYINFO_TABLE, new String[] {"id", "company_name", "address", "url", "note"}, null, null, null, null, null);
        Map<String, Object> thisHash = new HashMap<String, Object>();
        int numRows = c.getCount();
        if(numRows>0){
            c.moveToFirst();
            thisHash.put("company_name", c.getString(1));
            thisHash.put("address", c.getString(2));
            thisHash.put("url", c.getString(3));
            thisHash.put("note", c.getString(4));
        }
        else
            return null;
        c.close();
        return thisHash;
    }
    

    public boolean updateCompanyInfo(String companyName, String address, String url, String note)
    {
        ContentValues values = new ContentValues();

        Cursor c = db.query(COMPANYINFO_TABLE, new String[] {"id"}, null, null, null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();

        values.put("company_name", companyName);
        values.put("address", address);
        values.put("url", url);
        values.put("note", note);

        if(numRows>0){
            c.close();
            return db.update(COMPANYINFO_TABLE, values, null,null)>0;
        }
        else{
            c.close();
            return db.insert(COMPANYINFO_TABLE, null, values) > 0;
        }
    }

    public Map<String,Object> getPersonInfo()
    {
        Cursor c = db.query(PERSONINFO_TABLE, new String[] {"id", "fname", "title", "telephone", "email"}, null, null, null, null, null);
        Map<String, Object> thisHash = new HashMap<String, Object>();
        int numRows = c.getCount();
        if(numRows>0){
            c.moveToFirst();
            thisHash.put("fname", c.getString(1));
            thisHash.put("title", c.getString(2));
            thisHash.put("telephone", c.getString(3));
            thisHash.put("email", c.getString(4));
        }
        else
            return null;
        c.close();
        return thisHash;
    }

    public boolean updatePersonInfo(String fName, String title, String telephone, String email)
    {
        ContentValues values = new ContentValues();

        Cursor c = db.query(PERSONINFO_TABLE, new String[] {"id"}, null, null, null, null, null);
        int numRows = c.getCount();
        c.moveToFirst();

        values.put("fname", fName);
        values.put("title", title);
        values.put("telephone", telephone);
        values.put("email", email);

        if(numRows>0){
            c.close();
            return db.update(PERSONINFO_TABLE, values, null,null)>0;
        }
        else{
            c.close();
            return db.insert(PERSONINFO_TABLE, null, values)>0;
        }
    }
    

    public long addAccount(String username, String password)
    {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", encryptPassword(password));
        return db.insert(SETTINGS_TABLE, null, values);
    }

    public static String encryptPassword(String clearText) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    PASSWORD_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText
                    .getBytes("UTF-8")), Base64.DEFAULT);
            return encrypedPwd;
        } catch (Exception e) {
        }
        return clearText;
    }

    public static String decryptPassword(String encryptedPwd) {
        try {
            DESKeySpec keySpec = new DESKeySpec(
                    PASSWORD_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
        }
        return encryptedPwd;
    }

    public List<Map<String, Object>> getAccounts() {
        if (db == null)
            return new Vector<Map<String, Object>>();
        
        Cursor c = db.query(SETTINGS_TABLE, new String[] {"id","username","password"}, null, null, null, null, null);

        int numRows = c.getCount();
        c.moveToFirst();
        List<Map<String, Object>> accounts = new Vector<Map<String, Object>>();
        for (int i = 0; i < numRows; i++) {
            int id = c.getInt(0);
            String username = c.getString(1);
            String password = c.getString(2);
            if (!password.equals("") && id > 0) {
                Map<String, Object> thisHash = new HashMap<String, Object>();
                thisHash.put("id", id);
                thisHash.put("username", username);
                accounts.add(thisHash);
            }
            c.moveToNext();
        }
        c.close();

        //        Collections.sort(accounts, Utils.BlogNameComparator);
        return accounts;
    }

    public boolean deactivateAccounts() {

        ContentValues values = new ContentValues();
        values.put("password", "");

        boolean returnValue = db.update(SETTINGS_TABLE, values, null, null) > 0;
        return (returnValue);
    }
}

