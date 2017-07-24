package com.xjconvenience.vege.vege.utils;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.JsonReader;

import com.xjconvenience.vege.vege.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;


/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class TokenUtil {
    public static boolean tokenVerify(SharedPreferences prefs) {
        String token = prefs.getString(Constants.TOKEN_KEY, "");
        if (token.isEmpty()) {
            return false;
        } else {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            } else {
                byte[] decodedBytes = Base64.decode(parts[1], Base64.DEFAULT);
                try {
                    String json = new String(decodedBytes, "UTF-8");
                    JsonReader reader = new JsonReader(new StringReader(json));
                    reader.beginObject();
                    long exptimeStamp = 0;
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if ("exp".equals(name)) {
                            exptimeStamp = reader.nextLong();
                            break;
                        } else {
                            reader.skipValue();
                        }
                    }
                    if (exptimeStamp != 0) {
                        long now = System.currentTimeMillis() / 1000;
                        if (now < exptimeStamp) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        }
    }
}
