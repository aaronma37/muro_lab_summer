package com.example.aaron.test;

/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import org.ros.android.MessageCallable;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;
import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.Point;



import org.apache.commons.logging.Log;


/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class poseView<T> implements NodeMain {

    public String messageType,frame_id;
    public int newMeasurementFlag;
    final int maxBots=100;
    public boolean gotgauss=false;
    public double x,y,z,k,w,id,i,j;
    public PoseStamped pose;
    public float poseData[]={0,0,0,0,0,0,0,0,0};
    public dummyPoseArray dummyArray;
    public int specifyTurtlebot=0;
    public float orientData[]={1,0,0,0};
    public boolean measured=false;
    public Point p;
    public boolean newRobot=false;
    public turtle turtleList[]=new turtle[maxBots];
    public turtle gauss = new turtle();

    public poseView() {
        this.y=1;
        this.z=1;
        this.x=1;
        this.k=1;
        this.w=1;
        this.i=0;
        this.j=0;
        messageType= geometry_msgs.PoseStamped._TYPE;
        for (int i=0;i<maxBots;i++){
            turtleList[i]=new turtle();
        }
        dummyArray= new dummyPoseArray();
    }

    public turtle[] getTurtles(){
        return turtleList;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public float[] getPoseData(){return poseData;}
    public double getZ(){
        return z;
    }
    public double getID(){return id;}
    public double getYaw(){return Math.atan2(2*(w*k),(w*w-k*k))*57.2957795;}
    public Point getP(){return p;}
    public void setX(double xx) {
        x=xx;
    }
    public void setY(double xx) {
        x=xx;
    }
    /*public void setMessageToStringCallable(MessageCallable<String, T> callable) {
        this.callable = callable;
    }*/

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("poseFinder");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<geometry_msgs.PoseStamped> subscriber = connectedNode.newSubscriber("poseEstimation", messageType);
        subscriber.addMessageListener(new MessageListener<PoseStamped>() {
            @Override
            public void onNewMessage(geometry_msgs.PoseStamped pose) {

                frame_id=pose.getHeader().getFrameId();

                if(!frame_id.equals("null")){
                    newMeasurementFlag=1;
                    /*w=pose.getPose().getOrientation().getW();
                    i=pose.getPose().getOrientation().getX();
                    j=pose.getPose().getOrientation().getY();
                    k=pose.getPose().getOrientation().getZ();*/
                    //id=(double)pose.getHeader().getSeq();

                    poseData[0]=(float)(pose.getPose().getPosition().getX());
                    poseData[1]=(float)pose.getPose().getPosition().getY();
                    poseData[2]=(float)pose.getPose().getPosition().getZ();
                    poseData[3]=0;//-(float)(Math.atan2(-2*(i*j-w*k), w*w+i*i-j*j-k*k)*57.2957795);
                    poseData[5]=0;
                    poseData[6]=1;
                    poseData[7]=0;//(float)((Math.atan2(-2*(j*k-w*i), w*w-i*i-j*j+k*k))*57.2957795);
                    poseData[8]=0;//(float)((Math.asin(2 * (i * k + w * j)))*57.2957795);
                    orientData[0]=(float)pose.getPose().getOrientation().getW();
                    orientData[1]=(float)pose.getPose().getOrientation().getX();
                    orientData[2]=(float)pose.getPose().getOrientation().getY();
                    orientData[3]=(float)pose.getPose().getOrientation().getZ();

                    newRobot=true;

                    /*if (frame_id.equals("Bob")){poseData[4]=0;}else if(frame_id.equals("Frank")){poseData[4]=1;}
                else if(frame_id.equals("Eric")){poseData[4]=4;}
                else if(frame_id.equals("Gypsy Danger")){poseData[4]=3;}
                else if(frame_id.equals("Typhoon")){poseData[4]=2;}
                else{poseData[4]=0;}*/


                    /*if (num==0){
                        num++;
                        poseData[4]=0;
                        newRobot=false;
                    }
                    else{
                        for (int i=0;i<num;i++){
                            if (turtleList[i].getIdentification().equals(frame_id)){
                                poseData[4]=i;
                                newRobot=false;
                            }
                        }
                    }
                    if (newRobot==true){
                        poseData[4]=num;
                        num++;
                    }*/

                    if (frame_id.equals("dummy 1")){poseData[4]=11;specifyTurtlebot=0;}
                    else if (frame_id.equals("titian")){poseData[4]=1;specifyTurtlebot=1;}
                    else if(frame_id.equals("michelangelo")){poseData[4]=2; specifyTurtlebot=2;}
                    else if(frame_id.equals("donatello")){poseData[4]=3;specifyTurtlebot=3;}
                    else if(frame_id.equals("raphael")){poseData[4]=4;specifyTurtlebot=4;}
                    else if(frame_id.equals("leonardo")){poseData[4]=5;specifyTurtlebot=5; }
                    else if(frame_id.equals("boticelli")){poseData[4]=6;specifyTurtlebot=6;}
                    else if(frame_id.equals("giotto")){poseData[4]=7;specifyTurtlebot=7;}
                    else if(frame_id.equals("bellini")){poseData[4]=8;specifyTurtlebot=8;}
                    else if(frame_id.equals("ghiberti")){poseData[4]=9;specifyTurtlebot=9;}
                    else if(frame_id.equals("masaccio")){poseData[4]=10;specifyTurtlebot=10;}
                    else if(frame_id.equals("picasso")){poseData[4]=41;specifyTurtlebot=41;}
                    else if(frame_id.equals("dali")){poseData[4]=42;specifyTurtlebot=42;}
                    else if(frame_id.equals("goya")){poseData[4]=43;specifyTurtlebot=43;}

                    else if(frame_id.equals("dummy 2")){poseData[4]=12;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 3")){poseData[4]=13;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 4")){poseData[4]=14;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 5")){poseData[4]=15;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 6")){poseData[4]=16;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 7")){poseData[4]=17;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 8")){poseData[4]=18;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 9")){poseData[4]=19;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 10")){poseData[4]=20;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 11")){poseData[4]=21;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 12")){poseData[4]=22;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 13")){poseData[4]=23;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 14")){poseData[4]=24;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 15")){poseData[4]=25;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 16")){poseData[4]=26;specifyTurtlebot=0;}
                    else if(frame_id.equals("dummy 17")){poseData[4]=27;specifyTurtlebot=0;}
                    else if(frame_id.equals("gauss")){poseData[4]=-1;}
                    else if(frame_id.equals("obstacle")){poseData[4]=49;}
                    else{poseData[4]=0;}

                    if (pose.getHeader().getStamp().toSeconds()>5){
                        measured=false;
                    }
                    else {
                        measured=true;
                    }



                    if (poseData[4]!=-1){
                        if (orientData[0]==-1){
                            if (poseData[1]!=0 && poseData[0]!=0){
                                dummyArray.pose[(int)poseData[4]].x=poseData[0];
                                dummyArray.pose[(int)poseData[4]].y=-poseData[1];
                                dummyArray.pose[(int)poseData[4]].active=true;
                            }

                        }else{
                            turtleList[(int)poseData[4]].setData(poseData,frame_id,specifyTurtlebot, measured);
                            turtleList[(int)poseData[4]].setOrient(orientData);
                            if(poseData[4]==10){
                                System.out.println("\n\nOrientation W: " + orientData[0]+"\n");
                                System.out.println("\nX: " + poseData[0]+"\n");

                            }
                            gotgauss=false;
                        }
                    }
                    else {
                        //gauss.setData(poseData,frame_id,0);
                        //gotgauss=true;
                    }

                }



            }
        }, 10);
    }

    @Override
    public void onShutdown(Node node) {
    }

    @Override
    public void onShutdownComplete(Node node) {
    }

    @Override
    public void onError(Node node, Throwable throwable) {
    }
}