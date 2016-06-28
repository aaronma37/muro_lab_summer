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


import android.preference.PreferenceActivity;

import org.ros.concurrent.CancellableLoop;
import org.ros.internal.message.DefaultMessageFactory;
import org.ros.message.MessageFactory;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;


import java.util.Random;

import geometry_msgs.Point;
import geometry_msgs.Pose;
import std_msgs.Header;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class dummyMaker extends AbstractNodeMain {

    public double num=5;
    public int flag=0;
    private geometry_msgs.Point p;
    private MessageFactory messageFactory;
    public double x,y;
    public int type=1;  // 1 Denotes dummy for simulation, >40 Denotes obstacle ==100 Denotes clear EKF, 301 obstacles line segments
    public tf2_msgs.TFMessage pose;
    public geometry_msgs.TransformStamped tf;
    public float xx=0;
    public float yy=0;
    public float ww=0;
    public float zz=0;
    public float size=0;
    public float xxArray[] = new float[50];
    public float yyArray[] = new float[50];
    public float wwArray[] = new float[50];
    public float zzArray[] = new float[50];



    Node node;

    public dummyMaker(double numl){
        x=0;
        y=0;
        num=numl;

        for(int i=0;i<50;i++){
            xxArray[i]=0;
            yyArray[i]=0;
            wwArray[i]=0;
            zzArray[i]=0;
        }
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("idk");
    }


    public void setNum(double numl){
        num=numl;
    }

    public void setPoint(float xx, float yy){
        x=xx; y=yy;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        final Publisher<tf2_msgs.TFMessage> publisher =
                connectedNode.newPublisher("/tf", pose._TYPE);

        final Publisher<geometry_msgs.TransformStamped> publisher2 =
                connectedNode.newPublisher("/tf", tf._TYPE);

        // This CancellableLoop will be canceled automatically when the node shuts
        // down.
        connectedNode.executeCancellableLoop(new CancellableLoop() {


            @Override
            protected void setup() {
                //num= 0;
            }

            @Override
            protected void loop() throws InterruptedException {

                if (flag==1){
                    System.out.println("type" + type);

                    if(type!=301){

                        tf2_msgs.TFMessage pose = publisher.newMessage();

                        geometry_msgs.TransformStamped tf = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.TransformStamped._TYPE);
                        if (type<11 || (type>40 && type<44)){
                            tf.getTransform().getTranslation().setX(xx);
                            tf.getTransform().getTranslation().setY(yy);
                            tf.getTransform().getTranslation().setZ(zz);
                        }
                        else{
                            tf.getTransform().getTranslation().setX(xx);
                            tf.getTransform().getTranslation().setY(yy);
                            tf.getTransform().getTranslation().setZ(zz);
                            tf.getTransform().getRotation().setY(ww);
                        }

                        //tf.getTransform().getRotation().setX(0);
                        //tf.getTransform().getRotation().setY(0);
                        //tf.getTransform().getRotation().setZ(0);
                        tf.getTransform().getRotation().setW(type);
                        tf.getHeader().setFrameId("ORB_SLAM/World");
                        tf.getHeader().setSeq(1);
                        tf.setChildFrameId("Dummy");

                        pose.getTransforms().add(tf);
                        pose.getTransforms().set(0,tf);

                        publisher.publish(pose);
                        flag=0;


                    }else{


                        tf2_msgs.TFMessage pose = publisher.newMessage();
                        geometry_msgs.TransformStamped tf[] = new  geometry_msgs.TransformStamped[50];
//                        geometry_msgs.TransformStamped tf = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.TransformStamped._TYPE);


                        for (int i=0;i<size;i++){
                            tf[i] = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.TransformStamped._TYPE);

                            pose.getTransforms().add(tf[i]);
                            pose.getTransforms().set(i, tf[i]);
                            pose.getTransforms().get(i).getTransform().getTranslation().setX(xxArray[i]);
                            pose.getTransforms().get(i).getTransform().getTranslation().setY(yyArray[i]);
                            pose.getTransforms().get(i).getTransform().getTranslation().setZ(zzArray[i]);
                            pose.getTransforms().get(i).getTransform().getRotation().setY(wwArray[i]);
                            pose.getTransforms().get(i).getTransform().getRotation().setW(type);
                            pose.getTransforms().get(i).getTransform().getRotation().setX(size);
                            pose.getTransforms().get(i).getHeader().setFrameId("ORB_SLAM/World");
                            pose.getTransforms().get(i).getHeader().setSeq(1);
                            pose.getTransforms().get(i).setChildFrameId("Dummy");

                        }


                        publisher.publish(pose);
                        flag=0;

                    }


                }

               Thread.sleep(20);
            }
        });
    }
}
