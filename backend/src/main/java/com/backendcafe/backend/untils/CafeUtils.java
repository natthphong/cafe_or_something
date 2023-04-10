package com.backendcafe.backend.untils;

import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.models.JsonModel;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class CafeUtils {

    private CafeUtils() {

    }

    public static ResponseEntity<JsonModel> getResponseEntity(JsonModel responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<JsonModel>(responseMessage, httpStatus);
    }

    public static JsonModel stringToJson(String message, String status) {
        JsonModel json = new JsonModel();
        json.setMessage(message);
        json.setStatus(status);
        return json;
    }

    public static ResponseEntity<JsonModel> deFault() {
        return CafeUtils.getResponseEntity(stringToJson(CafeConstants.DEFAULT_ERROR, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<JsonModel> message(String message, HttpStatus status) {
        return CafeUtils.getResponseEntity(stringToJson(message, String.valueOf(status)), status);
    }

    public static String getUUID() {
        Date date = new Date();
        long time = date.getTime();

        return "BILL-" + time;
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data) {
        log.info("data {}", data);
        if (!Strings.isNullOrEmpty(data))
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {
            }.getType());

        return new HashMap<>();

    }

    public static Boolean isFileExit(String path) {
        log.info("Inside isFileExit {}", path);
        try {
            File file = new File(path);
            return (file != null && file.exists()) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
