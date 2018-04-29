package com.ariel.wizer.network;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.ariel.wizer.model.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;


public class ServerResponse {

    private View layout;

    public ServerResponse(View _layout){
        this.layout = _layout;
    }

    public View getLayout() {
        return layout;
    }

    public void setLayout(View layout) {
        this.layout = layout;
    }


    public void showSnackBarMessage(String message) {
        Snackbar.make(layout,message, Snackbar.LENGTH_SHORT).show();
    }

        public void handleError(Throwable error) {

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }


}
