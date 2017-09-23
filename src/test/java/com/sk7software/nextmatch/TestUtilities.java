package com.sk7software.nextmatch;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtilities {
    public static JSONObject fetchJSON(InputStream in) throws IOException, JSONException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }

        return new JSONObject(responseStrBuilder.toString());
    }

}
