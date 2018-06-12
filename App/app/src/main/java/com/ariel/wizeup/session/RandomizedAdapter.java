package com.ariel.wizeup.session;

import android.support.annotation.NonNull;

import com.robinhood.spark.SparkAdapter;

import java.util.Random;

public  class RandomizedAdapter extends SparkAdapter {
    private final int[] yData;
    private final Random random;

    public RandomizedAdapter() {
        random = new Random();
        yData = new int[100];
        randomize();
    }

    private void randomize() {
        for (int i = 0, count = yData.length; i < count; i++) {
            yData[i] = random.nextInt(100);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return yData.length;
    }

    @NonNull
    @Override
    public Object getItem(int index) {
        return yData[index];
    }

    @Override
    public float getY(int index) {
        return yData[index];
    }
}