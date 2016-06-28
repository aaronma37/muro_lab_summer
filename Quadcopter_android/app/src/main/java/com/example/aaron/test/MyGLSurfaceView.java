package com.example.aaron.test;
/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.app.Activity;
        import com.example.aaron.simplevoronoi.src.main.java.be.humphreys.simplevoronoi.*;
        import android.content.Context;
        import android.graphics.Point;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.wifi.WifiInfo;
        import android.net.wifi.WifiManager;
        import android.opengl.GLSurfaceView;
        import android.util.DisplayMetrics;
        import android.view.Display;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.WindowManager;
        import android.os.Vibrator;

        import org.apache.http.HttpResponse;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.params.BasicHttpParams;
        import org.apache.http.params.HttpConnectionParams;
        import org.apache.http.params.HttpParams;

        import java.math.BigDecimal;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.concurrent.ScheduledThreadPoolExecutor;
        import java.util.concurrent.TimeUnit;


/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    public final MyGLRenderer mRenderer;
    public float poseData[];
    final int maxBots=50;
    public turtle tList[]=new turtle[maxBots];
    public turtle obsticle = new turtle();
    public boolean newObstacle=false;
    private float width1;
    private float height1;
    private float pX=0;
    private float mapLeft,mapTop,mapBottom,workspace;
    private float pY=0;
    private Vibrator v;
    public int vFlag=0;
    public boolean addTurtleFlag=false;
    public boolean deleteTurtleFlag=false;
    public boolean freeBoundaryFlag=false;
    public boolean addObstacleFlag=false;
    public boolean closedBoundaryFlag=false;
    public boolean formation_flag=false;
    public int deleteTurtleNumber=0;
    public int addedTurtleNumber=0;
    public float addedTurtleX=0;
    public float addedTurtleY=0;
    public float addedTurtleZ=0;
    public int dummyFlag=0;
    private int connectable=0;
    private float firstPointFreeDraw[] ={-1000,-1000};
    private float tempTurtleList[]= new float[9];
    public int fFlag=0;
    public int gFlag = 0;
    public int gFlag2 = 0;
    public int gpFlag=0;
    public int gaussianPathArrayIteration=0;
    public float deltaX=0;
    public float deltaY=0;
    private int count=0;
    public boolean clearObstacles=false;
    private int i;
    public int pFlag=0;
    private boolean recievedTurtles=false;
    public boolean newAction=false;
    public int pFlag2=0;
    public boolean pathPublisherFlag=false;
    public int antispam=0;
    private Context context1;

    private int freeDrawCount=0;
    private int gaussDrawCount=0;
    private float vorCoords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f }; // top right
    private double cd;
    private double cy;





    String ssid = null;



    ArrayList<Double> temp= new ArrayList<Double>();
    ArrayList<Double> temp2= new ArrayList<Double>();

    private int gInd = 0;
    private float gaussScale;


    public Voronoi vor;
    private List<GraphEdge> voronoiEdges;
    private int state[]=new int[maxBots];
    public MyGLSurfaceView(Context context, float f[], turtle turtleList[]) {
        super(context);
        context1=context;
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vor = new Voronoi(.001);
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width1 = metrics.widthPixels;
        height1 = metrics.heightPixels;
        mapLeft=(width1-100)/height1;
        mapTop=(height1-5)/height1;
        mapBottom=-(height1-5)/height1;

        poseData=f;
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        for (int i=0;i<maxBots;i++){
            tList[i]=new turtle();
            state[i]=0;
        }

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context,f, tList,width1,height1);
        //float posTemp[]=f;
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }

    public void updateRen(turtle[] t){
        for (int i=0;i<maxBots;i++) {
                tempTurtleList=t[i].getData();
                tempTurtleList[5]=state[i];
                tList[i].setData(tempTurtleList, t[i].getIdentification(), t[i].getType(),t[i].measured);
        }
        mRenderer.updateRen(tList);
    }

    public float getHeight1(){
        return height1;
    }

    public void tick(){
        antispam=antispam+1;
       count=0;



        WifiInfo wifiInfo =getWifi(context1);
        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());

        mRenderer.textListSINFO.get(1).setText("Strength: " + wifiInfo.getRssi());
        if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
            mRenderer.textListSINFO.get(2).setText("Network: " + wifiInfo.getSSID());
        }

        if (mRenderer.voronoiDeploymentToggle.active==true) {
            mRenderer.textListSINFO.get(3).setText("Deployment: Multi-agent Voronoi");
        }
        else if (pFlag==1) {
            mRenderer.textListSINFO.get(3).setText("Go to Goal");
        }

        for (int i=0;i<50;i++){
            if (tList[i].getState()==1){
                count++;
                if (count>1){
                    mRenderer.textListARINFO.get(0).setText("Multiple Robots Selected");
                    mRenderer.textListARINFO.get(2).setText(" X:");
                    mRenderer.textListARINFO.get(3).setText(" Y:");
                    mRenderer.textListARINFO.get(4).setText(" Z:");
                }
                else{
                    mRenderer.textListARINFO.get(0).setText(tList[i].getIdentification());
                    mRenderer.textListARINFO.get(2).setText(" X:" + truncateDecimal(tList[i].getX(),3));
                    mRenderer.textListARINFO.get(3).setText(" Y:" + truncateDecimal(tList[i].getY(),3));
                    mRenderer.textListARINFO.get(4).setText(" Z:" + truncateDecimal(tList[i].getZ(),3));
                }
            }
        }

        if (count==0){
            mRenderer.textListARINFO.get(0).setText("No Robots Selected");
            mRenderer.textListARINFO.get(2).setText(" X:");
            mRenderer.textListARINFO.get(3).setText(" Y:");
            mRenderer.textListARINFO.get(4).setText(" Z:");
        }

        mRenderer.getUniqueVertices();
        mRenderer.toMapPoints();
        mRenderer.calculateAdjacencyMatrix();
        mRenderer.calculateAdjacencyMatrixGoal();
        if ( !mRenderer.obstacleMapPoints.isEmpty()){
            mRenderer.Dijkstra();
        }

    }


    public float getWidth1(){
        return width1;
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private float previousx = 0;
    private float previousy = 0;
    public void setR(float f[]){mRenderer.setPosition(f);}

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        int cc=0;

/*
        float xGL=(width1/2-x)/(float)(height1/1.85);
        float yGL=( height1/2+30-y)/(float)(height1/1.85);
*/

        float xGL=(width1/2-x)/(float)(height1/2);
        float yGL=( height1/2-y)/(float)(height1/2);


        workspace=-(width1-115)/(height1*2)+.15f-mRenderer.slider;


        mRenderer.tempFun(xGL, yGL);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mRenderer.tToggle=1;

                for (int i=0;i<maxBots;i++){
                    if(tList[i].getOn()==1) {
                        cc=cc+1;
                        if (Math.abs(tList[i].getX()*getScale()+mRenderer.dragX*mRenderer.scale - xGL) < .1f && Math.abs(tList[i].getY()*getScale()+mRenderer.dragY*mRenderer.scale  - yGL) < .1f) {
                            if (state[i] == 0) {
                                state[i]=1;
                            } else {
                                state[i]=0;
                            }
                            v.vibrate(50);
                        }
                    }
                }



                if (antispam>1) {

                    if (xGL< .85 && xGL>.65 && yGL > -.95 && yGL < -.75 ){
                        mRenderer.scale=mRenderer.scale+.5f;
                        mRenderer.textList.get(1).setText("Scale: "+truncateDecimal(mRenderer.scale,1)+"x");
                        mRenderer.textList.get(2).setText(truncateDecimal(2 / mRenderer.scale, 2) + " ft");
                        v.vibrate(75);
                        break;
                    }
                    if (xGL< 1.2f && xGL>.95f && yGL > -.85f && yGL < -.75 && mRenderer.scale>.5f){
                        mRenderer.scale=mRenderer.scale-.5f;
                        mRenderer.textList.get(1).setText("Scale: "+truncateDecimal(mRenderer.scale,1)+"x");
                        mRenderer.textList.get(2).setText(truncateDecimal(2 / mRenderer.scale, 2) + " ft");
                        v.vibrate(75);
                        break;
                    }

                    if (mRenderer.commit.blocked==false && xGL<mRenderer.commit.left- mRenderer.slider&& xGL>mRenderer.commit.right-mRenderer.slider&& yGL > mRenderer.commit.down && yGL < mRenderer.commit.up)
                        if (mRenderer.commit.active==true) {
                            mRenderer.commit.active=false;
                            newAction=true;
                            v.vibrate(50);
                        } else {
                            mRenderer.commit.active=true;
                            if (mRenderer.customFormation.active){
                                mRenderer.justifyRelativePositions();
                                mRenderer.resetScaleAndRotation();
                                mRenderer.formation.send=true;
                            }
                            newAction=true;
                            v.vibrate(50);
                        }


                    //Turn on voronoi toggle
                    if (buttonPush(xGL,yGL,mRenderer.vorToggle))
                    if (mRenderer.getvToggle() == 1) {
                        mRenderer.setvToggle(0);
                        v.vibrate(500);
                    } else {
                        mRenderer.setvToggle(1);
                        v.vibrate(500);
                    }
                    vFlag = mRenderer.getvToggle();


                    //Turn on Free Draw Toggle
                    if (buttonPush(xGL,yGL,mRenderer.freeDrawToggle))
                        if (mRenderer.getfToggle() == 1) {
                            mRenderer.setfToggle(0);
                            fFlag = mRenderer.getfToggle();
                        } else {
                            mRenderer.setfToggle(1);
                            mRenderer.eraseFreeLine();
                            freeDrawCount=0;
                            previousy=0;
                            previousx=0;
                            connectable=0;
                            firstPointFreeDraw[0]=-1000;
                            firstPointFreeDraw[1]=-1000;
                            fFlag = mRenderer.getfToggle();
                        }

                    //Toggable swarm for Path Follower
                    /*if (mRenderer.getfToggle()==1 && xGL<mRenderer.swarmToggle.left- mRenderer.slider&& xGL>mRenderer.swarmToggle.right-mRenderer.slider&& yGL > mRenderer.swarmToggle.down && yGL < mRenderer.swarmToggle.up)
                        if (mRenderer.swarmToggle.active == true) {
                            mRenderer.swarmToggle.active =  false;
                            v.vibrate(50);
                        } else {
                            mRenderer.swarmToggle.active =  true;
                            v.vibrate(50);
                        }
                    */  //THIS FEATURE CAN BE ADDED LATER IF WE WANT

                    //Turn on Way Point Toggle
                    if (buttonPush(xGL,yGL,mRenderer.wayPointToggle))
                        if (mRenderer.getpToggle() == 1) {
                            mRenderer.setpToggle(0);
                            pFlag2=0;
                            mRenderer.setpToggle2(pFlag2);
                            mRenderer.tToggle=1;
                            pX=0; pY=0;
                        } else {
                            mRenderer.tToggle=0;
                            mRenderer.setpToggle(1);
                            mRenderer.voronoiDeploymentToggle.active=false;
                            pX=0; pY=0;
                        }
                    pFlag = mRenderer.getpToggle();

                    //Turn on AndroneToggle
                    if (buttonPush(xGL,yGL,mRenderer.ardronePrefToggle))
                        if (mRenderer.getAPToggle() == 1) {
                            mRenderer.setAPToggle(0);
                            mRenderer.SINFO_FLAG=true;
                        } else {
                            mRenderer.setAPToggle(1);
                            mRenderer.SINFO_FLAG=false;
                        }

                    if (buttonPush(xGL,yGL,mRenderer.gaussianTrackToggle)){
                        if (mRenderer.gaussianTrackToggle.active==true){
                            mRenderer.gaussianTrackToggle.active=false;

                        }
                        else {
                            mRenderer.gaussianTrackToggle.active=true;

                        }
                        v.vibrate(50);
                    }


                    if (buttonPush(xGL,yGL,mRenderer.formation)){
                        if (mRenderer.formation.active==true){
                            mRenderer.formation.active=false;
                            mRenderer.formation.send=false;
                        }
                        else {
                            mRenderer.formation.active=true;
                            mRenderer.formation.send=true;
                            mRenderer.refreshFormationtext();
                        }
                    }

                    if (buttonPush(xGL,yGL,mRenderer.formationToggleTransform)){
                        if (mRenderer.formationToggleTransform.active==true){
                            mRenderer.formationToggleTransform.active=false;
                        }
                        else {
                            mRenderer.formationToggleTransform.active=true;
                            mRenderer.customFormation.active=false;
                            mRenderer.commit.blocked=true;
                            mRenderer.refreshFormationtext();
                        }
                    }

                    if (buttonPush(xGL,yGL,mRenderer.customFormation)){
                        if (mRenderer.customFormation.active==true){
                            mRenderer.customFormation.active=false;
                            mRenderer.commit.blocked=true;
                        }
                        else {
                            mRenderer.customFormation.active=true;
                            mRenderer.resetFormation();
                            mRenderer.refreshCenter();
                            mRenderer.formationToggleTransform.active=false;
                            mRenderer.commit.blocked=false;
                            mRenderer.commit.active=false;
                            mRenderer.refreshFormationtext();
                        }
                    }

                    if (buttonPush(xGL,yGL,mRenderer.ardroneAddToggle))
                    {
                        dummyFlag=1;
                    }

                    if (buttonPush(xGL,yGL,mRenderer.addToggle))
                    {
                        mRenderer.formation.val1=mRenderer.formation.val1+1;
                        mRenderer.refreshFormationtext();
                        mRenderer.formation.send=true;
                    }

                    if (buttonPush(xGL,yGL,mRenderer.subtractToggle))
                    {
                        mRenderer.formation.val1=mRenderer.formation.val1-1;
                        mRenderer.refreshFormationtext();
                        mRenderer.formation.send=true;
                    }



                    if (buttonPush(xGL,yGL,mRenderer.freeBoundarytoggle))
                    {
                        if (mRenderer.freeBoundarytoggle.active==true){
                            mRenderer.freeBoundarytoggle.active=false;
                            freeBoundaryFlag=true;
                            closedBoundaryFlag=false;
                        }
                        else {
                            mRenderer.freeBoundarytoggle.active=true;
                            closedBoundaryFlag=true;
                            freeBoundaryFlag=false;
                        }
                    }

                    if (buttonPush(xGL,yGL,mRenderer.obstacleLineToggle))
                    {
                        System.out.println("obstacle Line Pushed" );

                        addObstacleFlag=true;
                        mRenderer.makeObstacle();
                        mRenderer.fToggle=0;
                        fFlag= mRenderer.fToggle;
                    }

                    //IF GAUSS TOGGLE SELECTED
                    if (buttonPush(xGL,yGL,mRenderer.gaussToggle))
                        if (mRenderer.getgToggle() == 1) {
                            mRenderer.setgToggle(0);
                            gFlag2 = 0;
                            mRenderer.setgToggle2(gFlag2);
                            } else {
                            mRenderer.setgToggle(1);
                        }
                    gFlag = mRenderer.getgToggle();

                    //IF GAUSSPATH TOGGLE SELECTED
                    /*if (xGL<mRenderer.temptoggle.left- mRenderer.slider&& xGL>mRenderer.temptoggle.right-mRenderer.slider&& yGL > mRenderer.temptoggle.down && yGL < mRenderer.temptoggle.up)
                        if (mRenderer.getgpToggle() == 1) {
                            mRenderer.setgpToggle(0);
                            mRenderer.eraseGaussLine();
                            v.vibrate(50);
                        } else {
                            mRenderer.setgpToggle(1);
                            mRenderer.makeGaussPoints();
                            v.vibrate(50);
                        }*/
                    gpFlag = mRenderer.getgpToggle();

                    //Toggle for voronoi deployment
                    if (buttonPush(xGL,yGL,mRenderer.voronoiDeploymentToggle))
                        if (mRenderer.voronoiDeploymentToggle.active == true) {
                            mRenderer.voronoiDeploymentToggle.active =  false;
                            newAction=true;
                        } else {
                            newAction=true;
                            mRenderer.voronoiDeploymentToggle.active = true;
                            mRenderer.setpToggle(0);
                            mRenderer.setpToggle2(0);
                        }

                    if (buttonPush(xGL,yGL,mRenderer.dragToggle)){
                        addedTurtleNumber=0;
                        addedTurtleX=.1f;
                        addedTurtleY=.1f;
                        for (int j=41;j<44;j++){
                            if (tList[j].getOn()!=1){
                                addedTurtleNumber=j;
                            }
                        }
                        if (addedTurtleNumber==0){
                            addTurtleFlag=false;
                        }else{
                            addTurtleFlag=true;

                        }
                    }

                    if (buttonPush(xGL,yGL,mRenderer.addTurtle)){
                        addedTurtleNumber=0;
                        addedTurtleX=.1f;
                        addedTurtleY=.1f;
                            for (int j=1;j<11;j++){
                                if (tList[j].getOn()!=1){
                                    addedTurtleNumber=j;
                                }
                            }
                        if (addedTurtleNumber==0){
                            addTurtleFlag=false;
                        }else{
                            addTurtleFlag=true;

                        }
                    }

                    if (buttonPush(xGL,yGL,mRenderer.dragRobotToggle)){
                        if (mRenderer.dragRobotToggle.active==false){
                            mRenderer.dragRobotToggle.active=true;
                        }
                        else{
                            mRenderer.dragRobotToggle.active=false;
                        }
                    }

                    if (buttonPush(xGL,yGL,mRenderer.gaussianPathToggle)){
                        if (mRenderer.gaussianPathToggle.active==true){
                            mRenderer.gaussianPathToggle.active=false;
                            mRenderer.gaussPathPlay.active=false;
                        }else{
                            mRenderer.gaussianPathToggle.active=true;
                                if (mRenderer.getfToggle()==1){//
                                    mRenderer.gaussPathPlay.active=true;
                                    mRenderer.updateGauss(mRenderer.pathArray.pose[0].x*mRenderer.scale+mRenderer.dragX*mRenderer.scale, mRenderer.pathArray.pose[0].y*mRenderer.scale+mRenderer.dragY*mRenderer.scale, 0);
                                    gaussianPathArrayIteration=1;
                                    deltaX=(mRenderer.pathArray.pose[gaussianPathArrayIteration].x - mRenderer.pathArray.pose[gaussianPathArrayIteration-1].x)/(float)Math.sqrt(Math.pow(mRenderer.pathArray.pose[gaussianPathArrayIteration].x - mRenderer.pathArray.pose[gaussianPathArrayIteration-1].x,2)+Math.pow(mRenderer.pathArray.pose[ gaussianPathArrayIteration].y -mRenderer.pathArray.pose[ gaussianPathArrayIteration-1].y,2));
                                    deltaY=(mRenderer.pathArray.pose[gaussianPathArrayIteration].y - mRenderer.pathArray.pose[gaussianPathArrayIteration-1].y)/(float)Math.sqrt(Math.pow(mRenderer.pathArray.pose[gaussianPathArrayIteration].x - mRenderer.pathArray.pose[gaussianPathArrayIteration-1].x,2)+Math.pow(mRenderer.pathArray.pose[ gaussianPathArrayIteration].y - mRenderer.pathArray.pose[ gaussianPathArrayIteration-1].y,2));

                                }
                        }
                    }

                    if (xGL<mRenderer.clearAll.left- mRenderer.slider&& xGL>mRenderer.clearAll.right-mRenderer.slider&& yGL > mRenderer.clearAll.down && yGL < mRenderer.clearAll.up){
                        if (mRenderer.clearAll.active == true) {
                            mRenderer.clearAll.active =  false;
                            v.vibrate(50);
                        } else {
                            mRenderer.clearAll.active = true;
                            v.vibrate(50);
                        }
                    }

                    if (xGL<mRenderer.resetToggle.left- mRenderer.slider&& xGL>mRenderer.resetToggle.right-mRenderer.slider&& yGL > mRenderer.resetToggle.down && yGL < mRenderer.resetToggle.up){
                        if (mRenderer.resetToggle.active == true) {
                            mRenderer.resetToggle.active =  false;
                            v.vibrate(50);
                        } else {
                            mRenderer.resetToggle.active = true;
                            v.vibrate(50);
                        }
                    }



/*                    if (xGL<-(width1-90)/height1+.05f && xGL>-(width1-90)/height1 && yGL >-(height1-10)/(height1)-mRenderer.slider  && yGL < -(height1-10)/(height1)+05f-mRenderer.slider ){

                    }*/



                    //Clear button
                    if (xGL<mRenderer.clear.left- mRenderer.slider&& xGL>mRenderer.clear.right-mRenderer.slider&& yGL > mRenderer.clear.down && yGL < mRenderer.clear.up){
                        if(mRenderer.getgToggle()==1){
                            gInd = 0;
                            mRenderer.clearGauss();

                        }
                        if(mRenderer.dragToggle.active==true){
                            obsticle.on=0;
                        }

                        deleteTurtleNumber=-1;
                        for (int i=0;i<maxBots;i++) {
                            if (tList[i].getState() == 1) {
                                deleteTurtleNumber = i;
                            }
                        }
                            if (deleteTurtleNumber!=-1){
                                deleteTurtleFlag=true;
                            }
                            else{
                                deleteTurtleFlag=false;
                            }

                        v.vibrate(75);
                        mRenderer.clear.active = true;


                    }

                }





                if (pFlag==1 && xGL>workspace  && xGL < mapLeft && yGL < mapTop && yGL > mapBottom){
                    pX=xGL-mRenderer.dragX*mRenderer.scale;
                    pY=yGL-mRenderer.dragY*mRenderer.scale;
                    pFlag2=1;
                    mRenderer.setpToggle2(pFlag2);
                    mRenderer.setWayPointValues(pX,pY);
                }

                if (gFlag==1 && xGL>workspace  && xGL < mapLeft && yGL < mapTop && yGL > mapBottom){
                    //pX=xGL;
                    //pY=yGL;
                    gFlag2=1;
                    mRenderer.setgToggle2(gFlag2);

                    //System.out.println("GAUSSIAN INDEX: " +gInd);
                    //mRenderer.setWayPointValues(xGL, yGL);
                    //mRenderer.setGaussValues(xGL, yGL, gInd);
                    //mRenderer.setGaussScale(1f);
                    if (gInd<99){
                        //mRenderer.addGaussStuff(xGL, yGL, 1f,gInd);
                        //TEMP FIX
                        mRenderer.addGaussStuff(xGL, yGL, 1f,0);
                    }
                    gInd++;
                }

                if (mRenderer.gToggle==0 && mRenderer.fToggle==0 && mRenderer.directionalDrag.active ==true&& (mRenderer.directionalDrag.active == true &&xGL<mRenderer.directionalDrag.left- mRenderer.slider&& xGL>mRenderer.directionalDrag.right-mRenderer.slider&& yGL > mRenderer.directionalDrag.down && yGL < mRenderer.directionalDrag.up)) {
                    v.vibrate(50);
                    if (xGL>mRenderer.directionalDrag.left+(mRenderer.directionalDrag.right-mRenderer.directionalDrag.left)/3-mRenderer.slider){
                        mRenderer.dragX=mRenderer.dragX-.01f;
                    }
                    if (xGL<mRenderer.directionalDrag.left+2*(mRenderer.directionalDrag.right-mRenderer.directionalDrag.left)/3-mRenderer.slider){
                        mRenderer.dragX=mRenderer.dragX+.01f;
                    }

                    if (yGL>mRenderer.directionalDrag.up+(mRenderer.directionalDrag.down-mRenderer.directionalDrag.up)/3){
                        mRenderer.dragY=mRenderer.dragY-.01f;
                    }
                    if (yGL<mRenderer.directionalDrag.up+2*(mRenderer.directionalDrag.down-mRenderer.directionalDrag.up)/3){
                        mRenderer.dragY=mRenderer.dragY+.01f;
                    }
                }

            case MotionEvent.ACTION_POINTER_DOWN:
                //System.out.println("GAUSS 0");
                if (e.getActionIndex() == 1) {


                    if(gFlag==1){
                        float gaussX = e.getX(1);

                        float gaussY = e.getY(1);


                        float gauss_xGL = (width1 / 2 - gaussX) / (float) (height1 / 1.85);
                        float gauss_yGL = (height1 / 2 + 30 - gaussY) / (float) (height1 / 1.85);

                        float gauss_dx = gauss_xGL - xGL;
                        float gauss_dy = gauss_yGL - yGL;

                        float dgauss = (float)Math.sqrt(Math.pow(gauss_dx, 2)+ Math.pow(gauss_dy, 2));

                        gaussScale = dgauss/.2f;
                        //System.out.println("SCALE");
                        //mRenderer.addGaussStuff(xGL, yGL, gaussScale,gInd-1);
                        //mRenderer.setGaussScale(gInd-1, gaussScale);
                        //TEMP FIX
                        mRenderer.setGaussScale(0, gaussScale);
                    }
                }




            case MotionEvent.ACTION_MOVE:

                mRenderer.tToggle=1;
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                if (mRenderer.dragRobotToggle.active){
                    for (int i=0;i<maxBots;i++){
                        if(tList[i].getOn()==1) {
                            if (Math.abs(tList[i].getX()*getScale()+mRenderer.dragX*mRenderer.scale - xGL) < .1f && Math.abs(tList[i].getY()*getScale()+mRenderer.dragY*mRenderer.scale  - yGL) < .1f) {
                                addedTurtleNumber=i;
                                addedTurtleX=xGL/getScale()-mRenderer.dragX;
                                addedTurtleY=yGL/getScale()-mRenderer.dragY;
                                addTurtleFlag=true;
                                v.vibrate(50);
                            }
                        }
                    }

                }
//pX+(float)Math.cos(formation.val4)*.3f+dragX*scale, pY+(float)Math.sin(formation.val4)*.3f+dragY*scale, 0);
                if (mRenderer.formation.active && mRenderer.formationToggleTransform.active){

                    if (Math.abs(mRenderer.formation.x*mRenderer.scale+(float)Math.cos(mRenderer.formation.val4)*.3f*mRenderer.scale+mRenderer.dragX*mRenderer.scale-xGL)<.15f && Math.abs(mRenderer.formation.y*mRenderer.scale+(float)Math.sin(mRenderer.formation.val4)*.3f*mRenderer.scale+mRenderer.dragY*mRenderer.scale-yGL)<.15f){
                        mRenderer.formation.val4=(float)Math.atan2(yGL- (mRenderer.formation.y*mRenderer.scale+mRenderer.dragY*mRenderer.scale),xGL-(mRenderer.formation.x*mRenderer.scale+mRenderer.dragX*mRenderer.scale) );
                        mRenderer.refreshFormationtext();
                        mRenderer.justifyFormationLocations();
                        mRenderer.rotation_block.active=true;
                        mRenderer.formation.send=true;
                    }else{mRenderer.rotation_block.active=false;}
                }

                if (mRenderer.formation.active && mRenderer.formationToggleTransform.active){

                    if (Math.abs(mRenderer.formation.x*mRenderer.scale+mRenderer.dragX*mRenderer.scale-xGL)<.15f && Math.abs(mRenderer.formation.y*mRenderer.scale+mRenderer.dragY*mRenderer.scale-yGL)<.15f){
                        pX=xGL-mRenderer.scale*mRenderer.dragX;
                        pY=yGL-mRenderer.scale*mRenderer.dragY;
                        mRenderer.setWayPointValues(pX, pY);
                        mRenderer.refreshFormationtext();
                        mRenderer.justifyFormationLocations();
                        mRenderer.center_block.active=true;
                        mRenderer.formation.send=true;
                    }else{mRenderer.center_block.active=false;}
                }

                if (mRenderer.formation.active && mRenderer.formationToggleTransform.active){

                    if (Math.abs(mRenderer.formation.x*mRenderer.scale+mRenderer.dragX*mRenderer.scale+mRenderer.formation.val3*.2f*mRenderer.scale-xGL)<.15f && Math.abs(mRenderer.formation.y*mRenderer.scale+mRenderer.dragY*mRenderer.scale-yGL)<.15f){
                        mRenderer.formation.val3=(xGL-(mRenderer.formation.x*mRenderer.scale+mRenderer.dragX*mRenderer.scale))/.2f/mRenderer.scale;
                        mRenderer.refreshFormationtext();
                        mRenderer.justifyFormationLocations();
                        mRenderer.scale_block.active=true;
                        mRenderer.formation.send=true;
                    }else{mRenderer.scale_block.active=false;}
                }

                if (mRenderer.formation.active && mRenderer.customFormation.active){
                    for(int i=0;i<mRenderer.formation.val1;i++) {
                        if (target(convertToScreenPosition(mRenderer.formation_locations.x[i], true), xGL, convertToScreenPosition(mRenderer.formation_locations.y[i], false), yGL, .15f)) {
                            //mRenderer.formation.x=mRenderer.formation.x*mRenderer.formation.val1-mRenderer.formation_locations.x[i];
                            //mRenderer.formation.y=mRenderer.formation.y*mRenderer.formation.val1-mRenderer.formation_locations.y[i];

                            mRenderer.formation_locations.x[i] = convertToGlobalPosition(xGL, true);
                            mRenderer.formation_locations.y[i] = convertToGlobalPosition(yGL, false);

                            //mRenderer.formation.x=mRenderer.formation.x+mRenderer.formation_locations.x[i];
                            //mRenderer.formation.x=mRenderer.formation.x/mRenderer.formation.val1;

                            //mRenderer.formation.y=mRenderer.formation.y+mRenderer.formation_locations.y[i];
                            //mRenderer.formation.y=mRenderer.formation.y/mRenderer.formation.val1;
                            mRenderer.refreshCenter();
                            break;
                        }
                    }
                }





                    if (mRenderer.dragToggle.active==true && xGL>workspace
                    && xGL < mapLeft && yGL < mapTop  && yGL > mapBottom){
                        obsticle.x=xGL/getScale()-mRenderer.dragX;
                        obsticle.y=yGL/getScale()-mRenderer.dragY;
                        obsticle.Aw=1;
                        obsticle.on=1;
                        newObstacle=true;
                    }

                if (mRenderer.gToggle==0 && mRenderer.fToggle==0 && mRenderer.directionalDrag.active ==true&& (mRenderer.directionalDrag.active == true &&xGL<mRenderer.directionalDrag.left- mRenderer.slider&& xGL>mRenderer.directionalDrag.right-mRenderer.slider&& yGL > mRenderer.directionalDrag.down && yGL < mRenderer.directionalDrag.up)) {
                    v.vibrate(50);
                    if (xGL>mRenderer.directionalDrag.left+(mRenderer.directionalDrag.right-mRenderer.directionalDrag.left)/3-mRenderer.slider){
                        mRenderer.dragX=mRenderer.dragX-.01f;
                        System.out.println("LEFT");


                    }
                    if (xGL<mRenderer.directionalDrag.left+2*(mRenderer.directionalDrag.right-mRenderer.directionalDrag.left)/3-mRenderer.slider){
                        mRenderer.dragX=mRenderer.dragX+.01f;
                        System.out.println("RIGHT");
                    }

                    if (yGL>mRenderer.directionalDrag.up+(mRenderer.directionalDrag.down-mRenderer.directionalDrag.up)/3){
                        mRenderer.dragY=mRenderer.dragY-.01f;
                        System.out.println("UP");
                    }
                    if (yGL<mRenderer.directionalDrag.up+2*(mRenderer.directionalDrag.down-mRenderer.directionalDrag.up)/3){
                        mRenderer.dragY=mRenderer.dragY+.01f;
                        System.out.println("DOWN");
                    }
                }



                if (fFlag==1 && (Math.abs(xGL-previousx)> .03f || Math.abs(yGL -previousy)>.03f) && xGL>workspace
                        && xGL < mapLeft && yGL < mapTop  && yGL > mapBottom) {
                    if (previousx!=0 && previousy!=0){
                        if (firstPointFreeDraw[0]==-1000){
                            firstPointFreeDraw[0]=previousx;
                            firstPointFreeDraw[1]=previousy;
                        }
                        else if (xGL > firstPointFreeDraw[0]+.1f || xGL < firstPointFreeDraw[0]-.1f || yGL > firstPointFreeDraw[1]+.1f || yGL < firstPointFreeDraw[1]-.1f){
                            connectable=1;
                        }
                        else if (connectable==1){
                            fFlag=0;
                            setFreeDrawCoordinates(firstPointFreeDraw[0],firstPointFreeDraw[1], previousx, previousy,true);
                            v.vibrate(50);
                        }

                        if (fFlag == 1) {
                            setFreeDrawCoordinates(xGL-mRenderer.dragX*mRenderer.scale,yGL-mRenderer.dragY*mRenderer.scale, previousx-mRenderer.dragX*mRenderer.scale, previousy-mRenderer.dragY*mRenderer.scale,false);
                        }

                    }

                    previousx=xGL;
                    previousy=yGL;
                }


                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                if (yGL > -.15f && yGL < .15f && xGL>-(width1-115)/(height1*2)-mRenderer.slider  && xGL < -(width1-115)/(height1*2)-mRenderer.slider+.15f){
                    mRenderer.slider=-(width1-115)/(height1*2)-xGL;
                    if (mRenderer.slider<.1f){
                        mRenderer.slider=0;
                    }
                    else if (mRenderer.slider>-.1f+(width1-115)/(height1*2)) {
                        mRenderer.slider=(width1-115)/(height1*2);
                    }
                }


                mRenderer.setAngle(
                        mRenderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                requestRender();


                if (pFlag==1 && xGL>workspace  && xGL < mapLeft && yGL < mapTop && yGL > mapBottom){
                    pX=xGL-mRenderer.scale*mRenderer.dragX;
                    pY=yGL-mRenderer.scale*mRenderer.dragY;
                    pFlag2=1;
                    mRenderer.setpToggle2(pFlag2);
                    mRenderer.setWayPointValues(pX, pY);
                }


                //moving gauss
                if (gFlag==1 && xGL>workspace  && xGL < mapLeft && yGL < mapTop && yGL > mapBottom){
                    pX=xGL;
                    pY=yGL;
                    gFlag2=1;
                    mRenderer.setpToggle2(gFlag2);
                    //TEMP FIX
                    mRenderer.updateGauss(xGL, yGL, 0);
                    //mRenderer.updateGauss(xGL, yGL, gInd-1);
                }

                if (mRenderer.gToggle==1 &&  xGL<mRenderer.bar.left+.04f- mRenderer.slider && xGL>mRenderer.bar.right-.04f-mRenderer.slider&& yGL > mRenderer.dial.down && yGL < mRenderer.dial.up){
                        mRenderer.dial1=-(xGL-mRenderer.bar.left-mRenderer.slider)/.6f;
                        if (mRenderer.dial1<0.05){
                        mRenderer.dial1 = 0;
                        }else if (mRenderer.dial1>1){
                            mRenderer.dial1=1;
                        }
                        else if (mRenderer.dial1<.15){
                            mRenderer.dial1=.1f;
                        }
                        else if (mRenderer.dial1<.25){
                            mRenderer.dial1=.2f;
                        }
                        else if (mRenderer.dial1<.35){
                            mRenderer.dial1=.3f;
                        }
                        else if (mRenderer.dial1<.45){
                            mRenderer.dial1=.4f;
                        }
                        else if (mRenderer.dial1<.55){
                            mRenderer.dial1=.5f;
                        }
                        else if (mRenderer.dial1<.65){
                            mRenderer.dial1=.6f;
                        }
                        else if (mRenderer.dial1<.75){
                            mRenderer.dial1=.7f;
                        }
                        else if (mRenderer.dial1<.85){
                            mRenderer.dial1=.8f;
                        }
                        else if (mRenderer.dial1<.95){
                            mRenderer.dial1=.9f;
                        }
                        else{
                            mRenderer.dial1=1;
                        }
                        mRenderer.textListGaussian.get(1).setText(" "+ truncateDecimal(mRenderer.dial1,1));
                    mRenderer.dial.active=true;
                    v.vibrate(50);
                }

                if (mRenderer.gToggle==1 && xGL<mRenderer.bar_2.left+.04f- mRenderer.slider && xGL>mRenderer.bar_2.right-.04f-mRenderer.slider&& yGL > mRenderer.dial_2.down && yGL < mRenderer.dial_2.up){
                    mRenderer.dial2=-(xGL-mRenderer.bar_2.left-mRenderer.slider)/.6f;
                    if (mRenderer.dial2<0.05){
                        mRenderer.dial2 = 0;
                    }else if (mRenderer.dial2>1){
                        mRenderer.dial2=1;
                    }
                    else if (mRenderer.dial2<.15){
                        mRenderer.dial2=.1f;
                    }
                    else if (mRenderer.dial2<.25){
                        mRenderer.dial2=.2f;
                    }
                    else if (mRenderer.dial2<.35){
                        mRenderer.dial2=.3f;
                    }
                    else if (mRenderer.dial2<.45){
                        mRenderer.dial2=.4f;
                    }
                    else if (mRenderer.dial2<.55){
                        mRenderer.dial2=.5f;
                    }
                    else if (mRenderer.dial2<.65){
                        mRenderer.dial2=.6f;
                    }
                    else if (mRenderer.dial2<.75){
                        mRenderer.dial2=.7f;
                    }
                    else if (mRenderer.dial2<.85){
                        mRenderer.dial2=.8f;
                    }
                    else if (mRenderer.dial2<.95){
                        mRenderer.dial2=.9f;
                    }
                    else{
                        mRenderer.dial2=1;
                    }
                    mRenderer.textListGaussian.get(3).setText(" "+ truncateDecimal(mRenderer.dial2,1));
                    mRenderer.dial_2.active=true;
                    v.vibrate(50);
                }

                if (mRenderer.gToggle==1 && xGL<mRenderer.bar_3.left+.04f- mRenderer.slider && xGL>mRenderer.bar_3.right-.04f-mRenderer.slider&& yGL > mRenderer.dial_3.down && yGL < mRenderer.dial_3.up){
                    mRenderer.dial3=-(xGL-mRenderer.bar_3.left-mRenderer.slider)/.6f;
                    if (mRenderer.dial3<0.05){
                        mRenderer.dial3 = 0;
                    }else if (mRenderer.dial3>1){
                        mRenderer.dial3=1;
                    }
                    else if (mRenderer.dial3<.15){
                        mRenderer.dial3=.1f;
                    }
                    else if (mRenderer.dial3<.25){
                        mRenderer.dial3=.2f;
                    }
                    else if (mRenderer.dial3<.35){
                        mRenderer.dial3=.3f;
                    }
                    else if (mRenderer.dial3<.45){
                        mRenderer.dial3=.4f;
                    }
                    else if (mRenderer.dial3<.55){
                        mRenderer.dial3=.5f;
                    }
                    else if (mRenderer.dial3<.65){
                        mRenderer.dial3=.6f;
                    }
                    else if (mRenderer.dial3<.75){
                        mRenderer.dial3=.7f;
                    }
                    else if (mRenderer.dial3<.85){
                        mRenderer.dial3=.8f;
                    }
                    else if (mRenderer.dial3<.95){
                        mRenderer.dial3=.9f;
                    }
                    else{
                        mRenderer.dial3=1;
                    }
                    mRenderer.textListGaussian.get(5).setText(" "+ truncateDecimal(mRenderer.dial3,1));
                    mRenderer.dial_3.active=true;
                    v.vibrate(50);
                }

            case MotionEvent.ACTION_UP:

                mRenderer.tToggle=1;
                //mRenderer.clear.active = false;

        }



        mPreviousX = x;
        mPreviousY = y;

        antispam=0;
        return true;
    }

    public void setVoronoiCoordinates(){

        temp.clear();
        temp2.clear();
        for (int i=0;i<maxBots;i++){
            if (tList[i].getOn()==1) {
                temp.add((double) tList[i].getX());
                temp2.add((double) tList[i].getY());
                recievedTurtles=true;
            }
        }
        if (temp!=null && recievedTurtles==true) {

            double[] temp3 = new double[temp.size()];
            double[] temp4 = new double[temp.size()];
            for (int i=0;i<temp.size();i++){
                temp3[i]=temp.get(i);
                temp4[i]=temp2.get(i);
            }

            //voronoiEdges = vor.generateVoronoi(temp3, temp4, -width1 / (height1*mRenderer.scale)*2, width1 / (height1*mRenderer.scale)*2, -height1 / (height1*mRenderer.scale)*2, height1 / (height1*mRenderer.scale)*2);
            voronoiEdges = vor.generateVoronoi(temp3, temp4, -1, 1, -1, 1);

            for(int i = 0; i < voronoiEdges.size(); i++) {
                cd = Math.cos(Math.atan((voronoiEdges.get(i).x1 - voronoiEdges.get(i).x2) / (voronoiEdges.get(i).y1 - voronoiEdges.get(i).y2)));
                cy = Math.sin(Math.atan((voronoiEdges.get(i).x1 - voronoiEdges.get(i).x2) / (voronoiEdges.get(i).y1 - voronoiEdges.get(i).y2)));

                vorCoords[0] = (float) voronoiEdges.get(i).x1 + (float) cd * .005f/mRenderer.scale;
                vorCoords[1] = (float) voronoiEdges.get(i).y1 - (float) cy * .005f/mRenderer.scale;

                vorCoords[9] = (float) voronoiEdges.get(i).x2 + (float) cd * .005f/mRenderer.scale;
                vorCoords[10] = (float) voronoiEdges.get(i).y2 - (float) cy * .005f/mRenderer.scale;


                vorCoords[3] = (float) voronoiEdges.get(i).x1 - (float) cd * .005f/mRenderer.scale;
                vorCoords[4] = (float) voronoiEdges.get(i).y1 + (float) cy * .005f/mRenderer.scale;

                vorCoords[6] = (float) voronoiEdges.get(i).x2 - (float) cd * .005f / mRenderer.scale;
                vorCoords[7] = (float) voronoiEdges.get(i).y2 + (float) cy * .005f/mRenderer.scale;

                mRenderer.setVoronoiCoordinates(vorCoords, i, voronoiEdges.size(),voronoiEdges.get(i).x1,voronoiEdges.get(i).y1,voronoiEdges.get(i).x2,voronoiEdges.get(i).y2);

            }
        }
    }


    public void setFreeDrawCoordinates(float x, float y, float xp, float yp,boolean closed){

        float Coords[] = {
                -0.5f,  0.5f, 0.0f,   // top left
                -0.5f, -0.5f, 0.0f,   // bottom left
                0.5f, -0.5f, 0.0f,   // bottom right
                0.5f,  0.5f, 0.0f }; // top right

               double cd = Math.cos(Math.atan((x - xp) / (y - yp)));
                double cy = Math.sin(Math.atan((x - xp) / (y - yp)));

                Coords[0] = x + (float) cd * .005f*mRenderer.scale;
                Coords[0]=Coords[0]/mRenderer.scale;
                Coords[1] = y - (float) cy * .005f*mRenderer.scale;
                Coords[1]=Coords[1]/mRenderer.scale;


                Coords[9] = xp + (float) cd * .005f*mRenderer.scale;
                Coords[9]=Coords[9]/mRenderer.scale;
                Coords[10] =  yp - (float) cy * .005f*mRenderer.scale;
                Coords[10]=Coords[10]/mRenderer.scale;

                Coords[3] = x - (float) cd * .005f*mRenderer.scale;
                Coords[3]=Coords[3]/mRenderer.scale;
                Coords[4] = y + (float) cy * .005f*mRenderer.scale;
                Coords[4]=Coords[4]/mRenderer.scale;

                Coords[6] = xp - (float) cd * .005f*mRenderer.scale;
                Coords[6]=Coords[6]/mRenderer.scale;
                Coords[7] = yp + (float) cy * .005f*mRenderer.scale;
                Coords[7]=Coords[7]/mRenderer.scale;

        freeDrawCount++;
                if (freeDrawCount<100){
                    mRenderer.setFreeDrawCoordinates(Coords,freeDrawCount-1,freeDrawCount,xp,yp,x,y,closed);
                }
        pathPublisherFlag=true;

    }




    public float getpX(){
        return pX;
    }


    public float getpY(){
        return pY;
    }

    public float[] getGaussX() {
        return mRenderer.getGaussX();
    }
    public float[] getGaussY() {
        return mRenderer.getGaussY();
    }
    public float[] getGaussScale() {
        return mRenderer.getGaussScale();
    }



    public dummyPoseArray passPathArray(){
        return mRenderer.pathArray;
    }

    public float getScale(){
        return mRenderer.scale;
    }

    public boolean getActive(){
        return mRenderer.voronoiDeploymentToggle.active;
    }

    private static BigDecimal truncateDecimal(float x,int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_HALF_DOWN);
        }
    }

    public void setCentroids(dummyPoseArray tempArray){
        mRenderer.centroids=tempArray;
    }

    public boolean getObsticleActivity(){
        return mRenderer.dragToggle.active;
    }


    public gauss getGausses(){
        return mRenderer.gaussArrayList;
    }



    //FOUND_ONLINE
    public WifiInfo getWifi(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo;
            }
        }
        return null;
    }


    public void gaussDynamics(){
        mRenderer.gaussArrayList.locX[0]=mRenderer.gaussArrayList.locX[0]+deltaX/600;
        mRenderer.gaussArrayList.locY[0]=mRenderer.gaussArrayList.locY[0]+deltaY/600;


       if (Math.abs(mRenderer.gaussArrayList.locX[0]-mRenderer.pathArray.pose[gaussianPathArrayIteration].x)<.001 && Math.abs(mRenderer.gaussArrayList.locY[0]-mRenderer.pathArray.pose[gaussianPathArrayIteration].y)<.001 ){
            gaussianPathArrayIteration=gaussianPathArrayIteration+1;
        }
        if (gaussianPathArrayIteration< mRenderer.fSize){
            delta();
        }
        else{
            deltaX=0;
            deltaY=0;
            mRenderer.gaussPathPlay.active=false;
        }
    }

    public void delta(){
        deltaX=(mRenderer.pathArray.pose[gaussianPathArrayIteration].x - mRenderer.gaussArrayList.locX[0])/(float)Math.sqrt(Math.pow(mRenderer.pathArray.pose[gaussianPathArrayIteration].x - mRenderer.gaussArrayList.locX[0],2)+Math.pow(mRenderer.pathArray.pose[ gaussianPathArrayIteration].y - mRenderer.gaussArrayList.locY[0],2));
        deltaY=(mRenderer.pathArray.pose[gaussianPathArrayIteration].y - mRenderer.gaussArrayList.locY[0])/(float)Math.sqrt(Math.pow(mRenderer.pathArray.pose[gaussianPathArrayIteration].x - mRenderer.gaussArrayList.locX[0],2)+Math.pow(mRenderer.pathArray.pose[ gaussianPathArrayIteration].y - mRenderer.gaussArrayList.locY[0],2));
    }


    public boolean buttonPush(float xGL,float yGL,toggles t){
        if (xGL<t.left- mRenderer.slider&& xGL>t.right-mRenderer.slider&& yGL > t.down && yGL < t.up && t.blocked==false) {
            v.vibrate(50);
            return true;
        }
        return false;
    }

    public boolean target(float x, float xGL, float y, float yGL, float threshold){
        if (Math.abs(x-xGL)<threshold && Math.abs(y-yGL)<threshold){
            return true;
        }else{
            return false;
        }
    }

    public float convertToScreenPosition(float x,boolean X){
        if (X){
            return x*mRenderer.scale+mRenderer.dragX*mRenderer.scale;
        }else{
            return x*mRenderer.scale+mRenderer.dragY*mRenderer.scale;
        }
    }
    public float convertToGlobalPosition(float x, boolean X) {
        if (X){
            return (x-mRenderer.dragX*mRenderer.scale)/mRenderer.scale;
        }else{
            return (x-mRenderer.dragY*mRenderer.scale)/mRenderer.scale;
        }
    }





}

