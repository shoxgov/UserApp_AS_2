package com.qingwing.safebox.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MD5Utils {
	public static String digest(String password){
		try {
			MessageDigest digest=MessageDigest.getInstance("MD5");
			StringBuilder sb=new StringBuilder();
			byte[] bytes = digest.digest(password.getBytes());
			for(byte b:bytes){
				int result=b&0xff;//
				String hex=Integer.toHexString(result); //
				if(hex.length()<2){
					sb.append("0");
				}
				//System.out.println(hex);
				sb.append(hex);
			}
			String string = sb.toString();
			return string;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		
	}
	//endDate=2019-06-08T17:46:00
	public static String change_start(String a){
		if (TextUtils.isEmpty(a)) {
			return null; 
		}
		String f ="";
		String b ="";
		String d ="";
 		String[] c = a.split("T");
		String[] z = c[0].split("-");
		String[] h = c[1].split(":");
	for(int i =0;i<	z.length;i++){
		b+=z[i];
		 
	}
	for(int i =0;i<	h.length;i++){
		f+=h[i];
	}
	d= b.substring(2, b.length())+f;
	 return d;
	}
	public static long change_date(String date ){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date c;
		long a = 0;
		
		try {
			c = format.parse(date);
			  a = c.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return a;
	}
    public static boolean matchPhone(String phone) {
        if(TextUtils.isEmpty(phone)) {
            return false;
        } else {
            String matcher = "1[0-9]{10}";
            return phone.matches(matcher);
        }
    }
}
