package com.cel.tcm.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String SHARED_PREF_NAME = "session";
    String SESSION_TOKEN = "TCM_TOKEN";
    String SESSION_LANGUAGE = "TCM_LANGUAGE";


    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveLanguage(String lang) {
        editor.putString(SESSION_LANGUAGE, lang);
        editor.commit();
    }

    public String getLanguage() {
        return sharedPreferences.getString(SESSION_LANGUAGE, "-1");
    }


    public void saveToken(String token) {
        editor.putString(SESSION_TOKEN, token);
        editor.commit();
    }


    public String getToken() {
        return sharedPreferences.getString(SESSION_TOKEN, "-1");
    }

    public void removeToken() {
        editor.putString(SESSION_TOKEN, "-1").commit();
    }

    /*public void saveQuestionList(List<Subjective_exam_category_response.Question> questionList, String key) {
        Gson gson = new Gson();
        String json = gson.toJson(questionList);
        editor.putString(key, json);
        editor.apply();

    }

    public List<Subjective_exam_category_response.Question> getQuestionList(String key) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<List<Subjective_exam_category_response.Question>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public <T> void saveList(String key, List<T> list) {
        Gson gson = new Gson();
        String value = gson.toJson(list);

        editor.putString(key, value);
        editor.commit();
    }

    public <T> List getList(String key, Class<T> clazz) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(json, type);
    }

    public void saveExamID(String ID) {
        editor.putString(SESSION_EXAM_ID, ID);
        editor.commit();
    }

    public String getExamID() {
        return sharedPreferences.getString(SESSION_EXAM_ID, "-1");
    }*/
}
