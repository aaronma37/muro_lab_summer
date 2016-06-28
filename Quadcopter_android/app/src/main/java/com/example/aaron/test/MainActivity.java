package com.example.aaron.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.aaron.simplevoronoi.src.main.java.be.humphreys.simplevoronoi.*;
import org.ros.address.InetAddressFactory;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeListener;
import org.ros.node.NodeMainExecutor;
import geometry_msgs.Point;
import geometry_msgs.Pose;

import com.example.aaron.simplevoronoi.src.main.java.be.humphreys.simplevoronoi.Voronoi;
import com.example.aaron.test.Talker;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//import org.ros.rosjava_tutorial_pubsub.Talker;


/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity {

    private RosTextView<std_msgs.String> rosTextView;
    private Talker talker;
    private dummyMaker dummy;
    private poseView poseview;
    private formation_publisher formationPublisher;
    private MyGLSurfaceView mGLView;
    private PathPublisher pathPublisher;
    private GaussPublisher gaussPublisher;
    private pingSender pinger;
    private multipleGoalListener MultipleGoalListener;
    private allPositionsPublisher SelectedPositionsPublisher;
    private pingListener pinging;
    private float meanCentroid[]= new float[3];
    private float width1,height1;
    private messager message;
    final int maxBots=50;
    private int flag=0;
    private long time1=0;
    public float ping=0;
    private long time2=0;
    public Intent intent;
    public Voronoi vor;
    public double p[];
    public float pos[]={0,0,0,0,0};
    public float poseData[]={0,0,0,0,0};
    public turtle[] turtleList=new turtle[maxBots];
    public View decorView;
    private int currentApiVersion;
    //public turtle turt;
    TextView poseX;
    private List<GraphEdge> voronoiEdges;
    ArrayList<Double> temp= new ArrayList<Double>();
    ArrayList<Double> temp2= new ArrayList<Double>();




    public MainActivity() {
        super("Pubsub Tutorial", "Pubsub Tutorial");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }



        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();

        rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.text);
        rosTextView.setTopicName("chatter");
        rosTextView.setMessageType(std_msgs.String._TYPE);
        //std_msgs.String message = "Aaron Ma";
        rosTextView.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });

        mGLView = new MyGLSurfaceView(this, pos,turtleList);
        setContentView(mGLView);
        for (int i=0;i<maxBots;i++){
            turtleList[i]=new turtle();
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void init(final NodeMainExecutor nodeMainExecutor) {



        double num=1;
        talker = new Talker(num);

        dummy=new dummyMaker(num);
        pathPublisher=new PathPublisher();
        gaussPublisher = new GaussPublisher();
        poseview = new poseView();
        MultipleGoalListener = new multipleGoalListener();
        SelectedPositionsPublisher= new allPositionsPublisher();
        vor = new Voronoi(.001);

        //NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();
        final NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress().toString(), getMasterUri());
        nodeConfiguration.setMasterUri(getMasterUri());
        width1=mGLView.getWidth1();
        height1=mGLView.getHeight1();
        talker=new Talker(num);
        dummy=new dummyMaker(num);
        formationPublisher=new formation_publisher(num);

        pathPublisher=new PathPublisher();
        pinger = new pingSender();
        pinging = new pingListener();
        message = new messager();


        nodeMainExecutor.execute(poseview, nodeConfiguration);


            nodeMainExecutor.execute(dummy, nodeConfiguration);
        if (mGLView.mRenderer.formation.blocked==false){
            nodeMainExecutor.execute(formationPublisher, nodeConfiguration);
        }
        if (mGLView.mRenderer.obstacleLineToggle.blocked==false) {
            nodeMainExecutor.execute(pathPublisher, nodeConfiguration);
        }
        if (mGLView.mRenderer.gaussToggle.blocked==false){
            nodeMainExecutor.execute(gaussPublisher, nodeConfiguration);
        }
        if (mGLView.mRenderer.wayPointToggle.blocked==false){
            nodeMainExecutor.execute(talker, nodeConfiguration);
        }


        num=poseview.getX();
        talker.setNum(num);



        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(5);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (poseview.newMeasurementFlag == 1) {
                    if (poseview.gotgauss == false) {
                        turtleList = poseview.getTurtles();
                        mGLView.setCentroids(poseview.dummyArray);

                    } else {
                        mGLView.mRenderer.gaussArrayList.locX[0] = poseview.gauss.getX() / mGLView.mRenderer.scale;
                        mGLView.mRenderer.gaussArrayList.locY[0] = poseview.gauss.getY() / mGLView.mRenderer.scale;
                        mGLView.mRenderer.gaussArrayList.scaleG[0] = poseview.gauss.getZ() / mGLView.mRenderer.scale;
                    }

                    poseview.newMeasurementFlag = 0;

                    mGLView.updateRen(turtleList);
                    time1 = System.currentTimeMillis();
                    for (int i = 0; i < turtleList.length; i++) {
                        if (turtleList[i].getOn() == 1) {
                            turtleList[i].deltaT = (time1 - time2) / 1000;
                        }
                    }
                    time2 = time1;
                } else {
                    /*for (int i = 0; i < turtleList.length; i++) {
                        if (turtleList[i].getOn() == 1) {
                            turtleList[i].runPredictor();
                        }
                    }*/
                    //mGLView.updateRen(turtleList);
                }


            }
        }, 0, 35000, TimeUnit.MICROSECONDS);

    //}, 0, 35000, TimeUnit.MICROSECONDS);


        ScheduledThreadPoolExecutor exec2 = new ScheduledThreadPoolExecutor(5);
        exec2.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (flag == 1) {
                    mGLView.setVoronoiCoordinates();
                }
                flag = mGLView.vFlag;
                mGLView.tick();


                if (mGLView.pFlag == 1 && mGLView.pFlag2 == 1) {
                    talker.setPoint(mGLView.getpX() / mGLView.getScale(), mGLView.getpY() / mGLView.getScale());
                    talker.flag = 1;
                } else {
                    talker.flag = 0;
                }


                if (mGLView.addTurtleFlag==true){
                    dummy.type=mGLView.addedTurtleNumber;
                    dummy.xx=mGLView.addedTurtleY;
                    dummy.yy=mGLView.addedTurtleZ;
                    dummy.zz=mGLView.addedTurtleX;
                    dummy.flag = 1;
                    mGLView.addTurtleFlag = false;
                }else{
                    mGLView.addTurtleFlag = false;
                }

                if (mGLView.addObstacleFlag==true){


                    dummy.type=301;
                    for (int i=0;i<mGLView.mRenderer.obstacleLineList.size();i++){

                        dummy.xxArray[i]=mGLView.mRenderer.obstacleLineList.get(i).x[0];
                        dummy.yyArray[i]=mGLView.mRenderer.obstacleLineList.get(i).y[0];
                        dummy.zzArray[i]=mGLView.mRenderer.obstacleLineList.get(i).x[1];
                        dummy.wwArray[i]=mGLView.mRenderer.obstacleLineList.get(i).y[1];


                    }
                    dummy.size=mGLView.mRenderer.obstacleLineList.size();


                    dummy.flag = 1;
                    mGLView.addObstacleFlag = false;

                }
                else{
                    mGLView.addObstacleFlag = false;
                }

                if (mGLView.freeBoundaryFlag==true){
                    dummy.type=201;
                    dummy.flag = 1;
                    mGLView.freeBoundaryFlag = false;
                }else{
                    mGLView.freeBoundaryFlag = false;
                }

                if (mGLView.closedBoundaryFlag==true){
                    dummy.type=202;
                    dummy.flag = 1;
                    mGLView.closedBoundaryFlag = false;
                }else{
                    mGLView.closedBoundaryFlag = false;
                }

                if (mGLView.deleteTurtleFlag==true){
                    dummy.type=-mGLView.deleteTurtleNumber;
                    dummy.flag=1;
                    turtleList[-dummy.type].on=0;
                    turtleList[-dummy.type].state=0;
                    mGLView.deleteTurtleFlag=false;
                }else{
                    mGLView.deleteTurtleFlag=false;
                }


                if (mGLView.dummyFlag == 1) {
                    dummy.type=0;
                    dummy.flag = 1;
                    mGLView.dummyFlag = 0;
                } else {
                    mGLView.dummyFlag = 0;
                }

                for (int i = 0; i < turtleList.length; i++) {
                    if (turtleList[i].getOn() == 1) {
                        turtleList[i].setRot();
                    }
                }

                if (mGLView.obsticle.on == 1 && mGLView.newObstacle==true) {
                    //turtleList[49] = mGLView.obsticle;
                    dummy.type=49;
                    dummy.xx=mGLView.obsticle.y; //turtleList[49].x;
                    dummy.yy=mGLView.obsticle.z;
                    dummy.zz=mGLView.obsticle.x;
                    dummy.flag=1;
                    mGLView.newObstacle=false;
                    //mGLView.obsticle.on=0;
                } else if (mGLView.clearObstacles==true){
                    turtleList[49].x = 0;
                    turtleList[49].y = 0;
                    turtleList[49].z = 0;
                }

                if (mGLView.mRenderer.gaussPathPlay.active==true){

                    mGLView.gaussDynamics();

                }

                if (mGLView.mRenderer.formation.send==true){
                    //formationPublisher.type=mGLView.addedTurtleNumber;
                    //formationPublisher.xx=mGLView.addedTurtleY;
                    //formationPublisher.yy=mGLView.addedTurtleZ;
                    //formationPublisher.zz=mGLView.addedTurtleX;
                    formationPublisher.setFormation(mGLView.mRenderer.formation.val2);
                    if (mGLView.mRenderer.formation.val2==0){
                        for (int i=0;i<mGLView.mRenderer.formation.val1;i++){
                            formationPublisher.setLocations(mGLView.mRenderer.relative_formation_locations.x[i],mGLView.mRenderer.relative_formation_locations.y[i],i);
                        }
                    }
                    formationPublisher.setNumActive(mGLView.mRenderer.formation.val1);
                    formationPublisher.setScale(mGLView.mRenderer.formation.val3);
                    formationPublisher.setRotation(-mGLView.mRenderer.formation.val4);

                    formationPublisher.centerX=mGLView.mRenderer.formation.x;
                    formationPublisher.centerY=-mGLView.mRenderer.formation.y;
                    formationPublisher.flag = 1;

                    mGLView.mRenderer.formation.send= false;
                }else{
                    mGLView.mRenderer.formation.send = false;
                }



                mGLView.updateRen(turtleList);

            }

        }, 0, 50000, TimeUnit.MICROSECONDS);



        ScheduledThreadPoolExecutor exec3 = new ScheduledThreadPoolExecutor(5);
        exec3.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (mGLView.pathPublisherFlag==true && mGLView.newAction==true){
                    pathPublisher.setPathArray(mGLView.passPathArray());
                    pathPublisher.swarm=mGLView.mRenderer.swarmToggle.active;
                    pathPublisher.active=1;
                    pathPublisher.flag=true;
                    mGLView.newAction=false;
                }
                else{
                    pathPublisher.active=0;
                }


                /*if (mGLView.getActive()==true){
                    message.setMethod(1);
                    if (mGLView.newAction==true){
                        message.active=true;
                        nodeMainExecutor.execute(message, nodeConfiguration);
                        mGLView.newAction=false;
                    }

                    *//*if (message.active==false){
                        nodeMainExecutor.shutdownNodeMain(message);
                    }*//*

                }
                else{
                    message.setMethod(0);
                    if (mGLView.newAction==true){
                        message.active=true;
                        nodeMainExecutor.execute(message, nodeConfiguration);
                        mGLView.newAction=false;
                    }

                    *//*if (message.active==false){
                        nodeMainExecutor.shutdownNodeMain(message);
                    }*//*
                }*/

                if (mGLView.getActive()==true){

                    if(SelectedPositionsPublisher.active==0){
                        //nodeMainExecutor.execute(SelectedPositionsPublisher, nodeConfiguration);
                        //nodeMainExecutor.execute(MultipleGoalListener, nodeConfiguration);
                    }
                    SelectedPositionsPublisher.active=1;
                    SelectedPositionsPublisher.setPositions(turtleList);
                    SelectedPositionsPublisher.flag=true;
                    MultipleGoalListener.flag =true;
                    //mGLView.setCentroids(MultipleGoalListener.dummyArray);
                }
                else{
                    SelectedPositionsPublisher.active=0;
                    nodeMainExecutor.shutdownNodeMain(SelectedPositionsPublisher);
                    nodeMainExecutor.shutdownNodeMain(MultipleGoalListener);
                }

                if (mGLView.gFlag==1 || mGLView.mRenderer.gaussPathPlay.active==true){
                    gaussPublisher.getGaussData(mGLView.getGausses());
                    gaussPublisher.setGaussSize(mGLView.mRenderer.dial3);
                    gaussPublisher.tracking=mGLView.mRenderer.centroidTrackingOption.active;
                    gaussPublisher.activeSleep=20;
                    if(gaussPublisher.active==0){
                        gaussPublisher.active=1;
                    }
                    if(mGLView.mRenderer.gaussianTrackToggle.active==true){
                        mGLView.mRenderer.dragX=-mGLView.mRenderer.gaussArrayList.locX[0];
                        mGLView.mRenderer.dragY=-mGLView.mRenderer.gaussArrayList.locY[0];
                    }
                }
                    else{
                    gaussPublisher.active=0;
                    gaussPublisher.activeSleep=1000;
                }

                if (mGLView.mRenderer.resetToggle.active==true){

                    mGLView.vFlag=0;
                    flag=0;
                    mGLView.mRenderer.setvToggle(0);

                    for (int i=0;i<maxBots;i++){
                        turtleList[i]=new turtle();
                    }


                    for (int k=0;k<mGLView.mRenderer.centroids.pose.length;k++){
                        mGLView.mRenderer.centroids.pose[k].active=false;
                    }

                    nodeMainExecutor.shutdownNodeMain(poseview);
                    nodeMainExecutor.execute(poseview, nodeConfiguration);


                        nodeMainExecutor.shutdownNodeMain(dummy);
                        nodeMainExecutor.execute(dummy, nodeConfiguration);

                    if (mGLView.mRenderer.formation.blocked==false){
                        nodeMainExecutor.shutdownNodeMain(formationPublisher);
                        nodeMainExecutor.execute(formationPublisher, nodeConfiguration);


                    }
                    if (mGLView.mRenderer.obstacleLineToggle.blocked==false) {
                        nodeMainExecutor.shutdownNodeMain(pathPublisher);
                        nodeMainExecutor.execute(pathPublisher, nodeConfiguration);

                    }
                    if (mGLView.mRenderer.gaussToggle.blocked==false){
                        nodeMainExecutor.shutdownNodeMain(gaussPublisher);
                        nodeMainExecutor.execute(gaussPublisher, nodeConfiguration);
                    }
                    if (mGLView.mRenderer.wayPointToggle.blocked==false){
                        nodeMainExecutor.execute(talker, nodeConfiguration);
                    }


                    mGLView.mRenderer.resetToggle.active=false;
                }



                if (mGLView.mRenderer.clearAll.active==true){
                        dummy.type=100;
                        dummy.flag = 1;
                        mGLView.vFlag=0;
                    flag=0;
                        mGLView.mRenderer.setvToggle(0);

                    for (int i=0;i<maxBots;i++){
                        turtleList[i]=new turtle();
                    }

                    mGLView.mRenderer.clearAll.active=false;
                }

                if (mGLView.mRenderer.dial.active==true){
                    dummy.xx=mGLView.mRenderer.dial1;
                    dummy.type=101;
                    dummy.flag=1;
                    mGLView.mRenderer.dial.active=false;
                }

                if (mGLView.mRenderer.dial_2.active==true){
                    dummy.xx=mGLView.mRenderer.dial2;
                    dummy.type=102;
                    dummy.flag=1;
                    mGLView.mRenderer.dial_2.active=false;
                }





                //mGLView.mRenderer.printAdjacencyMatrix();




            }
        }, 0, 50000, TimeUnit.MICROSECONDS);




        //SLOW PROCESSES



      /*  nodeMainExecutor.execute(pinger, nodeConfiguration);
        nodeMainExecutor.execute(pinging, nodeConfiguration);
        ScheduledThreadPoolExecutor exec4 = new ScheduledThreadPoolExecutor(6);
        exec4.scheduleAtFixedRate(new Runnable() {
            public void run() {
                pinger.flag=true;
                pinging.received=false;

                //while(pinger.flag==true){}


                //while (pinging.received==false){}
                ping=pinging.timeEnd -pinger.timeStart;
                mGLView.updatePing(ping);

            }
        }, 0, 500000, TimeUnit.MICROSECONDS);*/



    }

    public void calculateCentroid(){
        meanCentroid[0]=0;
        meanCentroid[1]=0;
        meanCentroid[2]=0;


        for (int i=0;i<maxBots;i++) {
            if (turtleList[i].getOn()==1){
                meanCentroid[0]=meanCentroid[0]+turtleList[i].getX();
                meanCentroid[1]=meanCentroid[1]+turtleList[i].getY();
                meanCentroid[2]=meanCentroid[2]+1;
            }
        }

        meanCentroid[0]=meanCentroid[0]/meanCentroid[2];
        meanCentroid[1]=meanCentroid[1]/meanCentroid[2];

    }
    public void clearAllValues(){
        for (int i=0;i<maxBots;i++){
            turtleList[i]=new turtle();
        }
        nodeMainExecutorService.shutdown();


        /*Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

        startActivity(i);*/
    }




}