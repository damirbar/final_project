package com.ariel.wizeup.session;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.robinhood.spark.SparkAdapter;

import java.util.Random;

public  class RandomizedAdapter extends SparkAdapter {
    private final int[] yData;
    private final Random random;

    public RandomizedAdapter() {
        random = new Random();
        yData = new int[50];
        randomize();
    }

    private void randomize() {
        for (int i = 0, count = 10; i < count; i++) {
            yData[i] = random.nextInt(100);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return yData.length;
    }


    @Override
    public RectF getDataBounds() {
        RectF bounds = super.getDataBounds();
        // will 'zoom in' to the middle portion of the graph
        bounds.inset(bounds.width() / 4, bounds.height() / 4);
        return bounds;
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