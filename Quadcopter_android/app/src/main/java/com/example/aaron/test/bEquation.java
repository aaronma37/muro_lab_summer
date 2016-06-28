package com.example.aaron.test;

/**
 * Created by aaron on 11/2/15.
 */
public class bEquation {

    public float m[]=new float[100];
    public float x[]=new float[100];
    public float y[]=new float[100];
    public float x2[]=new float[100];
    public float y2[]=new float[100];
    public boolean active[]=new boolean[100];


    public bEquation(){
        for (int i=0;i<100;i++){
            m[i]=0;
            x[i]=0;
            y[i]=0;
            x2[i]=0;
            y2[i]=0;
            active[i]=false;
        }

    }

}
