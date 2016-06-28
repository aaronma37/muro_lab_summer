package com.example.aaron.test;

/**
 * Created by aaron on 11/4/15.
 */
public class vertexArray {
    public float x[]= new float[100];
    public float y[]= new float[100];
    public int size=0;
    public float dist[]=new float[100];
    public float errorBound=.01f;
    public float angle[]=new float[100];
    public boolean used[]=new boolean[100];


    public vertexArray(){
        for (int i = 0 ; i<100;i++){
            x[i]=0;
            y[i]=0;
            dist[i]=100;
            angle[i]=0;
            used[i]=false;
        }
    }
}
