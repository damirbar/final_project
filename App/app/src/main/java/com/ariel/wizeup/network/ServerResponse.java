package com.ariel.wizeup.network;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.ariel.wizeup.model.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;


public class ServerResponse {

    private View layout;

    public ServerResponse(View _layout) {
        this.layout = _layout;
    }

    public View getLayout() {
        return layout;
    }

    public void setLayout(View layout) {
        this.layout = layout;
    }

    public void showSnackBarMessage(String message) {
        TSnackbar snackBar = TSnackbar.make(layout, message, TSnackbar.LENGTH_LONG);
        View snackBarView = snackBar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#3e4a5b"));
        TextView textView = (TextView) snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackBar.show();
    }

    public void handleError(Throwable error) {
        Log.d("error", error.toString());
        try {
            if (error instanceof HttpException) {
                Gson gson = new GsonBuilder().setLenient().create();
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                showSnackBarMessage(response.getMessage());
            } else {
                showSnackBarMessage("Network Error.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackBarMessage("Internal Server Error.");
        }
    }

    public static void handleErrorQuiet(Throwable error) {
        Log.d("tag", error.toString());
        if (error instanceof HttpException) {
            Gson gson = new GsonBuilder().setLenient().create();
            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}