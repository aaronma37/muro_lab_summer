package com.example.aaron.test;

/**
 * Created by aaron on 11/2/15.
 */
public class pointXY {
    public float x=0;
    public float y=0;
    public int index1=0;
    public int index2=0;
    public int count=0;
    public boolean targeted=false;
    public boolean boundary=false;
    public int linkSize=0;
    public float angle[]= new float[5];

    public pointXY(){
        for (int i=0; i<5;i++){
            angle[i]=0;
        }

    }

    public void addLinkAngle(float linkAngle){
        angle[linkSize]= linkAngle;
        linkSize++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        pointXY pointXY = (pointXY) o;

        if (Float.compare(pointXY.x, x) != 0) return false;
        return Float.compare(pointXY.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }


}
