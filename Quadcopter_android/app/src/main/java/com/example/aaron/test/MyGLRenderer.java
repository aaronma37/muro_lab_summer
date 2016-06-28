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

        import javax.microedition.khronos.egl.EGLConfig;
        import javax.microedition.khronos.opengles.GL10;

        import android.content.Context;
        import android.opengl.GLES20;
        import android.opengl.GLSurfaceView;
        import android.opengl.Matrix;
        import android.util.Log;

/*        import org.ros.node.ConnectedNode;
        import org.ros.node.DefaultNodeFactory;
        import org.ros.node.Node;
        import org.ros.node.NodeConfiguration;
        import org.ros.node.NodeFactory;*/

        import com.google.common.collect.BiMap;

        import java.math.BigDecimal;
        import java.nio.FloatBuffer;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Map;
        import java.util.Set;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private static final int maxObstacleSize = 50;
    private Square mArena2;
    final int maxBots=50;
    final int gMaxNum=5;
    private selection selected,localizationSignal;
    private scalevis scaleVis;
    private interfaceImage interfacePull;
    private Square vLine[] = new Square[25];
    private Triangle vTest;

    private Square fLine[] = new Square[100];
    private Square oLine[] = new Square[100];
    private Square gLine[] = new Square[100];
    private Square bLine[] = new Square[100];
    private Square gLine2[] = new Square[100];
    private Square pathLine[]=new Square[100];
    private boolean voronoi_deployoment_disable=false;
    private boolean formation_deployment_disable=true;
    private boolean gen_disable=false;



    private boolean flag=false;
    private pointXY intersections[] = new pointXY[50];
    private pointXY cellBoundaries[] = new pointXY[50];
    private vertexArray uniqueVoronoiVertices = new vertexArray();
    private vertexArray voronoiCellBoundaries[] = new vertexArray[maxBots];
    private vertexArray tempArrayInsertion = new vertexArray();
    private vertexArray tempArray= new vertexArray();
    private pointXY obstacleLinePoints[] = new pointXY[50];
    private ArrayList<pointXY> shortestPaths[]= new ArrayList[maxBots];
    private float insertionX=0f;
    private float insertionY=0f;
    private float insertionAngle=0f;
    private float tempVX;
    private float tempMax;
    private float tempVY;
    Map<Integer ,pointXY> obstacleMapPoints = new HashMap<Integer,pointXY>();


    public int adjacencyMatrix[][][] = new int[maxBots][maxObstacleSize*2+2][maxObstacleSize*2+2]; //+2 is for goal and turtle bot location


    //FOR SURFACE PLOT

    private float geoDistance[][]= new float[100][100];
    private distanceColorClass geoDistanceImage;







    public obstacleLine tempobstacleLine = new obstacleLine();
    public bEquation bEquations[]= new bEquation[50];
    public bLinePairs pair1= new bLinePairs();
    public bEquation voronoiEquations = new bEquation();
    private pointXY temptPoint;
    public ArrayList<obstacleLine> obstacleLineList = new ArrayList<obstacleLine>();
    private double cd, cy;
    private float tY,tX;
    private float lastPathCoordinateX=0;
    private float lastPathCoordinateY=0;
    public  dummyPoseArray pathArray = new dummyPoseArray();
    public  dummyPoseArray centroids = new dummyPoseArray();
    private waypoint wp,wpg;
    public block   rotation_block, scale_block, center_block, straight_arrow, curved_arrow;

    private gauss gg;
    private grid myGrid;
    private float dheight = -.3f;
    private float vheight = -.125f;
    private float infoheight= -.23f;
    private float gaussTextheight= .48f;
    private float formationTextheight=-.23f;

    private turtB turt1[] = new turtB[50];
    public turtB formationLocationImage,yellowGlow;
    public formationLocations formation_locations, relative_formation_locations;
    private ardroneImage myAr;
    private target tar;
    private origin Origin;
    public buttons plus, minus, arrows, clear, clearAll, commit;
    public fieldRange field, fieldOutline;
    public float firstFreeLinePointX=0;
    public float firstFreeLinePointY=0;
    public float field1X=0;
    public float field1Y=0f;
    //public float field1Y=.06f;
    public float field2X=0;
    public float field2Y=.02f;
    public float dial1=.5f;
    public float dial2=.2f;
    public float dial3=.5f;
    private gauss density;
    public float slider=0;
    //private ArrayList<textclass> textSystem= new ArrayList<textclass>();
    private textclass textSystem;
    public toggles vorToggle, freeDrawToggle,wayPointToggle,exit,ardronePrefToggle, ardroneAddToggle, gaussToggle,temptoggle,voronoiDeploymentToggle, dragToggle, swarmToggle, centroidTrackingOption,resetToggle, directionalDrag, addTurtle, gaussianPathToggle, gaussPathPlay;
    public toggles bar, dial, bar_2, dial_2, bar_3, dial_3, dragRobotToggle, freeBoundarytoggle, gaussianTrackToggle, obstacleLineToggle, addToggle, subtractToggle;
    public toggles formation_scale, formation, formationToggleTransform, customFormation;
    public toggles gaussOutline;
    public algorithms centroidTrackingEquation, lloyds;
    private obstacle circ;
    private float textPosition[]= {-.95f, .5f};
    public ArrayList<toText> textList = new ArrayList<toText>();
    public ArrayList<toText> textListARINFO = new ArrayList<toText>();
    public ArrayList<toText> textListSINFO = new ArrayList<toText>();
    public ArrayList<toText> textListGaussian = new ArrayList<toText>();
    public ArrayList<toText> textListFormation = new ArrayList<toText>();

    public boolean SINFO_FLAG=true;
    private FloatBuffer textureBuffer;
    public Context context;

    private boolean isPressed = false;

    private int g = 1;
    private boolean gaussFlag = false;

    private int vToggle=0;
    public int fToggle=0;
    private int APToggle=0;
    public float scale=1.5f;
    public float dragX=.5f;
    public float DRAGX=0f;
    public float dragY=-.35f;
    public float DRAGY=-.4f;
    public float ping=0;
    private float sX = 1f;
    private float sY = 1f;
    private float sZ = 1f;
    public int gToggle=0;
    private int gToggle2=0;
    private int gpToggle=0;
    public GaussPoint gaussPoint[]=new GaussPoint[100];
    private int pToggle=0;
    private int pToggle2=0;
    private int framecounter=0;
    public int tToggle=0;
    //public Node node;
    private float pX=0;
    private float pY=0;
    private int vSize=0;
    private int gpSize=0;
    float Coords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f }; // top right
    public int ardroneTextBegin=0;
    private int ardroneTextEnd=0;
    public int fSize=0;
    public int bSize=0;
    public int oSize=0;
    static private float newline=-.05f;
    static private float tab    =-.2f;
    private float texture[] = {
            0.0f, 1.0f,     // top left     (V2)
            0.0f, 0.0f,     // bottom left  (V1)
            1.0f, 1.0f,     // top right    (V4)
            1.0f, 0.0f      // bottom right (V3)
    };

    //gauss[] gList = new gauss[15];

    public gauss gaussArrayList;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] stockMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private final float[] zeroRotationMatrix = new float[16];
    float[] scratch = new float[16];
    float[] scratch2 = new float[16];
    float poseData[]={0,0,0,0,0};
    public turtle turtleList[]= new turtle[maxBots];
    private float tempX;
    private float tempY;
    private float width,height;

    private float mAngle;

    public MyGLRenderer(Context context1,float f[], turtle t[],float w, float h) {
        width = w;
        height = h;
        poseData = f;
        context = context1;

        //SET STATIC MATRICES


        for (int i = 0; i < maxBots; i++) {
            turtleList[i] = new turtle();
            if (t[i] != null) {
                turtleList[i].setData(t[i].getData(), t[i].getIdentification(), t[i].getType(), t[i].measured);
            }
            voronoiCellBoundaries[i]=new vertexArray();
        }

        for (int i=0;i<maxBots;i++){
            for (int j=0;j<maxObstacleSize+2;j++){
                for (int k=0;k<maxObstacleSize+2;k++){
                    adjacencyMatrix[i][j][k]=0;
                }
            }
            shortestPaths[i]=new ArrayList<pointXY>();
        }





    }


    public void updateRen(turtle t[]){
        for (int i=0;i<maxBots;i++){
                turtleList[i].setData(t[i].getData(), t[i].getIdentification(), t[i].getType(),t[i].measured);}
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


        float sTemp[] = {
                -0.5f,  0.5f, 0.0f,   // top left
                -0.5f, -0.5f, 0.0f,   // bottom left
                0.5f, -0.5f, 0.0f,   // bottom right
                0.5f,  0.5f, 0.0f };
        float c[] = { 0f,255f,255f, 1.0f };
        Origin    = new origin(context);
        sTemp[0]=-width/height;sTemp[1]=height/height;
        sTemp[3]=-width/height;;sTemp[4]=-height/(height*2);
        sTemp[6]=width/height;;sTemp[7]=-height/(height*2);
        sTemp[9]=width/height;sTemp[10]=height/height;

        sTemp[0]=-(width-100)/height;sTemp[1]=(height-5)/height;
        sTemp[3]=-(width-100)/height;sTemp[4]=-(height-10)/(height*2);
        sTemp[6]=(width-100)/height;;sTemp[7]=-(height-10)/(height*2);
        sTemp[9]=(width-100)/height;sTemp[10]=(height-5)/height;

        c[0]=255;c[1]=255;c[2]=255;c[3]=.2f;

        mArena2  = new Square(sTemp);



        wp= new waypoint(context,0);
        rotation_block= new block(context,0);
        scale_block= new block(context,0);
        center_block= new block(context,0);
        straight_arrow= new block(context,1);
        curved_arrow= new block(context,1);



        wpg = new waypoint(context,1);
        myAr=new ardroneImage(context);
        scaleVis=new scalevis(context);
        selected=new selection(context,0);
        localizationSignal = new selection(context,1);
        mArena2.setColor(c);

        plus= new buttons(context,0);
        minus= new buttons(context,1);
        arrows= new buttons(context,2);
        circ = new obstacle(context);

        lloyds = new algorithms(context,0,width,height,dheight);
        centroidTrackingEquation = new algorithms(context,1,width,height,dheight);



        float spriteCoords[] = {
                -0.05f,  0.05f,   // top left
                -0.05f, -0.05f,   // bottom left
                0.05f, -0.05f,   // bottom right
                0.05f,  0.05f}; //top right

        spriteCoords[0]=-(width-115)/(height*2)-.15f;spriteCoords[1]=-.75f;
        spriteCoords[2]=-(width-115)/(height*2)-.15f;spriteCoords[3]=-.85f;
        spriteCoords[4]=-(width-115)/(height*2)-.05f;spriteCoords[5]=-.85f;
        spriteCoords[6]=-(width-115)/(height*2)-.05f;spriteCoords[7]=-.75f;

        commit = new buttons(context,spriteCoords,5,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3]);
        commit.blocked=true;

        spriteCoords[0]=-(width-115)/(height*2)-.15f;spriteCoords[1]=-.75f;
        spriteCoords[2]=-(width-115)/(height*2)-.15f;spriteCoords[3]=-.85f;
        spriteCoords[4]=-(width-115)/(height*2)-.05f;spriteCoords[5]=-.85f;
        spriteCoords[6]=-(width-115)/(height*2)-.05f;spriteCoords[7]=-.75f;


        field = new fieldRange(context,0);

        fieldOutline = new fieldRange(context,1);



        spriteCoords[0]=-(width-115)/(height*2)-.4f;spriteCoords[1]=-.75f;
        spriteCoords[2]=-(width-115)/(height*2)-.4f;spriteCoords[3]=-.85f;
        spriteCoords[4]=-(width-115)/(height*2)-.2f;spriteCoords[5]=-.85f;
        spriteCoords[6]=-(width-115)/(height*2)-.2f;spriteCoords[7]=-.75f;

        clear = new buttons(context, spriteCoords, 3, spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3]);
        clear.blocked=true;

        spriteCoords[0]=-(width-115)/(height*2)-.33f;spriteCoords[1]=-.75f;
        spriteCoords[2]=-(width-115)/(height*2)-.33f;spriteCoords[3]=-.85f;
        spriteCoords[4]=-(width-115)/(height*2)-.23f;spriteCoords[5]=-.85f;
        spriteCoords[6]=-(width-115)/(height*2)-.23f;spriteCoords[7]=-.75f;

        addToggle = new toggles(context,spriteCoords,27,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);
        addToggle.blocked=true;

        spriteCoords[0]=-(width-115)/(height*2)-.44f;spriteCoords[1]=-.75f;
        spriteCoords[2]=-(width-115)/(height*2)-.44f;spriteCoords[3]=-.85f;
        spriteCoords[4]=-(width-115)/(height*2)-.34f;spriteCoords[5]=-.85f;
        spriteCoords[6]=-(width-115)/(height*2)-.34f;spriteCoords[7]=-.75f;

        subtractToggle = new toggles(context,spriteCoords,28,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);
        subtractToggle.blocked=true;





        spriteCoords[0]=-(width-115)/(height*2)-.75f;spriteCoords[1]=-.75f;
        spriteCoords[2]=-(width-115)/(height*2)-.75f;spriteCoords[3]=-.85f;
        spriteCoords[4]=-(width-115)/(height*2)-.45f;spriteCoords[5]=-.85f;
        spriteCoords[6]=-(width-115)/(height*2)-.45f;spriteCoords[7]=-.75f;

        clearAll = new buttons(context, spriteCoords,4,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3]);

        spriteCoords[0]=-(width-115)/(height*2)-.75f;spriteCoords[1]=-.6f;
        spriteCoords[2]=-(width-115)/(height*2)-.75f;spriteCoords[3]=-.7f;
        spriteCoords[4]=-(width-115)/(height*2)-.45f;spriteCoords[5]=-.7f;
        spriteCoords[6]=-(width-115)/(height*2)-.45f;spriteCoords[7]=-.6f;

        resetToggle = new toggles(context, spriteCoords,12,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);

        spriteCoords[0]=-.0198f;spriteCoords[1]=.0198f;
        spriteCoords[2]=-.0198f;spriteCoords[3]=-.0198f;
        spriteCoords[4]=.0198f;spriteCoords[5]=-.0198f;
        spriteCoords[6]=.0198f;spriteCoords[7]=.0198f;
        geoDistanceImage = new distanceColorClass(context, spriteCoords,1,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3]);







        for (int i=0;i<50;i++){
            turt1[i] = new turtB(context,i);
        }
        formationLocationImage = new turtB(context,-1);
        yellowGlow = new turtB(context,-2);



        for (int i=0;i<50;i++){
            bEquations[i]=new bEquation();
        }

        for (int i=0;i<100;i++){

            for(int j=0;j<100;j++){

                geoDistance[i][j]=0;
            }

        }





        tar =new target(context);

        sTemp[0]=-(width-100)/height;sTemp[1]=0;
        sTemp[3]=-(width-100)/height;sTemp[4]=-.01f;
        sTemp[6]=(width-100)/height;;sTemp[7]=-.01f;
        sTemp[9]=(width-100)/height;sTemp[10]=0;
        c[0]=0f;c[1]=1f;c[2]=1f;c[3]=1f;
        for (int i=0;i<25;i++) {
            vLine[i] = new Square(sTemp);
        }
        vTest  = new Triangle();

        c[0]=1f;c[1]=0f;c[2]=0f;c[3]=1f;

        for (int i=0;i<100;i++) {
            oLine[i] = new Square(sTemp);
            oLine[i].setColor(c);
        }

        for (int i=0;i<100;i++) {
            bLine[i] = new Square(sTemp);
            pathLine[i]=new Square(sTemp);
        }

        for (int i=0;i<100;i++) {
            fLine[i] = new Square(sTemp);
            c[0]=255;c[1]=255;c[2]=255;c[3]=.2f;
            fLine[i].setColor(c);
            c[0]=255;c[1]=255;c[2]=255;c[3]=.2f;
            gLine[i] = new Square(sTemp);
            gLine2[i] = new Square(sTemp);
            gLine[i].setColor(c);
            gLine2[i].setColor(c);
            gaussPoint[i]= new GaussPoint();
        }

        for (int i = 0; i < 50; i++) {
            intersections[i]=new pointXY();
            cellBoundaries[i]=new pointXY();
            obstacleLinePoints[i]=new pointXY();

        }



        voronoiEquations.m[0]=0;
        voronoiEquations.x[0]=-1f;
        voronoiEquations.y[0]=1;
        voronoiEquations.x2[0]=-1f;
        voronoiEquations.y2[0]=-1;

        voronoiEquations.m[1]=0;
        voronoiEquations.x[1]=-1f;
        voronoiEquations.y[1]=-1;
        voronoiEquations.x2[1]=1;
        voronoiEquations.y2[1]=-1;

        voronoiEquations.m[2]=0;
        voronoiEquations.x[2]=1;
        voronoiEquations.y[2]=-1;
        voronoiEquations.x2[2]=1;
        voronoiEquations.y2[2]=1;

        voronoiEquations.m[3]=0;
        voronoiEquations.x[3]=1;
        voronoiEquations.y[3]=1;
        voronoiEquations.x2[3]=-1f;
        voronoiEquations.y2[3]=1;



        spriteCoords[0]=-(width-115)/(height*2)-.11f;spriteCoords[1]=(height)/(height)+vheight;
        spriteCoords[2]=-(width-115)/(height*2)-.11f;spriteCoords[3]=(height)/(height)+vheight-.1f;
        spriteCoords[4]=-(width-115)/(height*2)-.01f;spriteCoords[5]=(height)/(height)+vheight-.1f;
        spriteCoords[6]=-(width-115)/(height*2)-.01f;spriteCoords[7]=(height)/(height)+vheight;
        vorToggle = new toggles(context,spriteCoords,0,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.22f;
        spriteCoords[2]=-(width-115)/(height*2)-.22f;
        spriteCoords[4]=-(width-115)/(height*2)-.12f;
        spriteCoords[6]=-(width-115)/(height*2)-.12f;
        ardronePrefToggle = new toggles(context, spriteCoords,5,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);



        spriteCoords[0]=-(width-115)/(height*2)-.44f;
        spriteCoords[2]=-(width-115)/(height*2)-.44f;
        spriteCoords[4]=-(width-115)/(height*2)-.34f;
        spriteCoords[6]=-(width-115)/(height*2)-.34f;
        temptoggle = new toggles(context, spriteCoords, 7,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.44f;
        spriteCoords[2]=-(width-115)/(height*2)-.44f;
        spriteCoords[4]=-(width-115)/(height*2)-.34f;
        spriteCoords[6]=-(width-115)/(height*2)-.34f;
        dragRobotToggle = new toggles(context, spriteCoords, 18,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.55f;
        spriteCoords[2]=-(width-115)/(height*2)-.55f;
        spriteCoords[4]=-(width-115)/(height*2)-.45f;
        spriteCoords[6]=-(width-115)/(height*2)-.45f;
        ardroneAddToggle = new toggles(context, spriteCoords,6,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);


        spriteCoords[0]=-(width-115)/(height*2)-.66f;
        spriteCoords[2]=-(width-115)/(height*2)-.66f;
        spriteCoords[4]=-(width-115)/(height*2)-.56f;
        spriteCoords[6]=-(width-115)/(height*2)-.56f;
        addTurtle = new toggles(context, spriteCoords, 14,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);


        spriteCoords[0]=-(width-115)/(height*2)-.33f;
        spriteCoords[2]=-(width-115)/(height*2)-.33f;
        spriteCoords[4]=-(width-115)/(height*2)-.23f;
        spriteCoords[6]=-(width-115)/(height*2)-.23f;
        dragToggle = new toggles(context, spriteCoords, 20,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.77f;
        spriteCoords[2]=-(width-115)/(height*2)-.77f;
        spriteCoords[4]=-(width-115)/(height*2)-.67f;
        spriteCoords[6]=-(width-115)/(height*2)-.67f;
        gaussianTrackToggle = new toggles(context, spriteCoords, 22,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.55f;spriteCoords[1]=(height)/(height)+dheight;
        spriteCoords[2]=-(width-115)/(height*2)-.55f;spriteCoords[3]=(height)/(height)+dheight-.1f;
        spriteCoords[4]=-(width-115)/(height*2)-.45f;spriteCoords[5]=(height)/(height)+dheight-.1f;
        spriteCoords[6]=-(width-115)/(height*2)-.45f;spriteCoords[7]=(height)/(height)+dheight;
        freeDrawToggle = new toggles(context, spriteCoords,1,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.22f;
        spriteCoords[2]=-(width-115)/(height*2)-.22f;
        spriteCoords[4]=-(width-115)/(height*2)-.12f;
        spriteCoords[6]=-(width-115)/(height*2)-.12f;
        wayPointToggle = new toggles(context, spriteCoords,2,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.33f;
        spriteCoords[2]=-(width-115)/(height*2)-.33f;
        spriteCoords[4]=-(width-115)/(height*2)-.23f;
        spriteCoords[6]=-(width-115)/(height*2)-.23f;
        gaussToggle = new toggles(context, spriteCoords, 3,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);



        spriteCoords[0]=-(width-115)/(height*2)-.11f;
        spriteCoords[2]=-(width-115)/(height*2)-.11f;
        spriteCoords[4]=-(width-115)/(height*2)-.01f;
        spriteCoords[6]=-(width-115)/(height*2)-.01f;
        voronoiDeploymentToggle = new toggles(context, spriteCoords, 8,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);



        spriteCoords[0]=-(width-115)/(height*2)-.66f;
        spriteCoords[2]=-(width-115)/(height*2)-.66f;
        spriteCoords[4]=-(width-115)/(height*2)-.56f;
        spriteCoords[6]=-(width-115)/(height*2)-.56f;
        gaussianPathToggle = new toggles(context, spriteCoords, 15,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.44f;
        spriteCoords[2]=-(width-115)/(height*2)-.44f;
        spriteCoords[4]=-(width-115)/(height*2)-.34f;
        spriteCoords[6]=-(width-115)/(height*2)-.34f;
        freeBoundarytoggle = new toggles(context, spriteCoords, 19,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.77f;
        spriteCoords[2]=-(width-115)/(height*2)-.77f;
        spriteCoords[4]=-(width-115)/(height*2)-.67f;
        spriteCoords[6]=-(width-115)/(height*2)-.67f;
        gaussPathPlay = new toggles(context, spriteCoords, 16,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.11f;spriteCoords[1]=(height)/(height)+(float)1.75*dheight;
        spriteCoords[2]=-(width-115)/(height*2)-.11f;spriteCoords[3]=(height)/(height)+(float)1.75*dheight-.1f;
        spriteCoords[4]=-(width-115)/(height*2)-.01f;spriteCoords[5]=(height)/(height)+(float)1.75*dheight-.1f;
        spriteCoords[6]=-(width-115)/(height*2)-.01f;spriteCoords[7]=(height)/(height)+(float)1.75*dheight;
        formation = new toggles(context, spriteCoords, 24,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],formation_deployment_disable);
        formation.val1=3;formation.val2=0;formation.val3=1; formation.val4=0;

        formation_locations= new formationLocations();
        relative_formation_locations= new formationLocations();

        relative_formation_locations.size=10;
        formation_locations.size=10;
        resetFormation();

        spriteCoords[0]=-(width-115)/(height*2)-.22f;spriteCoords[1]=(height)/(height)+(float)1.75*dheight;
        spriteCoords[2]=-(width-115)/(height*2)-.22f;spriteCoords[3]=(height)/(height)+(float)1.75*dheight-.1f;
        spriteCoords[4]=-(width-115)/(height*2)-.12f;spriteCoords[5]=(height)/(height)+(float)1.75*dheight-.1f;
        spriteCoords[6]=-(width-115)/(height*2)-.12f;spriteCoords[7]=(height)/(height)+(float)1.75*dheight;
        formationToggleTransform = new toggles(context, spriteCoords, 25,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],formation_deployment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.33f;spriteCoords[1]=(height)/(height)+(float)1.75*dheight;
        spriteCoords[2]=-(width-115)/(height*2)-.33f;spriteCoords[3]=(height)/(height)+(float)1.75*dheight-.1f;
        spriteCoords[4]=-(width-115)/(height*2)-.23f;spriteCoords[5]=(height)/(height)+(float)1.75*dheight-.1f;
        spriteCoords[6]=-(width-115)/(height*2)-.23f;spriteCoords[7]=(height)/(height)+(float)1.75*dheight;
        customFormation = new toggles(context, spriteCoords, 26,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],formation_deployment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.65f;spriteCoords[1]=(height)/(height)-.55f;
        spriteCoords[2]=-(width-115)/(height*2)-.65f;spriteCoords[3]=(height)/(height)-.55f-.04f;
        spriteCoords[4]=-(width-115)/(height*2)-.05f;spriteCoords[5]=(height)/(height)-.55f-.04f;
        spriteCoords[6]=-(width-115)/(height*2)-.05f;spriteCoords[7]=(height)/(height)-.55f;
        bar = new toggles(context, spriteCoords, 16,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.08f;spriteCoords[1]=(height)/(height)-.54f;
        spriteCoords[2]=-(width-115)/(height*2)-.08f;spriteCoords[3]=(height)/(height)-.54f-.06f;
        spriteCoords[4]=-(width-115)/(height*2)-.03f;spriteCoords[5]=(height)/(height)-.54f-.06f;
        spriteCoords[6]=-(width-115)/(height*2)-.03f;spriteCoords[7]=(height)/(height)-.54f;
        dial = new toggles(context, spriteCoords, 17,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);
        dial.active=true;

        spriteCoords[0]=-(width-115)/(height*2)-.65f;spriteCoords[1]=(height)/(height)-.70f;
        spriteCoords[2]=-(width-115)/(height*2)-.65f;spriteCoords[3]=(height)/(height)-.70f-.04f;
        spriteCoords[4]=-(width-115)/(height*2)-.05f;spriteCoords[5]=(height)/(height)-.70f-.04f;
        spriteCoords[6]=-(width-115)/(height*2)-.05f;spriteCoords[7]=(height)/(height)-.70f;
        bar_2 = new toggles(context, spriteCoords, 16,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.08f;spriteCoords[1]=(height)/(height)-.69f;
        spriteCoords[2]=-(width-115)/(height*2)-.08f;spriteCoords[3]=(height)/(height)-.69f-.06f;
        spriteCoords[4]=-(width-115)/(height*2)-.03f;spriteCoords[5]=(height)/(height)-.69f-.06f;
        spriteCoords[6]=-(width-115)/(height*2)-.03f;spriteCoords[7]=(height)/(height)-.69f;
        dial_2 = new toggles(context, spriteCoords, 17,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);
        dial_2.active=true;

        spriteCoords[0]=-(width-115)/(height*2)-.65f;spriteCoords[1]=(height)/(height)-.85f;
        spriteCoords[2]=-(width-115)/(height*2)-.65f;spriteCoords[3]=(height)/(height)-.85f-.04f;
        spriteCoords[4]=-(width-115)/(height*2)-.05f;spriteCoords[5]=(height)/(height)-.85f-.04f;
        spriteCoords[6]=-(width-115)/(height*2)-.05f;spriteCoords[7]=(height)/(height)-.85f;
        bar_3 = new toggles(context, spriteCoords, 16,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.08f;spriteCoords[1]=(height)/(height)-.84f;
        spriteCoords[2]=-(width-115)/(height*2)-.08f;spriteCoords[3]=(height)/(height)-.84f-.06f;
        spriteCoords[4]=-(width-115)/(height*2)-.03f;spriteCoords[5]=(height)/(height)-.84f-.06f;
        spriteCoords[6]=-(width-115)/(height*2)-.03f;spriteCoords[7]=(height)/(height)-.84f;
        dial_3 = new toggles(context, spriteCoords, 17,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);
        dial_3.active=true;


        spriteCoords[0]=-.135f;spriteCoords[1]=.135f;
        spriteCoords[2]=-.135f;spriteCoords[3]=-.135f;
        spriteCoords[4]=.135f;spriteCoords[5]=-.135f;
        spriteCoords[6]=.135f;spriteCoords[7]=.135f;
        gaussOutline = new toggles(context, spriteCoords, 21,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);






        spriteCoords[0]=-(width-115)/(height*2)-.55f;
        spriteCoords[2]=-(width-115)/(height*2)-.55f;
        spriteCoords[4]=-(width-115)/(height*2)-.25f;
        spriteCoords[6]=-(width-115)/(height*2)-.25f;
        spriteCoords[1]=(height)/(height)+dheight-.3f;
        spriteCoords[3]=(height)/(height)+dheight-.6f;
        spriteCoords[5]=(height)/(height)+dheight-.6f;
        spriteCoords[7]=(height)/(height)+dheight-.3f;
        swarmToggle = new toggles(context, spriteCoords, 9,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.55f;
        spriteCoords[2]=-(width-115)/(height*2)-.55f;
        spriteCoords[4]=-(width-115)/(height*2)-.25f;
        spriteCoords[6]=-(width-115)/(height*2)-.25f;
        spriteCoords[1]=(height)/(height)+dheight-.3f;
        spriteCoords[3]=(height)/(height)+dheight-.6f;
        spriteCoords[5]=(height)/(height)+dheight-.6f;
        spriteCoords[7]=(height)/(height)+dheight-.3f;
        obstacleLineToggle = new toggles(context, spriteCoords, 23,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],voronoi_deployoment_disable);

        spriteCoords[0]=-(width-115)/(height*2)-.55f;
        spriteCoords[2]=-(width-115)/(height*2)-.55f;
        spriteCoords[4]=-(width-115)/(height*2)-.25f;
        spriteCoords[6]=-(width-115)/(height*2)-.25f;
        spriteCoords[1]=(height)/(height)+dheight-.3f;
        spriteCoords[3]=(height)/(height)+dheight-.6f;
        spriteCoords[5]=(height)/(height)+dheight-.6f;
        spriteCoords[7]=(height)/(height)+dheight-.3f;

        centroidTrackingOption= new toggles(context,spriteCoords,10,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);
        centroidTrackingOption.active=true;
        directionalDrag= new toggles(context,spriteCoords,13,spriteCoords[4],spriteCoords[0],spriteCoords[1],spriteCoords[3],gen_disable);


        spriteCoords[0]=-(width-115)/(height*2);spriteCoords[1]=(height)/(height);
        spriteCoords[2]=-(width-115)/(height*2);spriteCoords[3]=-(height)/(height);
        spriteCoords[4]=-(width-115)/(height*2)+.05f;spriteCoords[5]=-(height)/(height);
        spriteCoords[6]=-(width-115)/(height*2)+.05f;spriteCoords[7]=(height)/(height);
        interfacePull = new interfaceImage(context,spriteCoords);

        textSystem = new textclass(context, "A");
        textList.add(new toText(-.85f,.22f,0,"Multi-Agent Robotics Lab",0,1));
        textList.add(new toText(-.85f,1f,0,"Scale: "+scale+"x",0,1));
        textList.add(new toText(-.78f,.94f,0,2/scale+" ft",0,1));

        textList.add(new toText((height)/(height)-.1f,-(width-115)/(height*2)-.03f,0,"Options",1,1));
        textList.add(new toText((height)/(height)-.275f,-(width-115)/(height*2)-.03f,0,"Deployment",1,1));

        textListARINFO.add(new toText(infoheight,-(width-115)/(height*2)-.05f,0,"No Robots Selected",1,1));
        textListARINFO.add(new toText(infoheight+newline,-(width-115)/(height*2)-.05f,0,"Battery: "+scale+"x",1,1));
        textListARINFO.add(new toText(infoheight+newline*2,-(width-115)/(height*2)-.05f,0," X:",1,1));
        textListARINFO.add(new toText(infoheight+newline*2,-(width-115)/(height*2)-.05f+tab,0," Y:",1,1));
        textListARINFO.add(new toText(infoheight+newline*2,-(width-115)/(height*2)-.05f+tab*2,0," Z:",1,1));
        textListARINFO.add(new toText(infoheight+newline*3,-(width-115)/(height*2)-.05f,0,"Vx:",1,1));
        textListARINFO.add(new toText(infoheight+newline*3,-(width-115)/(height*2)-.05f+tab,0,"Vy:",1,1));
        textListARINFO.add(new toText(infoheight+newline*3,-(width-115)/(height*2)-.05f+tab*2,0,"Vz:",1,1));

        textListSINFO.add(new toText(infoheight,-(width-115)/(height*2)-.05f,0,"Standard Output",1,1));
        textListSINFO.add(new toText(infoheight+newline,-(width-115)/(height*2)-.05f,0,"Connection: DISCONNECTED" ,1,1));
        textListSINFO.add(new toText(infoheight+newline*2,-(width-115)/(height*2)-.05f,0,"Network: Undetected",1,1));
        textListSINFO.add(new toText(infoheight+newline*3,-(width-115)/(height*2)-.05f,0,"Deployment: None",1,1));

        textListGaussian.add(new toText(gaussTextheight,-(width-115)/(height*2)-.05f,0,"K term",1,1));
        textListGaussian.add(new toText(gaussTextheight+newline,-(width-115)/(height*2)-.7f,0,""+dial1 ,1,1));
        textListGaussian.add(new toText(gaussTextheight+newline*3f,-(width-115)/(height*2)-.05f,0,"C term" ,1,1));
        textListGaussian.add(new toText(gaussTextheight+newline*4f,-(width-115)/(height*2)-.7f,0,""+dial2 ,1,1));
        textListGaussian.add(new toText(gaussTextheight+newline*6f,-(width-115)/(height*2)-.05f,0,"Gaussian Size" ,1,1));
        textListGaussian.add(new toText(gaussTextheight+newline*7f,-(width-115)/(height*2)-.7f,0,""+dial3 ,1,1));

        textListFormation.add(new toText(formationTextheight,-(width-115)/(height*2)-.05f,0,"Formation information: " ,1,1));
        textListFormation.add(new toText(formationTextheight+newline,-(width-115)/(height*2)-.05f,0,"Active Robots: " ,1,1));
        textListFormation.add(new toText(formationTextheight+2*newline,-(width-115)/(height*2)-.05f,0,"Type: " ,1,1));
        textListFormation.add(new toText(formationTextheight+3*newline,-(width-115)/(height*2)-.05f,0,"Scale: " ,1,1));
        textListFormation.add(new toText(formationTextheight+4*newline,-(width-115)/(height*2)-.05f,0,"Rotation: " ,1,1));






        gaussArrayList=new gauss(context);





        sTemp[0]=-(width-100)/height;sTemp[1]=(height-5)/height;
        sTemp[3]=-(width-100)/height;sTemp[4]=-(height-10)/(height*2);
        sTemp[6]=(width-100)/height;;sTemp[7]=-(height-10)/(height*2);
        sTemp[9]=(width-100)/height;sTemp[10]=(height-5)/height;
        myGrid = new grid(context,sTemp);

    //SET MATRICES



    }

    public void setVoronoiCoordinates(float s[],int i,int j,double x1, double y1, double x2, double y2){
        vLine[i].setSquareCoords(s);
        vSize=j;
        voronoiEquations.m[i+4]=(float)(y2-y1)/(float)(x2-x1);
        voronoiEquations.x[i+4]=(float)x1;
        voronoiEquations.y[i+4]=(float)y1;
        voronoiEquations.x2[i+4]=(float)x2;
        voronoiEquations.y2[i+4]=(float)y2;
    }



    public void setPosition(float f[]) {
        poseData = f;
    }

    @Override
    public void onDrawFrame(GL10 unused) {

/*        if (obstacleLineList.isEmpty()==false){
            for (int i=0;i<obstacleLineList.size();i++){
                for (int j=0;j<maxBots;j++){
                    if(turtleList[j].on==1){
                        nearestPoint(obstacleLineList.get(i),turtleList[j].x, turtleList[j].y,i,j);
                    }
                }

            }
            findGeoDistance();
        }*/





        //dragX=DRAGX/scale;
        //dragY=DRAGY/scale;
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0, 0, 0f, 0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.setRotateM(zeroRotationMatrix, 0, 0, 0, 0, 1.0f);
        Matrix.multiplyMM(stockMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //GRID AND ORIGIN
        Matrix.multiplyMM(scratch, 0, stockMatrix, 0, zeroRotationMatrix, 0);
        Matrix.translateM(scratch, 0, 0 + dragX * scale, 0 + dragY * scale, 0);
        myGrid.Draw(scratch);
        Matrix.translateM(scratch, 0, 2f, 0, 0);
        myGrid.Draw(scratch);
        Matrix.translateM(scratch, 0, -4f, 0, 0);
        myGrid.Draw(scratch);
        Matrix.translateM(scratch, 0, 0, 2f, 0);
        myGrid.Draw(scratch);
        Matrix.translateM(scratch, 0, 2f, 0, 0);
        myGrid.Draw(scratch);
        Matrix.translateM(scratch, 0, 2f, 0, 0);
        myGrid.Draw(scratch);

        /*for (int i=0;i<100;i=i+2){
            for(int j=0;j<100;j=j+2){

                Matrix.multiplyMM(scratch, 0, stockMatrix, 0, zeroRotationMatrix, 0);
                Matrix.translateM(scratch, 0, 1 - 2 * (i / 100f) * scale + dragX * scale, 1 - 2 * (j / 100f) * scale + dragY * scale, 0);
                //System.out.println("Opacity #" + (1 - 2 * (i / 100f)));
                geoDistanceImage.setOpacity(1-geoDistance[i][j]/2f);
                geoDistanceImage.Draw(scratch, 0);
            }
        }*/


        Matrix.multiplyMM(scratch, 0, stockMatrix, 0, zeroRotationMatrix, 0);
        Matrix.translateM(scratch, 0, 0 + dragX*scale, 0 + dragY*scale, 0);
        Origin.Draw(scratch);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
        Matrix.translateM(scratch, 0, .9f, -.8f, 0);
        scaleVis.Draw(scratch,1);

        /////////////////

        // DRAW TURTLES
        for (int i=0;i<maxBots;i++){
            //System.out.println("turtle #"+i);
            //System.out.println("active: "+turtleList[i].getX());
            if (turtleList[i].getOn()==1) {
                Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
                Matrix.translateM(scratch, 0, turtleList[i].getX() * scale+dragX*scale, turtleList[i].getY() * scale+dragY*scale, 0);
                Matrix.rotateM(scratch, 0, turtleList[i].getRot(), 0, 0, 1f);
                Matrix.scaleM(scratch, 0, scale, scale, scale);

                if (i!=49){
                    if (1==1){
                        localizationSignal.Draw(scratch,turtleList[i].measured);
                    }
                    else {
                        localizationSignal.Draw(scratch,turtleList[i].measured);
                    }

                    if (i<1 || i>10 && (i<41 || i>43)){
                        myAr.Draw(scratch,turtleList[i].getState(),framecounter);
                    }
                    else{
                        turt1[turtleList[i].type].Draw(scratch);
                    }
                    if (turtleList[i].getState()==1){
                        selected.Draw(scratch,true);
                    }

                }
                else {
                    circ.Draw(scratch);
                    if (turtleList[i].getState()==1){
                        selected.Draw(scratch,true);
                    }
                }

            }
        }

        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
        Matrix.translateM(scratch, 0, dragX * scale, dragY * scale, 0);
        Matrix.scaleM(scratch, 0, scale, scale, scale);
        // DRAW VORONOI LINES
        if (vToggle==1) {
            for (int i = 0; i < vSize; i++) {
                if (i<vLine.length){
                   vLine[i].draw(scratch,.0f,1.0f,1.0f);
                }
            }
        }
        // DRAW FREE LINE   S
        if (fSize!=0) {
            for (int i = 0; i < fSize  ; i++) {
                    fLine[i].draw(scratch,1f,1f,1f);
                    Matrix.multiplyMM(scratch2, 0, mMVPMatrix, 0, mRotationMatrix, 0);
                    Matrix.translateM(scratch2, 0, pathArray.pose[i].x*scale+dragX*scale, pathArray.pose[i].y*scale+dragY*scale, 0);
                    Matrix.scaleM(scratch2, 0, .5f,.5f,.5f);
                    Matrix.rotateM(scratch2, 0, pathArray.pose[i].direction, 0, 0, 1f);
                    arrows.Draw(scratch2,false);
            }
            }

/*

//            for (int i = 0; i < 100; i++) {
//                if (bLine[i].active==true){
//                    bLine[i].draw(scratch,1f,1f,0f);
//                }
//            }

            for (int i=0; i<100;i++){
                if (pathLine[i].active==true){
                    pathLine[i].draw(scratch,1f,0,1f);
                }
            }


        if (obstacleLineList.isEmpty()==false){
            for (int i = 0; i < obstacleLineList.size(); i++) {
                oLine[i].draw(scratch,1f,0f,0f);

            }

        }*/




        if (gpToggle==1) {
            for (int i = 0; i < gpSize  ; i++) {
                Matrix.multiplyMM(scratch2, 0, mMVPMatrix, 0, mRotationMatrix, 0);
                Matrix.translateM(scratch2, 0, gaussPoint[i].x * scale+dragX*scale, gaussPoint[i].y * scale+dragY*scale, 0);
                Matrix.scaleM(scratch2, 0, .1f, .1f, .1f);
                arrows.Draw(scratch2, false);
            }
        }

        // DRAW WAYPOINT
        if (pToggle==1 && pToggle2==1) {
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, pX+dragX*scale, pY+dragY*scale, 0);
            wp.Draw(scratch);
        }

        // DRAW Block
        if (formation.active && formationToggleTransform.active) {
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + (float) Math.cos(formation.val4) * .3f * scale + dragX * scale, formation.y * scale + (float) Math.sin(formation.val4) * .3f * scale + dragY * scale, 0);
            rotation_block.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + formation.val3 * .2f * scale + dragX * scale, formation.y * scale + dragY * scale, 0);
            scale_block.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + formation.val3 * .2f * scale + dragX * scale - .05f, formation.y * scale + dragY * scale, 0);
            straight_arrow.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + formation.val3 * .2f * scale + dragX * scale + .05f, formation.y * scale + dragY * scale, 0);
            Matrix.rotateM(scratch, 0, 180, 0, 0, 1.0f);

            straight_arrow.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + dragX * scale, formation.y * scale + dragY * scale, 0);
            center_block.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale - .1f + dragX * scale, formation.y * scale + dragY * scale, 0);
            straight_arrow.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + dragX * scale, formation.y * scale + dragY * scale - .1f, 0);
            Matrix.rotateM(scratch, 0, 90, 0, 0, 1.0f);

            straight_arrow.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + .1f + dragX * scale, formation.y * scale + dragY * scale, 0);
            Matrix.rotateM(scratch, 0, 180, 0, 0, 1.0f);

            straight_arrow.Draw(scratch);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + dragX * scale, formation.y * scale + dragY * scale + .1f, 0);
            Matrix.rotateM(scratch, 0, 270, 0, 0, 1.0f);

            straight_arrow.Draw(scratch);
        }
        if (formation.active) {
            for (int i =0;i<formation.val1;i++){
                Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
                Matrix.translateM(scratch, 0, formation_locations.x[i] * scale+ dragX * scale, formation_locations.y[i] * scale  + dragY * scale, 0);
                Matrix.scaleM(scratch, 0, scale, scale, scale);
                formationLocationImage.Draw(scratch);
            }

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, formation.x * scale + dragX * scale, formation.y * scale + dragY * scale, 0);
            center_block.Draw(scratch);

        }
        if (formation.active && customFormation.active){
            for (int i =0;i<formation.val1;i++){
                Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
                Matrix.translateM(scratch, 0, formation_locations.x[i] * scale+ dragX * scale, formation_locations.y[i] * scale  + dragY * scale, 0);
                Matrix.scaleM(scratch, 0, scale, scale, scale);
                yellowGlow.Draw(scratch);
            }
        }


        // TEST -REMOVE
        //findCell(10);
       // findIntersects(10);
            /*Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, tX*scale+dragX*scale, tY*scale+dragY*scale, 0);
            wp.Draw(scratch);*/

/*        for (int i=0;i<50;i++){
            if (intersections[i].x!=0){
                Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
                Matrix.translateM(scratch, 0, intersections[i].x*scale+dragX*scale, intersections[i].y*scale+dragY*scale, 0);
               wp.Draw(scratch);

            }

        }*/
/*        if (voronoiCellBoundaries[8].size>0){
            for (int i=0;i<voronoiCellBoundaries[8].size;i++){
                Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
                Matrix.translateM(scratch, 0, voronoiCellBoundaries[8].x[i]*scale+dragX*scale, voronoiCellBoundaries[8].y[i]*scale+dragY*scale, 0);
                wpg.Draw(scratch);

            }

        }*/


        // DRAW CENTROIDS
        if (voronoiDeploymentToggle.active==true){
            for (int i=0; i< centroids.pose.length;i++){
                if(centroids.pose[i].active==true){
                    Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
                    Matrix.translateM(scratch, 0, centroids.pose[i].x*scale+dragX*scale, centroids.pose[i].y*scale+dragY*scale, 0);
                    wp.Draw(scratch);
                }
            }
        }

        // DRAW TARGET MARK
        /*if (tToggle==1){
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, zeroRotationMatrix, 0);
            Matrix.translateM(scratch, 0, tempX, tempY, 0);
            tar.Draw(scratch);
        }*/

        //DRAW GAUSSIAN
        //if (gToggle==1 && gToggle2==1){
            for (int i = 0; i < gMaxNum; i++) {
                if (gaussArrayList.locX[i]!=0&&gaussArrayList.locY[i]!=0){
                    float[] s = new float[16];
                    Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, 1.0f);
                    Matrix.multiplyMM(s, 0, mMVPMatrix, 0, mRotationMatrix, 0);

                    Matrix.translateM(s, 0, gaussArrayList.locX[i] * scale+dragX*scale, gaussArrayList.locY[i] * scale+dragY*scale, 0);
                    Matrix.scaleM(s, 0, (.75f+dial3)*scale, (.75f+dial3)*scale, (.75f+dial3)*scale);
                    gaussArrayList.Draw(s);
                    gaussOutline.Draw(s,0);
                }
            }

        //}


            //gList[g].Draw(scratch);
            //gg.Draw(scratch);



        //START DRAWING TEXT BLOCK
        //

        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.translateM(scratch, 0, -slider, 0, 0);
        interfacePull.Draw(scratch);


        // DRAW TOGGLE ICONS

        vorToggle.Draw(scratch, vToggle);
        freeDrawToggle.Draw(scratch, fToggle);

        /*if (fToggle==1){
            if (swarmToggle.active==true){
                swarmToggle.Draw(scratch,1);
            }
            else {
                swarmToggle.Draw(scratch,0);
            }
        }*/  //THIS FEATURE CAN BE ADDED LATER IF WE WANT


        if (gToggle==1){

            bar.Draw(scratch,1);
            Matrix.translateM(scratch, 0, dial1*-.6f, 0, 0);
            dial.Draw(scratch, 1);
            Matrix.translateM(scratch, 0, -dial1*-.6f, 0, 0);

            bar_2.Draw(scratch,1);
            Matrix.translateM(scratch, 0, dial2*-.6f, 0, 0);
            dial_2.Draw(scratch, 1);
            Matrix.translateM(scratch, 0, -dial2*-.6f, 0, 0);

            bar_3.Draw(scratch,1);
            Matrix.translateM(scratch, 0, dial3*-.6f, 0, 0);
            dial_3.Draw(scratch, 1);
            Matrix.translateM(scratch, 0, -dial3*-.6f, 0, 0);


            //if (centroidTrackingOption.active==true){
               // centroidTrackingOption.Draw(scratch, 1);
              centroidTrackingEquation.Draw(scratch);
            //}
            //else {
              //  centroidTrackingOption.Draw(scratch,0);
                //lloyds.Draw(scratch);
            //}
        } else if (fToggle==0){
            directionalDrag.active=true;
            directionalDrag.Draw(scratch,1);
        }



        wayPointToggle.Draw(scratch,pToggle);
        ardronePrefToggle.Draw(scratch, APToggle);
        ardroneAddToggle.Draw(scratch, 0);

        gaussToggle.Draw(scratch,gToggle);
       // temptoggle.Draw(scratch, gpToggle);        //exit.Draw(scratch,1);
        addTurtle.Draw(scratch,1);
        gaussianPathToggle.Draw(scratch,1);
        resetToggle.Draw(scratch,1);
        if (voronoiDeploymentToggle.active==true){
            voronoiDeploymentToggle.Draw(scratch, 1);
        }
        else {
            voronoiDeploymentToggle.Draw(scratch, 0);
        }
        if (gaussianTrackToggle.active==true){
            gaussianTrackToggle.Draw(scratch, 1);
        }
        else {
            gaussianTrackToggle.Draw(scratch, 0);
        }
        if (dragRobotToggle.active==true){
            dragRobotToggle.Draw(scratch,1);
        }else{
            dragRobotToggle.Draw(scratch,0);
        }
        if (freeBoundarytoggle.active==true){
            freeBoundarytoggle.Draw(scratch,1);
        }else{
            freeBoundarytoggle.Draw(scratch,0);
        }

        if (turtleList[43].on==0){
            dragToggle.Draw(scratch,2);
        }else if (turtleList[42].on==0){
            dragToggle.Draw(scratch,1);
        }else if (turtleList[41].on==0){
            dragToggle.Draw(scratch,0);
        }else{
            dragToggle.Draw(scratch,-1);
        }
/*        if (fToggle==1) {
            obstacleLineToggle.Draw(scratch, 0);
        }*/
        if (formation.active==true){
            formation.Draw(scratch,1);
        }else{
            formation.Draw(scratch,0);
        }
        if (formationToggleTransform.active==true){
            formationToggleTransform.Draw(scratch,1);
        }else{
            formationToggleTransform.Draw(scratch,0);
        }
        if (customFormation.active==true){
            customFormation.Draw(scratch,1);
        }else{
            customFormation.Draw(scratch,0);
        }


        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.translateM(scratch, 0, .75f, -.85f, 0);


        // DRAW BUTTONS
        plus.Draw(scratch, false);
        Matrix.translateM(scratch, 0, .3f, 0f, 0);
        minus.Draw(scratch, false);


        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.translateM(scratch, 0, field1X*scale+ dragX * scale, field1Y * scale + dragY * scale, 0);
        Matrix.scaleM(scratch, 0, scale / 2, scale / 2, scale / 2);
        field.Draw(scratch);
        fieldOutline.Draw(scratch);

        /*Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.translateM(scratch, 0, field2X*scale+dragX*scale, field2Y*scale+(dragY+.6f)*scale, 0);
        Matrix.scaleM(scratch, 0, scale / 2, scale / 2, scale / 2);
        field.Draw(scratch);
        fieldOutline.Draw(scratch);*/

        if (commit.blocked==false){
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            Matrix.translateM(scratch, 0, -slider, 0, 0);
            commit.Draw(scratch, commit.active);
        }


        if (!clear.blocked){
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            Matrix.translateM(scratch, 0, -slider, 0, 0);
            clear.Draw(scratch, clear.active);
        }


        if (!clearAll.blocked) {
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            Matrix.translateM(scratch, 0, -slider, 0, 0);
            clearAll.Draw(scratch, false);
        }

        if (formation.active && !customFormation.active && !formationToggleTransform.active){
            addToggle.blocked=false;
            subtractToggle.blocked=false;
        }else{
            addToggle.blocked=true;
            subtractToggle.blocked=true;
        }
        if (!addToggle.blocked) {
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            Matrix.translateM(scratch, 0, -slider, 0, 0);
            addToggle.Draw(scratch, 0);
        }

        if (!subtractToggle.blocked) {
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            Matrix.translateM(scratch, 0, -slider, 0, 0);
            subtractToggle.Draw(scratch, 0);
        }






        int temp = 0;
        for (int j = 0; j<textList.size();j++){
            if (textList.get(j).getActive()==1){
                    Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
                    if (textList.get(j).getSlides()==1){
                        Matrix.translateM(scratch, 0, textList.get(j).getyGl()-slider, textList.get(j).getxGl(), 0);
                    }
                    else{
                        Matrix.translateM(scratch, 0, textList.get(j).getyGl(), textList.get(j).getxGl(), 0);
                    }
                    String tempString = textList.get(j).getText();
                    for (int i = 0; i<tempString.length();i++){
                        String s = String.valueOf(tempString.charAt(i));

                        if (Character.isUpperCase(tempString.codePointAt(i))==true || s.equals(" ")){
                            if (temp!=0){
                                Matrix.translateM(scratch, 0, -.01f, 0f, 0);
                            }
                            textSystem.Draw(scratch, s, 0);
                            temp++;
                            Matrix.translateM(scratch, 0, -.001f, 0f, 0);
                        }
                        else{
                            textSystem.Draw(scratch, s, 1);
                            temp = 0;
                        }
                        if (s.equals("i")|| s.equals("t")||s.equals("l")|| s.equals("r")){
                            Matrix.translateM(scratch, 0, -.013f, 0f, 0);
                        }
                        else if (s.equals("m")){
                            Matrix.translateM(scratch, 0, -.036f, 0f, 0);
                        }
                        else Matrix.translateM(scratch, 0, -.023f, 0f, 0);
                        if (s.equals(".")||s.equals(":") || s.equals(" ")){
                            Matrix.translateM(scratch, 0, .009f, 0f, 0);
                        }
                    }
            }
        }


        if (APToggle==1){
            temp = 0;
            for (int j = 0; j<textListARINFO.size();j++){
                if (textListARINFO.get(j).getActive()==1){
                    Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
                    if (textListARINFO.get(j).getSlides()==1){
                        Matrix.translateM(scratch, 0, textListARINFO.get(j).getyGl()-slider, textListARINFO.get(j).getxGl(), 0);
                    }
                    else{
                        Matrix.translateM(scratch, 0, textListARINFO.get(j).getyGl(), textListARINFO.get(j).getxGl(), 0);
                    }
                    String tempString = textListARINFO.get(j).getText();
                    for (int i = 0; i<tempString.length();i++){
                        String s = String.valueOf(tempString.charAt(i));

                        if (Character.isUpperCase(tempString.codePointAt(i))==true || s.equals(" ")){
                            if (temp!=0){
                                Matrix.translateM(scratch, 0, -.01f, 0f, 0);
                            }
                            textSystem.Draw(scratch, s, 0);
                            temp++;
                            Matrix.translateM(scratch, 0, -.001f, 0f, 0);
                        }
                        else{
                            textSystem.Draw(scratch, s, 1);
                            temp = 0;
                        }
                        if (s.equals("i")|| s.equals("t")||s.equals("l")|| s.equals("r")){
                            Matrix.translateM(scratch, 0, -.013f, 0f, 0);
                        }
                        else if (s.equals("m")){
                            Matrix.translateM(scratch, 0, -.036f, 0f, 0);
                        }
                        else Matrix.translateM(scratch, 0, -.023f, 0f, 0);
                        if (s.equals(".")||s.equals(":") || s.equals(" ")||s.equals("m")){
                            Matrix.translateM(scratch, 0, .009f, 0f, 0);
                        }
                    }
                }
            }
        }

        if (SINFO_FLAG==true && !formation.active){
            temp = 0;
            for (int j = 0; j<textListSINFO.size();j++){
                if (textListSINFO.get(j).getActive()==1){
                    Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
                    if (textListSINFO.get(j).getSlides()==1){
                        Matrix.translateM(scratch, 0, textListSINFO.get(j).getyGl()-slider, textListSINFO.get(j).getxGl(), 0);
                    }
                    else{
                        Matrix.translateM(scratch, 0, textListSINFO.get(j).getyGl(), textListSINFO.get(j).getxGl(), 0);
                    }
                    String tempString = textListSINFO.get(j).getText();
                    for (int i = 0; i<tempString.length();i++){
                        String s = String.valueOf(tempString.charAt(i));

                        if (Character.isUpperCase(tempString.codePointAt(i))==true || s.equals(" ")){
                            if (temp!=0){
                                Matrix.translateM(scratch, 0, -.01f, 0f, 0);
                            }
                            Matrix.translateM(scratch, 0, .005f, 0f, 0);
                            textSystem.Draw(scratch, s, 0);
                            temp++;
                            Matrix.translateM(scratch, 0, -.001f, 0f, 0);
                        }
                        else{
                            textSystem.Draw(scratch, s, 1);
                            temp = 0;
                        }
                        if (s.equals("i")|| s.equals("t")||s.equals("l")|| s.equals("r")){
                            Matrix.translateM(scratch, 0, -.013f, 0f, 0);
                        }
                        else if (s.equals("m")){
                            Matrix.translateM(scratch, 0, -.036f, 0f, 0);
                        }
                        else Matrix.translateM(scratch, 0, -.023f, 0f, 0);
                        if (s.equals(".")||s.equals(":") || s.equals(" ")||s.equals("m")){
                            Matrix.translateM(scratch, 0, .009f, 0f, 0);
                        }
                    }
                }
            }
        }

        if (gToggle==1){
            temp = 0;
            for (int j = 0; j<textListGaussian.size();j++){
                if (textListGaussian.get(j).getActive()==1){
                    Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
                    if (textListGaussian.get(j).getSlides()==1){
                        Matrix.translateM(scratch, 0, textListGaussian.get(j).getyGl()-slider, textListGaussian.get(j).getxGl(), 0);
                    }
                    else{
                        Matrix.translateM(scratch, 0, textListGaussian.get(j).getyGl(), textListGaussian.get(j).getxGl(), 0);
                    }
                    String tempString = textListGaussian.get(j).getText();
                    for (int i = 0; i<tempString.length();i++){
                        String s = String.valueOf(tempString.charAt(i));

                        if (Character.isUpperCase(tempString.codePointAt(i))==true || s.equals(" ")){
                            if (temp!=0){
                                Matrix.translateM(scratch, 0, -.01f, 0f, 0);
                            }
                            Matrix.translateM(scratch, 0, .005f, 0f, 0);
                            textSystem.Draw(scratch, s, 0);
                            temp++;
                            Matrix.translateM(scratch, 0, -.001f, 0f, 0);
                        }
                        else{
                            textSystem.Draw(scratch, s, 1);
                            temp = 0;
                        }
                        if (s.equals("i")|| s.equals("t")||s.equals("l")|| s.equals("r")){
                            Matrix.translateM(scratch, 0, -.013f, 0f, 0);
                        }
                        else if (s.equals("m")){
                            Matrix.translateM(scratch, 0, -.036f, 0f, 0);
                        }
                        else Matrix.translateM(scratch, 0, -.023f, 0f, 0);
                        if (s.equals(".")||s.equals(":") || s.equals(" ")||s.equals("m")){
                            Matrix.translateM(scratch, 0, .009f, 0f, 0);
                        }
                    }
                }
            }
        }

        if (formation.active && APToggle!=1){
            temp = 0;
            for (int j = 0; j<textListFormation.size();j++){
                if (textListFormation.get(j).getActive()==1){
                    Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
                    if (textListFormation.get(j).getSlides()==1){
                        Matrix.translateM(scratch, 0, textListFormation.get(j).getyGl()-slider, textListFormation.get(j).getxGl(), 0);
                    }
                    else{
                        Matrix.translateM(scratch, 0, textListFormation.get(j).getyGl(), textListFormation.get(j).getxGl(), 0);
                    }
                    String tempString = textListFormation.get(j).getText();
                    for (int i = 0; i<tempString.length();i++){
                        String s = String.valueOf(tempString.charAt(i));

                        if (Character.isUpperCase(tempString.codePointAt(i))==true || s.equals(" ")){
                            if (temp!=0){
                                Matrix.translateM(scratch, 0, -.01f, 0f, 0);
                            }
                            Matrix.translateM(scratch, 0, .005f, 0f, 0);
                            textSystem.Draw(scratch, s, 0);
                            temp++;
                            Matrix.translateM(scratch, 0, -.001f, 0f, 0);
                        }
                        else{
                            textSystem.Draw(scratch, s, 1);
                            temp = 0;
                        }
                        if (s.equals("i")|| s.equals("t")||s.equals("l")|| s.equals("r")){
                            Matrix.translateM(scratch, 0, -.013f, 0f, 0);
                        }
                        else if (s.equals("m")){
                            Matrix.translateM(scratch, 0, -.036f, 0f, 0);
                        }
                        else Matrix.translateM(scratch, 0, -.023f, 0f, 0);
                        if (s.equals(".")||s.equals(":") || s.equals(" ")||s.equals("m")){
                            Matrix.translateM(scratch, 0, .009f, 0f, 0);
                        }
                    }
                }
            }
        }

        //
        //END DRAWING TEXT BLOCK
        framecounter++;
        if (framecounter>11){
            framecounter=0;
        }
    }

    public void tempFun(float xx, float yy){
        tempX  =xx;
        tempY  =yy;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }
    public void setvToggle(int i){
        vToggle=i;
    }
    public int getvToggle(){
        return vToggle;
    }
    public void setfToggle(int i){
        fToggle=i;
    }
    public int getfToggle(){
        return fToggle;
    }
    public void setpToggle(int i){
        pToggle=i;
    }
    public void setpToggle2(int i){
        pToggle2=i;

    }
    public int getpToggle(){
        return pToggle;
    }

    public void setgToggle(int i) {
        gToggle = i;
    }

    public void setgToggle2(int i) {
        gToggle2 = i;
    }

    public int getgToggle() { return gToggle;}
    public void setAPToggle(int i){
        APToggle=i;
    }
    public int getAPToggle(){
        return APToggle;
    }

    public int getgpToggle(){
        return gpToggle;
    }

    public void setgpToggle(int gptoggle){
        gpToggle=gptoggle;
    }

    public void eraseFreeLine(){
        fSize=0;
        pathArray.Clear();
    }


    public void eraseGaussLine(){
        gpSize=0;
    }

    public void setAllToggles(int i){
        vToggle = i;
        fToggle = i;
        pToggle = i;
        gToggle = i;
        APToggle = i;
    }

    public void setWayPointValues(float px, float py){
        pX=px;
        pY=py;
        if (formation.active){
            formation.x=pX/scale;
            formation.y=pY/scale;
            formation.send=true;
        }
    }

    public void setGaussValues(float px, float py, int ind){
        pX = px;
        pY = py;
        g = ind;
    }

    public float[] toGlobal(float px, float py){
        float[] globalCoord={px*scale, py*scale};
        return globalCoord;
    }

    public float[] toGL(float px, float py){
        float[] glCoord={px/scale, py/scale};
        return glCoord;
    }


    public void setGaussScale(int i, float x) {
        gaussArrayList.scaleG[i] = 1f/scale;
    }


    public void addGaussStuff(float xPos, float yPos, float s, int gInd){
        gaussArrayList.locY[gInd]=yPos/scale;
        gaussArrayList.locX[gInd]=xPos/scale;
        gaussArrayList.scaleG[gInd]=1f/scale;
        gaussFlag = true;
    }

    public void updateGauss(float xPos, float yPos, int gInd){
        gaussArrayList.locX[gInd]= xPos/scale-dragX;
        gaussArrayList.locY[gInd]= yPos/scale-dragY;
    }
    public float[] getGaussX(){
        return gaussArrayList.locY;
    }

    public float[] getGaussY(){
        return gaussArrayList.locX;
    }

    public float[] getGaussScale(){
        return gaussArrayList.scaleG;
    }

    public void clearGauss() {
        for (int i = 0; i< gMaxNum; i++){
            gaussArrayList.locX[i] = 0;
            gaussArrayList.locY[i] = 0;
            gaussArrayList.scaleG[i] = 0;
        }
    }






    public void setFreeDrawCoordinates(float s[],int i, int j, float xPos, float yPos,float x2, float y2, boolean closed){
        fLine[i].setSquareCoords(s);
        fSize = j;

        lastPathCoordinateX=x2/scale;
        lastPathCoordinateY=y2/scale;

        pathArray.header="OPEN";
        //if (i==0){
            pathArray.pose[i].x=xPos/scale;
            pathArray.pose[i].y=yPos/scale;
            pathArray.pose[i].active=true;
        /*}
        pathArray.pose[j].x=x2/scale;
        pathArray.pose[j].y=y2/scale;
        pathArray.pose[j].active=true;*/
        if (i%10==0 && i>1){
            pathArray.pose[i].direction=(float)Math.acos((pathArray.pose[i].x-pathArray.pose[i-1].x)/(Math.sqrt(pathArray.pose[i].x*pathArray.pose[i].x+pathArray.pose[i].y*pathArray.pose[i].y)));
            pathArray.pose[i].direction=pathArray.pose[i].direction+180;
        }
        else{
            pathArray.pose[i].direction=45;
        }

        if (closed==true){
            pathArray.header="CLOSED";
        }
    }


    public void makeGaussPoints(){
        int pastDir=1;
        if (fSize>1) {
            float Coords[] = {
                    -0.5f,  0.5f, 0.0f,   // top left
                    -0.5f, -0.5f, 0.0f,   // bottom left
                    0.5f, -0.5f, 0.0f,   // bottom right
                    0.5f,  0.5f, 0.0f }; // top right

                Coords= fLine[0].getSquareCoords();

            float angle=(float) Math.atan(-(Coords[0] - Coords[9]) / (Coords[1] - Coords[10]));
            double cd = Math.cos(angle);
            double cy = Math.sin(angle);
            if ((Coords[1] - Coords[10])<0){
                cd=-cd;
                cy=-cy;
                pastDir=-1;
            }
            else{
                pastDir=1;
            }
            gaussPoint[0].x=Coords[9]+ (float) cd * .1f;
            gaussPoint[0].y=Coords[10]+ (float) cy * .1f;
            gpSize=1;
            float pastAngle = angle;
                for (int i = 1; i < fSize-1; i++) {
                    Coords= fLine[i].getSquareCoords();
                    if(pastDir*(Coords[1] - Coords[10])<0){
                        if ((Coords[0] - Coords[9])<0)
                        {
                            pastAngle=pastAngle-3.14f*pastDir;
                        }
                        else{
                            pastAngle=pastAngle+3.14f*pastDir;
                        }

                    }
                    angle=(float) Math.atan(-(Coords[0] - Coords[9]) / (Coords[1] - Coords[10]));
                    cd = Math.cos((angle + pastAngle) / 2);
                    cy = Math.sin((angle+pastAngle)/2);
                    if ((Coords[1] - Coords[10])<0){
                        cd=-cd;
                        cy=-cy;
                        pastDir=-1;
                    }
                    else{
                        pastDir=1;
                    }
                    gaussPoint[i].x=Coords[9]+ (float) cd * .1f;
                    gaussPoint[i].y=Coords[10]+ (float) cy * .1f;
                    pastAngle=angle;
                    gpSize++;



                }
            Coords= fLine[fSize-1].getSquareCoords();
            angle=(float) Math.atan(-(Coords[0] - Coords[9]) / (Coords[1] - Coords[10]));
            cd = Math.cos((angle) / 2);
            cy = Math.sin((angle)/2);
            if ((Coords[1] - Coords[10])<0){
                cd=-cd;
                cy=-cy;
            }

            gaussPoint[fSize-1].x=Coords[0]+ (float) cd * .1f;
            gaussPoint[fSize-1].y=Coords[1]+ (float) cy * .1f;
            gpSize++;
        }
    }

    public void makeObstacle(){
        if (fSize>0){

            for (int i=1; i<=fSize;i++){

                tempobstacleLine=new obstacleLine();

                tempobstacleLine.x[0]=pathArray.pose[i-1].x;
                tempobstacleLine.y[0]=pathArray.pose[i-1].y;

                if (i!=fSize){
                    tempobstacleLine.x[1]=pathArray.pose[i].x;
                    tempobstacleLine.y[1]=pathArray.pose[i].y;
                }
                else{
                    tempobstacleLine.x[1]=lastPathCoordinateX;
                    tempobstacleLine.y[1]=lastPathCoordinateY;
                }

                obstacleLineList.add(tempobstacleLine);
                obstacleLinePoints[(i-1)*2].x=tempobstacleLine.x[0];
                obstacleLinePoints[(i-1)*2].y=tempobstacleLine.y[0];

                obstacleLinePoints[(i-1)*2+1].x=tempobstacleLine.x[1];
                obstacleLinePoints[(i-1)*2+1].y=tempobstacleLine.y[1];




/*                if (!obstacleMapPoints.containsValue(obstacleLinePoints[(i-1)*2])){
                    obstacleMapPoints.put(obstacleMapPoints.size(),obstacleLinePoints[(i-1)*2]);
                }
                if (!obstacleMapPoints.containsValue(obstacleLinePoints[(i-1)*2+1])){
                    obstacleMapPoints.put(obstacleMapPoints.size(),obstacleLinePoints[(i-1)*2+1]);
                }*/





                cd = Math.cos(Math.atan((tempobstacleLine.x[0] - tempobstacleLine.x[1]) / (tempobstacleLine.y[0] - tempobstacleLine.y[1])));
                cy = Math.sin(Math.atan((tempobstacleLine.x[0] - tempobstacleLine.x[1]) / (tempobstacleLine.y[0] - tempobstacleLine.y[1])));

                Coords[0] = tempobstacleLine.x[0] + (float) cd * .005f*scale;
                //Coords[0]=Coords[0]/scale;
                Coords[1] = tempobstacleLine.y[0] - (float) cy * .005f*scale;
                // Coords[1]=Coords[1]/scale;


                Coords[9] = tempobstacleLine.x[1] + (float) cd * .005f*scale;
                // Coords[9]=Coords[9]/scale;
                Coords[10] =  tempobstacleLine.y[1] - (float) cy * .005f*scale;
                //Coords[10]=Coords[10]/scale;

                Coords[3] = tempobstacleLine.x[0] - (float) cd * .005f*scale;
                // Coords[3]=Coords[3]/scale;
                Coords[4] = tempobstacleLine.y[0] + (float) cy * .005f*scale;
                // Coords[4]=Coords[4]/scale;

                Coords[6] = tempobstacleLine.x[1] - (float) cd * .005f*scale;
                // Coords[6]=Coords[6]/scale;
                Coords[7] = tempobstacleLine.y[1] + (float) cy * .005f*scale;
                // Coords[7]=Coords[7]/scale;

                oLine[obstacleLineList.size()-1].setSquareCoords(Coords);
                nearestPoint(tempobstacleLine, turtleList[10].x, turtleList[10].y, i - 1,10);


            }


        }

        fSize=0;
        pathArray.Clear();



    }

    public void toMapPoints(){




        for (int i=0;i<obstacleLineList.size();i++){

            pointXY temp = new pointXY();
            pointXY temp2= new pointXY();
            pointXY temp3= new pointXY();

            temp.x=obstacleLineList.get(i).x[0];
            temp.y=obstacleLineList.get(i).y[0];

            if (!obstacleMapPoints.containsValue(temp)){

                obstacleMapPoints.put(obstacleMapPoints.size(), temp);
                obstacleMapPoints.get(obstacleMapPoints.size()-1).addLinkAngle(returnTwoVectorAngle(temp.x, temp.y, temp.x + 1, temp.y, obstacleLineList.get(i).x[1], obstacleLineList.get(i).y[1]));

                for (int k=i+1; k<obstacleLineList.size();k++){

                    temp3.x=obstacleLineList.get(k).x[0];
                    temp3.y=obstacleLineList.get(k).y[0];
                    if (obstacleMapPoints.containsValue(temp3)){

                        obstacleMapPoints.get(obstacleMapPoints.size()-1).addLinkAngle(returnTwoVectorAngle(temp3.x, temp3.y, temp3.x + 1, temp3.y, obstacleLineList.get(k).x[1], obstacleLineList.get(k).y[1]) );
                    }

                    temp3.x=obstacleLineList.get(k).x[1];
                    temp3.y=obstacleLineList.get(k).y[1];

                    if (obstacleMapPoints.containsValue(temp3)){

                        obstacleMapPoints.get(obstacleMapPoints.size()-1).addLinkAngle(returnTwoVectorAngle(temp3.x, temp3.y, temp3.x + 1, temp3.y, obstacleLineList.get(k).x[0], obstacleLineList.get(k).y[0]) );
                    }
                }
            }


            temp2.x=obstacleLineList.get(i).x[1];
            temp2.y = obstacleLineList.get(i).y[1];

            if (!obstacleMapPoints.containsValue(temp2)){

                obstacleMapPoints.put(obstacleMapPoints.size(), temp2);
                obstacleMapPoints.get(obstacleMapPoints.size()-1).addLinkAngle(returnTwoVectorAngle(temp2.x, temp2.y, temp2.x + 1, temp2.y, obstacleLineList.get(i).x[0], obstacleLineList.get(i).y[0]));


               for (int k=i+1; k<obstacleLineList.size();k++){

                    temp3.x=obstacleLineList.get(k).x[0];
                    temp3.y=obstacleLineList.get(k).y[0];
                    if (obstacleMapPoints.containsValue(temp3)){

                        obstacleMapPoints.get(obstacleMapPoints.size()-1).addLinkAngle(returnTwoVectorAngle(temp3.x, temp3.y, temp3.x + 1, temp3.y, obstacleLineList.get(k).x[1], obstacleLineList.get(k).y[1]) );
                    }

                    temp3.x=obstacleLineList.get(k).x[1];
                    temp3.y=obstacleLineList.get(k).y[1];

                    if (obstacleMapPoints.containsValue(temp3)){

                        obstacleMapPoints.get(obstacleMapPoints.size()-1).addLinkAngle(returnTwoVectorAngle(temp3.x, temp3.y, temp3.x + 1, temp3.y, obstacleLineList.get(k).x[0], obstacleLineList.get(k).y[0]) );
                    }
                }

            }
        }

    }


    public void findIntersects(int t){
        int intCount=0;
        boolean flag=false;

        for (int i=0;i<50;i++){
            intersections[i].x=0;
            intersections[i].y=0;
            intersections[i].boundary=false;

        }


        for (int i=0;i<obstacleLineList.size();i++){
            for (int j=0;j<obstacleLineList.size();j++){
                if (j!=i){
                    flag=false;
                    for (int k=0;k<intCount;k++){
                        if (intersections[k].x==findXIntercept(bEquations[t].m[i], bEquations[t].m[j], bEquations[t].x[i], bEquations[t].x[j], bEquations[t].y[i], bEquations[t].y[j])){
                            flag=true;
                        }
                    }

                    if(flag==false){
                        intersections[intCount].x=findXIntercept(bEquations[t].m[i], bEquations[t].m[j], bEquations[t].x[i], bEquations[t].x[j], bEquations[t].y[i], bEquations[t].y[j]);
                        intersections[intCount].y=findYIntercept(bEquations[t].m[i], intersections[intCount].x, bEquations[t].x[i], bEquations[t].y[i]);
                        intersections[intCount].index1=Math.min(i,j);
                        intersections[intCount].index2=Math.max(i,j);
                        intCount=intCount+1;
                    }

                }
            }

            for (int j=0;j<vSize+4;j++){

                if (segmentLineintersection(bEquations[t].m[i],bEquations[t].x[i],bEquations[t].y[i],voronoiEquations.x[j],voronoiEquations.x2[j],voronoiEquations.y[j],voronoiEquations.y2[j])!=-1){
                    intersections[intCount].x=segmentLineintersection(bEquations[t].m[i],bEquations[t].x[i],bEquations[t].y[i],voronoiEquations.x[j],voronoiEquations.x2[j],voronoiEquations.y[j],voronoiEquations.y2[j]);
                    intersections[intCount].y=findYIntercept(bEquations[t].m[i], intersections[intCount].x, bEquations[t].x[i], bEquations[t].y[i]);
                    intersections[intCount].index1=i;
                    intersections[intCount].index2=j+obstacleLineList.size();
                    if (j>=vSize){
                        intersections[intCount].boundary=true;
                    }
                    intCount=intCount+1;
                }
            }
        }

        intersections[intCount].x=voronoiEquations.x[0];
        intersections[intCount].y=voronoiEquations.y[0];
        intersections[intCount].index1=obstacleLineList.size()+0;
        intersections[intCount].index2=obstacleLineList.size()+3;
        intersections[intCount].boundary=true;
        intCount=intCount+1;

        intersections[intCount].x=voronoiEquations.x[1];
        intersections[intCount].y=voronoiEquations.y[1];
        intersections[intCount].index1=obstacleLineList.size()+0;
        intersections[intCount].index2=obstacleLineList.size()+1;
        intersections[intCount].boundary=true;

        intCount=intCount+1;

        intersections[intCount].x=voronoiEquations.x[2];
        intersections[intCount].y=voronoiEquations.y[2];
        intersections[intCount].index1=obstacleLineList.size()+1;
        intersections[intCount].index2=obstacleLineList.size()+2;
        intersections[intCount].boundary=true;

        intCount=intCount+1;

        intersections[intCount].x=voronoiEquations.x[3];
        intersections[intCount].y=voronoiEquations.y[3];
        intersections[intCount].index1=obstacleLineList.size()+2;
        intersections[intCount].index2=obstacleLineList.size()+3;
        intersections[intCount].boundary=true;

        intCount=intCount+1;



        //Look for intersects in vLine and boundary
        //Add intersects in vLine

        findCell2(t, intCount);

    }

    public void findCell2(int t, int c){

        for (int i=0;i<50;i++){
            cellBoundaries[i].x=0;
            cellBoundaries[i].y=0;
        }
        int intCount=0;
        for (int i=0;i<c;i++){
            boolean intersectionFlag=false;
            for (int j=0;j<obstacleLineList.size() && intersectionFlag==false;j++){
                if (j!=intersections[i].index1 && j!=intersections[i].index2 && segmentLineintersection(bEquations[t].m[j], bEquations[t].x[j], bEquations[t].y[j], turtleList[t].x, intersections[i].x, turtleList[t].y, intersections[i].y)!=-1){
                   /* cellBoundaries[intCount].x = segmentLineintersection(bEquations[t].m[j], bEquations[t].x[j], bEquations[t].y[j], turtleList[t].x, intersections[i].x, turtleList[t].y,intersections[i].y);
                    cellBoundaries[intCount].y=findYIntercept(bEquations[t].m[j],cellBoundaries[intCount].x,bEquations[t].x[j],bEquations[t].y[j]);
                    System.out.("INTERSECTIONFOUND~~~~~~~~~~\n~~~~~~~~~~~~\n~~~~~X: " + cellBoundaries[intCount].x);
                    intCount=intCount+1;
*/
                    intersectionFlag=true;
                }
            }

            if (intersections[i].boundary==false){
                for (int j=0;j<vSize+4 && intersectionFlag==false;j++){
                    if ( j+obstacleLineList.size()!=intersections[i].index1 && j+obstacleLineList.size()!=intersections[i].index2 && segmentIntersection(turtleList[t].x,intersections[i].x,turtleList[t].y,intersections[i].y,voronoiEquations.x[j],voronoiEquations.x2[j],voronoiEquations.y[j],voronoiEquations.y2[j])==true){

                        intersectionFlag=true;

                    }
                }

            }

            if (intersections[i].boundary==false && (intersections[i].x>1 || intersections[i].x< -1.2 || intersections[i].y>1 || intersections[i].y< -1)){
                intersectionFlag=true;
            }



            if (intersectionFlag==false){
                cellBoundaries[intCount].x=intersections[i].x;
                cellBoundaries[intCount].index1=intersections[i].index1;
                cellBoundaries[intCount].index2=intersections[i].index2;
                cellBoundaries[intCount].y=intersections[i].y;
                cellBoundaries[intCount].targeted=false;
                intCount=intCount+1;
            }
        }
        setBline(intCount);
    }


    public void setBline(int c){
        for (int i=0;i<100;i++){
            bLine[i].active=false;
        }
        int intCount=0;
        int lastIndex=-1;
        int targetIndex=-1;
        boolean flag=false;
        int targetNode=-1;
        pair1.count=c;
        for (int i=0;i<c;i++){
            for (int j=0;j<c;j++){
                if (i!=j){

                    if (cellBoundaries[i].index1==cellBoundaries[j].index1 || cellBoundaries[i].index2==cellBoundaries[j].index1 || cellBoundaries[i].index1==cellBoundaries[j].index2 || cellBoundaries[i].index2==cellBoundaries[j].index2){
                        flag=false;

                       // System.out.print("CELL POINT FOUND~INDEX # " + 1 +"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ : " + cellBoundaries[i].index1 + ", "+cellBoundaries[i].index2+ ", "+cellBoundaries[j].index1+ ", "+cellBoundaries[j].index2 + "\n");

                        for (int k=0;k<c;k++){
                            if (pair1.pair[k][0]== Math.min(i,j) && pair1.pair[k][1]==Math.max(i, j)){
                                flag=true;
                            }
                        }
                        if (flag==false){
                            pair1.pair[intCount][0]=Math.min(i, j);
                            pair1.pair[intCount][1]=Math.max(i, j);
                            intCount++;
//                            System.out.print("CELL POINT FOUND~INDEX # " + 1 +"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ : " + pair1.pair[intCount][0] + ", "+pair1.pair[intCount][1]+ "\n");

                        }
                    }
                }
            }
        }
        intCount=0;

        for (int i=0;i<c;i++){
            cd = Math.cos(Math.atan((cellBoundaries[pair1.pair[i][0]].x - cellBoundaries[pair1.pair[i][1]].x) / (cellBoundaries[pair1.pair[i][0]].y - cellBoundaries[pair1.pair[i][1]].y )));
            cy = Math.sin(Math.atan((cellBoundaries[pair1.pair[i][0]].x -   cellBoundaries[pair1.pair[i][1]].x ) / (cellBoundaries[pair1.pair[i][0]].y -cellBoundaries[pair1.pair[i][1]].y )));


            Coords[0] = cellBoundaries[pair1.pair[i][0]].x + (float) cd * .005f/scale;
            //Coords[0]=Coords[0]/scale;
            Coords[1] = cellBoundaries[pair1.pair[i][0]].y - (float) cy * .005f/scale;
            // Coords[1]=Coords[1]/scale;


            Coords[9] = cellBoundaries[pair1.pair[i][1]].x + (float) cd * .005f/scale;
            // Coords[9]=Coords[9]/scale;
            Coords[10] =  cellBoundaries[pair1.pair[i][1]].y - (float) cy * .005f/scale;
            //Coords[10]=Coords[10]/scale;

            Coords[3] = cellBoundaries[pair1.pair[i][0]].x - (float) cd * .005f/scale;
            // Coords[3]=Coords[3]/scale;
            Coords[4] = cellBoundaries[pair1.pair[i][0]].y + (float) cy * .005f/scale;
            // Coords[4]=Coords[4]/scale;

            Coords[6] = cellBoundaries[pair1.pair[i][1]].x - (float) cd * .005f/scale;
            // Coords[6]=Coords[6]/scale;
            Coords[7] = cellBoundaries[pair1.pair[i][1]].y + (float) cy * .005f/scale;
            // Coords[7]=Coords[7]/scale;

            bLine[intCount].setSquareCoords(Coords);
            bLine[intCount].active=true;
            intCount=intCount+1;
        }


    }




    public float findXIntercept(float m1, float m2, float x1, float x2, float y1, float y2){
        return (m1*x1-m2*x2-y1+y2)/(m1-m2);
    }
    public float findYIntercept(float m1, float x, float x1, float y1){
        return m1*(x-x1)+y1;
    }
    public float findSlope(float x1, float x2, float y1, float y2){
        return (y1-y2)/(x1-x2);
    }

    public float segmentLineintersection(float m1, float x1, float y1, float x2, float x3, float y2, float y3){
        float Xa=0;
        Xa=findXIntercept(m1,(y2-y3)/(x2-x3),x1,x2,y1,y2);
        if (Xa!=Xa || x2==x3){
            Xa=x3;
            if(findYIntercept(m1,Xa,x1,y1)<Math.min(y2,y3) || findYIntercept(m1,Xa,x1,y1)>Math.max(y2,y3) ){
                return -100;
            }
        }else{
            if (Xa > Math.max(x2, x3) || Xa < Math.min(x2, x3)){
                return -100;
            }
        }

        return Xa;
    }

    public boolean segmentIntersection(float x1, float x2, float y1,float y2, float x3, float x4, float y3, float y4){
        float Xa=0;

        Xa=1/(x3-x4);
        if (Xa!=Xa){
            Xa=x3;
            if(findYIntercept((y1-y2)/(x1-x2),Xa,x1,y1)<Math.min(y3,y4) || findYIntercept((y1-y2)/(x1-x2),Xa,x1,y1)>Math.max(y3,y4) ){
                return false;
            }
        }else{
            if (Math.max(x1,x2)<Math.min(x3,x4)){
                return false;
            }
            if ((y1-y2)/(x1-x2)==(y3-y4)/(x3-x4)){
                return false;
            }

            Xa=findXIntercept((y1-y2)/(x1-x2), (y3-y4)/(x3-x4), x1, x3,y1,y3);

            if (Xa<(Math.max(Math.min(x1,x2),Math.min(x3, x4))) || Xa > Math.min(Math.max(x1,x2), Math.max(x3,x4))){
                return false;
            }
        }

        return true;
    }
    public float findAngle(float x1, float x2, float y1,float y2, float x3, float x4, float y3, float y4){
               /*  dot = x1*x2 + y1*y2      # dot product
            det = x1*y2 - y1*x2      # determinant
            angle = atan2(det, dot)  # atan2(y, x) or atan2(sin, cos)
            */
        float dot = (x1-x2)*(x3-x4)+(y1-y2)*(y3-y4);
        float det = (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4);
        return (float)Math.atan2(det,dot);
    }

    public void nearestPoint(obstacleLine o, float x, float y, int k, int turtleIndex){
        float m=(o.y[1]-o.y[0])/(o.x[1]-o.x[0]);

        tX=(o.y[0]-y-x/m-o.x[0]*m)/((-1/m)-m);

        if (tX<o.x[0]&&tX<o.x[1]){
            if (o.x[0]<o.x[1]){
                tX=o.x[0];
            }
            else{
                tX=o.x[1];
            }
        }
        else if (tX>o.x[0]&&tX>o.x[1]){
            if (o.x[0]>o.x[1]){
                tX=o.x[0];
            }
            else{
                tX=o.x[1];
            }
        }
        tY=m*(tX-o.x[0])+o.y[0];

        m=(y-tY)/(x-tX);
        float x1=-1;
        float x2=1;

        y=tY+.05f*(y-tY)/(float)(Math.sqrt((y-tY)*(y-tY)+(x-tX)*(x-tX)));
        x=tX+.05f*(x-tX)/(float)(Math.sqrt((y-tY)*(y-tY)+(x-tX)*(x-tX)));


        bEquations[turtleIndex].x[k]=x;
        bEquations[turtleIndex].y[k]=y;
        bEquations[turtleIndex].m[k]=-1/m;
        bEquations[turtleIndex].active[k]=true;





        float y1= -1/m*(x1-x)+y;
        float y2= -1/m*(x2-x)+y;



        cd = Math.cos(Math.atan((x1 - x2) / (y1 - y2)));
        cy = Math.sin(Math.atan((x1-x2) / (y1-y2)));


        Coords[0] = x1 + (float) cd * .005f/scale;
        //Coords[0]=Coords[0]/scale;
        Coords[1] = y1 - (float) cy * .005f/scale;
        // Coords[1]=Coords[1]/scale;


        Coords[9] = x2 + (float) cd * .005f/scale;
        // Coords[9]=Coords[9]/scale;
        Coords[10] =  y2 - (float) cy * .005f/scale;
        //Coords[10]=Coords[10]/scale;

        Coords[3] = x1 - (float) cd * .005f/scale;
        // Coords[3]=Coords[3]/scale;
        Coords[4] = y1 + (float) cy * .005f/scale;
        // Coords[4]=Coords[4]/scale;

        Coords[6] = x2 - (float) cd * .005f/scale;
        // Coords[6]=Coords[6]/scale;
        Coords[7] = y2 + (float) cy * .005f/scale;
        // Coords[7]=Coords[7]/scale;

       // bLine[k].setSquareCoords(Coords);
       // bLine[k].active=true;


    }



    //PART OF OBSTACLE PACKAGE

    public void getUniqueVertices(){


        uniqueVoronoiVertices.size=0;

        //GET UNIQUE VERTICES FROM VORONOI EQUATIONS
        for (int i=0; i<vSize+4;i++){
            flag=false;

            for (int j=0;j<uniqueVoronoiVertices.size;j++){
                if (uniqueVoronoiVertices.x[j]==voronoiEquations.x[i] && uniqueVoronoiVertices.y[j]==voronoiEquations.y[i]){
                    flag=true;
                }

            }
            if (flag==false){
                uniqueVoronoiVertices.x[uniqueVoronoiVertices.size]=voronoiEquations.x[i];
                uniqueVoronoiVertices.y[uniqueVoronoiVertices.size]=voronoiEquations.y[i];
                uniqueVoronoiVertices.size=uniqueVoronoiVertices.size+1;

            }

            flag=false;

            for (int j=0;j<uniqueVoronoiVertices.size;j++){
                if (uniqueVoronoiVertices.x[j]==voronoiEquations.x2[i] && uniqueVoronoiVertices.y[j]==voronoiEquations.y2[i]){
                    flag=true;
                }

            }
            if (flag==false){
                uniqueVoronoiVertices.x[uniqueVoronoiVertices.size]=voronoiEquations.x2[i];
                uniqueVoronoiVertices.y[uniqueVoronoiVertices.size]=voronoiEquations.y2[i];
                uniqueVoronoiVertices.size=uniqueVoronoiVertices.size+1;

            }
        }



        //FIND SHORTEST DISTANCE
        for (int i=0;i<uniqueVoronoiVertices.size;i++){
            uniqueVoronoiVertices.dist[i]=100;
            for (int j=0;j<maxBots;j++){
                if (turtleList[j].on==1){
                    float k=(float)Math.sqrt((uniqueVoronoiVertices.x[i]-turtleList[j].x)*(uniqueVoronoiVertices.x[i]-turtleList[j].x)+(uniqueVoronoiVertices.y[i]-turtleList[j].y)*(uniqueVoronoiVertices.y[i]-turtleList[j].y));
                    if (k<uniqueVoronoiVertices.dist[i]){
                        uniqueVoronoiVertices.dist[i]=k;
                    }
                }

            }
        }

        //PUT IN THEIR BOUNDARIES
        for (int j=0;j<maxBots;j++){

            voronoiCellBoundaries[j].size=0;
            tempArray.size=0;
            for (int i=0;i<uniqueVoronoiVertices.size;i++){
                if (turtleList[j].on==1){
                    float k=(float)Math.sqrt((uniqueVoronoiVertices.x[i]-turtleList[j].x)*(uniqueVoronoiVertices.x[i]-turtleList[j].x)+(uniqueVoronoiVertices.y[i]-turtleList[j].y)*(uniqueVoronoiVertices.y[i]-turtleList[j].y));

                    if (k<uniqueVoronoiVertices.dist[i]+uniqueVoronoiVertices.errorBound){
                        tempArray.x[tempArray.size]=uniqueVoronoiVertices.x[i];
                        tempArray.y[tempArray.size]=uniqueVoronoiVertices.y[i];
                        tempArray.angle[tempArray.size]=returnAngle(turtleList[j].x,turtleList[j].y,tempArray.x[tempArray.size],tempArray.y[tempArray.size]);
                        tempArray.used[tempArray.size]=false;
                        tempArray.size=tempArray.size+1;

                    }
                }

            }

            voronoiCellBoundaries[j]=organizeList(tempArray);
        }

        for (int j=0;j<maxBots;j++){
            if(turtleList[j].on==1){
                for (int i=0;i<obstacleLineList.size();i++){
                    tempMax=voronoiCellBoundaries[j].size;

                    for (int k=0;k<tempMax;k++){

                        if (k==0){

                            tempVX=segmentLineintersection(bEquations[j].m[i], bEquations[j].x[i], bEquations[j].y[i], voronoiCellBoundaries[j].x[k], voronoiCellBoundaries[j].x[voronoiCellBoundaries[j].size - 1], voronoiCellBoundaries[j].y[k], voronoiCellBoundaries[j].y[voronoiCellBoundaries[j].size - 1]);

                            if (tempVX!=-100){
                                voronoiCellBoundaries[j].x[voronoiCellBoundaries[j].size]=tempVX;
                                voronoiCellBoundaries[j].y[voronoiCellBoundaries[j].size]=findYIntercept(bEquations[j].m[i], tempVX, bEquations[j].x[i], bEquations[j].y[i]);
                                voronoiCellBoundaries[j].used[voronoiCellBoundaries[j].size]=false;
                                voronoiCellBoundaries[j].angle[voronoiCellBoundaries[j].size]=returnAngle(turtleList[j].x,turtleList[j].y,voronoiCellBoundaries[j].x[voronoiCellBoundaries[j].size],voronoiCellBoundaries[j].y[voronoiCellBoundaries[j].size]);
                                voronoiCellBoundaries[j].size=voronoiCellBoundaries[j].size+1;
                            }

                        }
                        else {
                            tempVX=segmentLineintersection(bEquations[j].m[i], bEquations[j].x[i], bEquations[j].y[i], voronoiCellBoundaries[j].x[k], voronoiCellBoundaries[j].x[k - 1], voronoiCellBoundaries[j].y[k], voronoiCellBoundaries[j].y[k - 1]);

                            if (tempVX!=-100){
                                voronoiCellBoundaries[j].x[voronoiCellBoundaries[j].size]=tempVX;
                                voronoiCellBoundaries[j].y[voronoiCellBoundaries[j].size]=findYIntercept(bEquations[j].m[i], tempVX, bEquations[j].x[i], bEquations[j].y[i]);
                                voronoiCellBoundaries[j].angle[voronoiCellBoundaries[j].size]=returnAngle(turtleList[j].x, turtleList[j].y, voronoiCellBoundaries[j].x[voronoiCellBoundaries[j].size], voronoiCellBoundaries[j].y[voronoiCellBoundaries[j].size]);
                                voronoiCellBoundaries[j].used[voronoiCellBoundaries[j].size]=false;
                                voronoiCellBoundaries[j].size=voronoiCellBoundaries[j].size+1;
                            }
                        }
                    voronoiCellBoundaries[j].used[k]=false;

                    }








                    if (findYIntercept(bEquations[j].m[i],turtleList[j].x,bEquations[j].x[i],bEquations[j].y[i])<turtleList[j].y){ //DELETE VERTICES BELOW EQUATION
                        flag=false;

                        for (int k=0;k<voronoiCellBoundaries[j].size;k++){

                            if (findYIntercept(bEquations[j].m[i],voronoiCellBoundaries[j].x[k],bEquations[j].x[i],bEquations[j].y[i])>voronoiCellBoundaries[j].y[k]){ //THE VORONOI POINT IS BELOW EQUATION
                                for (int g=k+1;g<voronoiCellBoundaries[j].size;g++){
                                    voronoiCellBoundaries[j].x[g-1]=voronoiCellBoundaries[j].x[g];
                                    voronoiCellBoundaries[j].y[g-1]=voronoiCellBoundaries[j].y[g];
                                    voronoiCellBoundaries[j].angle[g-1]=voronoiCellBoundaries[j].angle[g];
                                }
                                voronoiCellBoundaries[j].size=voronoiCellBoundaries[j].size-1;
                                k=k-1;
                                flag=true;
                            }


                        }


                    }
                    else{
                        for (int k=0;k<voronoiCellBoundaries[j].size;k++){
                            if (findYIntercept(bEquations[j].m[i],voronoiCellBoundaries[j].x[k],bEquations[j].x[i],bEquations[j].y[i])<voronoiCellBoundaries[j].y[k]){ //THE VORONOI POINT IS ABOVE EQUATION
                                for (int g=k+1;g<voronoiCellBoundaries[j].size;g++){
                                    voronoiCellBoundaries[j].x[g-1]=voronoiCellBoundaries[j].x[g];
                                    voronoiCellBoundaries[j].y[g-1]=voronoiCellBoundaries[j].y[g];
                                    voronoiCellBoundaries[j].angle[g-1]=voronoiCellBoundaries[j].angle[g];

                                }
                                voronoiCellBoundaries[j].size=voronoiCellBoundaries[j].size-1;
                                k=k-1;

                            }

                        }


                    }
                    tempArray=voronoiCellBoundaries[j];
                    voronoiCellBoundaries[j]=organizeList(tempArray);
                }
            }


            for (int g=0;g<voronoiCellBoundaries[j].size;g++){
                //System.out.println("g: "+g);
                //System.out.println("VCELL BOUNDARIES X "+ voronoiCellBoundaries[j].x[g]);
                //System.out.println("VCELL BOUNDARIES y "+ voronoiCellBoundaries[j].y[g]);
            }
        }

        int c=0;
        float x1,x2;
        float y1,y2;

        for (int j=0;j<100;j++){
            bLine[j].active=false;
        }

        for (int j=0;j<maxBots;j++){
            for (int i=0;i<voronoiCellBoundaries[j].size;i++){


                if (turtleList[j].on==1){


                    if (i==0){
                        x1=voronoiCellBoundaries[j].x[i];
                        x2=voronoiCellBoundaries[j].x[voronoiCellBoundaries[j].size-1];
                        y1=voronoiCellBoundaries[j].y[i];
                        y2=voronoiCellBoundaries[j].y[voronoiCellBoundaries[j].size-1];
                    }
                    else{
                        x1=voronoiCellBoundaries[j].x[i];
                        x2=voronoiCellBoundaries[j].x[i-1];
                        y1=voronoiCellBoundaries[j].y[i];
                        y2=voronoiCellBoundaries[j].y[i-1];

                    }

                    cd = Math.cos(Math.atan((x1 - x2) / (y1 - y2)));
                    cy = Math.sin(Math.atan((x1-x2) / (y1-y2)));


                    Coords[0] = x1 + (float) cd * .005f/scale;
                    //Coords[0]=Coords[0]/scale;
                    Coords[1] = y1 - (float) cy * .005f/scale;
                    // Coords[1]=Coords[1]/scale;


                    Coords[9] = x2 + (float) cd * .005f/scale;
                    // Coords[9]=Coords[9]/scale;
                    Coords[10] =  y2 - (float) cy * .005f/scale;
                    //Coords[10]=Coords[10]/scale;

                    Coords[3] = x1 - (float) cd * .005f/scale;
                    // Coords[3]=Coords[3]/scale;
                    Coords[4] = y1 + (float) cy * .005f/scale;
                    // Coords[4]=Coords[4]/scale;

                    Coords[6] = x2 - (float) cd * .005f/scale;
                    // Coords[6]=Coords[6]/scale;
                    Coords[7] = y2 + (float) cy * .005f/scale;
                    // Coords[7]=Coords[7]/scale;

                    bLine[c].setSquareCoords(Coords);
                    bLine[c].active=true;
                    c++;


                }



            }

        }




    }

    public vertexArray insertionSort(vertexArray v){
        tempArrayInsertion = v;
        int j;


        for (int i=1;i<tempArrayInsertion.size-1;i++){
            j=i;
            insertionX=tempArrayInsertion.x[j];
            insertionY=tempArrayInsertion.y[j];
            insertionAngle=tempArrayInsertion.angle[j];


            while (j>0 && tempArrayInsertion.angle[j-1]>insertionAngle){

                tempArrayInsertion.x[j]=tempArrayInsertion.x[j-1];
                tempArrayInsertion.y[j]=tempArrayInsertion.y[j-1];
                tempArrayInsertion.angle[j]=tempArrayInsertion.angle[j-1];
                j=j-1;
            }
            tempArrayInsertion.x[j]=insertionX;
            tempArrayInsertion.y[j]=insertionY;
            tempArrayInsertion.angle[j]=insertionAngle;


        }

        return tempArrayInsertion;

    }


    public vertexArray organizeList(vertexArray v){
        vertexArray t= new vertexArray();

        float angle;
        int  z=-1;
        t.size=0;
        for (int i=0;i<v.size;i++){
            angle=360;
            for (int k=0;k<v.size;k++){
                if (v.angle[k]<angle && v.used[k]!=true){
                    z=k;
                    angle=v.angle[k];
                }

            }
            v.used[z]=true;
            t.x[t.size]=v.x[z];
            t.y[t.size]=v.y[z];

            t.angle[t.size]=v.angle[z];
            t.size=t.size+1;

        }

        return t;


    }


    public float returnAngle(float turtleX, float turtleY, float X, float Y){

        return (float)Math.atan2(1 * (Y - turtleY) - 0 * (X - turtleX), 1 * (X - turtleX) + 0 * (Y - turtleY));



    }

    public float dist(float x1, float x2, float y1, float y2) {
        return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }


    public float returnTwoVectorAngle(float vx, float vy, float x1, float y1, float x2, float y2){
        float X1=(x1-vx);
        float X2=(x2-vx);
        float Y1=(y1-vy);
        float Y2=(y2-vy);


        float dot = (X1*X2+Y1*Y2);
        float det = (X1*Y2-Y1*X2);
        return (float)Math.atan2(det, dot);
    }

    public void findGeoDistance(){

        float d1=.75f;


        if(!obstacleLineList.isEmpty()){

            int nz=1;
            int nk=0;
            int sx;
            int sy;
            float dist1;
            float x3,y3;
            float x2=gaussArrayList.locX[0];
            float y2=gaussArrayList.locY[0];
            float m=(obstacleLineList.get(0).y[nz]-y2)/(obstacleLineList.get(0).x[nz]-x2);	//THE SLOPE BETWEEN THE OBSTACLE POINT AND GOAL
            float m3=(obstacleLineList.get(0).y[1]-obstacleLineList.get(0).y[0])/(obstacleLineList.get(0).x[1]-obstacleLineList.get(0).x[0]);
            float m2;
            float alpha;
            float ang1;
            float ang2;
            float eD;
            float x1;
            float y1;
            float intersect1;
            float intersect2;
            boolean oFlag=false;

            float d2=dist(obstacleLineList.get(0).x[nz] , gaussArrayList.locX[0],obstacleLineList.get(0).y[nz],gaussArrayList.locY[0] );
            for(int i =0;i<100;i++){
                for(int j=0;j<100;j++){

                    oFlag=false;
                    x1=1-2*(i/100f);
                    y1=1-2*(j/100f);

                    intersect1=findYIntercept(m,x1,obstacleLineList.get(0).x[nz],obstacleLineList.get(0).y[nz]);
                    intersect2=findYIntercept(m,obstacleLineList.get(0).x[nk],(obstacleLineList.get(0).x[nz]),(obstacleLineList.get(0).y[nz]));
                    if (intersect1<y1){

                        if (intersect2<obstacleLineList.get(0).y[nk]){
                            oFlag=true;
                            if (i==99){
                                if(j==49){

                                }
                            }

                        }
                        else{
                            oFlag=false;
                            if (i==99){
                                if(j==49){

                                }
                            }

                        }
                    }
                    else{

                        if (intersect2>obstacleLineList.get(0).y[nk]){
                            oFlag=true;
                            if (i==99){
                                if(j==49){

                                }
                            }
                        }
                        else{
                            oFlag=false;
                            if (i==99){
                                if(j==49){


                                }
                            }

                        }

                    }

                    if (oFlag==true && ((findYIntercept(m3,x1,obstacleLineList.get(0).x[nz],obstacleLineList.get(0).y[nz])<y1  && findYIntercept(m3,x2,obstacleLineList.get(0).x[nz],obstacleLineList.get(0).y[nz])>y2) || (findYIntercept(m3,x1,obstacleLineList.get(0).x[nz],obstacleLineList.get(0).y[nz])>y1
                            &&findYIntercept(m3,x2,obstacleLineList.get(0).x[nz],obstacleLineList.get(0).y[nz])<y2))){



                        m2=(obstacleLineList.get(0).y[nz]-y1)/(obstacleLineList.get(0).x[nz]-x1);



                        if (obstacleLineList.get(0).y[nz]>y1){
                            sy = 1;
                        } else {
                            sy = -1;
                        }

                        if (obstacleLineList.get(0).x[nz] > x1) {
                            sx=1;
                        } else {
                            sx = -1;
                        }


                        ang1 = (180 - 57.2958f * Math.abs(returnTwoVectorAngle(-obstacleLineList.get(0).x[nz],obstacleLineList.get(0).y[nz],-obstacleLineList.get(0).x[nk],obstacleLineList.get(0).y[nk],-x2,y2)));
                        ang2=(180-57.2958f*Math.abs(returnTwoVectorAngle(-obstacleLineList.get(0).x[nz],obstacleLineList.get(0).y[nz],-x1,y1,-x2,y2)));
                        alpha =1-(ang1-ang2)/ang1;
                        eD=(d2+(d1-d2)*alpha);



                        x3=obstacleLineList.get(0).x[nz]+sx*(float)Math.abs(eD*Math.cos(Math.atan(m2)));
                        y3=obstacleLineList.get(0).y[nz]+sy*(float)Math.abs(eD*Math.sin(Math.atan(m2)));




                        dist1=(float)Math.sqrt((x3-x2)*(x3-x2)+(y3-y2)*(y3-y2));
                        dist1=dist1+(float)Math.sqrt((x3-x1)*(x3-x1)+(y3-y1)*(y3-y1));

                        geoDistance[i][j]=dist1;


                    }
                    else{
                        geoDistance[i][j]=(float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

                    }



                }
            }
        }




    }

    public void clearObstacles(){
        obstacleMapPoints.clear();
    }




    public void calculateAdjacencyMatrix(){

        int uniqueVertices=0;

        uniqueVertices=uniqueVertices+obstacleMapPoints.size();



        float xR1, xR2, yR1, yR2;  //Obstacle points trying to determine connection
        float xO1, xO2, yO1, yO2;  //Possible obstructions

        for(int i=0;i<uniqueVertices;i++){
            for(int j=0;j<uniqueVertices;j++){
                if(i!=j){  // segmentIntersection(float x1, float x2, float y1,float y2, float x3, float x4, float y3, float y4)
                    xR1=obstacleMapPoints.get(i).x;
                    xR2=obstacleMapPoints.get(j).x;
                    yR1=obstacleMapPoints.get(i).y;
                    yR2=obstacleMapPoints.get(j).y;

                    flag=false;

                    for (int k=0;k<obstacleLineList.size();k++){
                        xO1=obstacleLineList.get(k).x[0];
                        yO1=obstacleLineList.get(k).y[0];
                        xO2=obstacleLineList.get(k).x[1];
                        yO2=obstacleLineList.get(k).y[1];


                        if((thresholdDist(xR1,xO1) && thresholdDist(yR1,yO1)) || (thresholdDist(xR1,xO2) && thresholdDist(yR1,yO2)) || (thresholdDist(xR2,xO1) && thresholdDist(yR2,yO1)) || (thresholdDist(xR2,xO2) && thresholdDist(yR2,yO2))){

                            //Check if these are two points on the same line

                            if (k>0 && k<obstacleLineList.size()-1 && (((thresholdDist(xR1,xO1) && thresholdDist(yR1,yO1)) && (thresholdDist(xR2,xO2) && thresholdDist(yR2,yO2))) || ((thresholdDist(xR2,xO1) && thresholdDist(yR2,yO1)) && (thresholdDist(xR1,xO2) && thresholdDist(yR1,yO2))) )){

                                if (    returnTwoVectorAngle(obstacleLineList.get(k-1).x[1], obstacleLineList.get(k-1).y[1], obstacleLineList.get(k-1).x[0], obstacleLineList.get(k-1).y[0], obstacleLineList.get(k).x[1], obstacleLineList.get(k).y[1])  >   0   ){
                                    //THIS HAPPENS IF ANGLE IS POSITIVE
                                    if (    returnTwoVectorAngle(obstacleLineList.get(k).x[1], obstacleLineList.get(k).y[1], obstacleLineList.get(k).x[0], obstacleLineList.get(k).y[0], obstacleLineList.get(k+1).x[1], obstacleLineList.get(k+1).y[1])  <   0   ){
                                        flag=true;
                                    }
                                }

                                if (    returnTwoVectorAngle(obstacleLineList.get(k-1).x[1], obstacleLineList.get(k-1).y[1], obstacleLineList.get(k-1).x[0], obstacleLineList.get(k-1).y[0], obstacleLineList.get(k).x[1], obstacleLineList.get(k).y[1])  <   0   ){
                                    //THIS HAPPENS IF ANGLE IS Negative
                                    if (    returnTwoVectorAngle(obstacleLineList.get(k).x[1], obstacleLineList.get(k).y[1], obstacleLineList.get(k).x[0], obstacleLineList.get(k).y[0], obstacleLineList.get(k+1).x[1], obstacleLineList.get(k+1).y[1])  >   0   ){
                                        flag=true;
                                    }
                                }




                            }







                        }else{
                            if (segmentIntersection(xR1,xR2,yR1,yR2,xO1,xO2,yO1,yO2)){
                                flag=true;
                            }

                        }
                    }


                    if (obstacleMapPoints.get(i).linkSize>1){
                        if (concaveTest(obstacleMapPoints.get(i).angle[0],obstacleMapPoints.get(i).angle[1],returnTwoVectorAngle(xR1, yR1, xR1 + 1, yR1, xR2, yR2))){
                            flag=true;
                        }
                    }
                    if (obstacleMapPoints.get(j).linkSize>1){
                        if (concaveTest(obstacleMapPoints.get(j).angle[0],obstacleMapPoints.get(j).angle[1],returnTwoVectorAngle(xR2, yR2, xR2 + 1, yR2, xR1, yR1))){
                            flag=true;
                        }
                    }

                    if(flag==false){
                        for(int k=0;k<maxBots;k++){
                            if(turtleList[k].on==1){
                                adjacencyMatrix[k][i+2][j+2]=1;
                                adjacencyMatrix[k][j+2][i+2]=1;
                            }
                        }
                    }

                }
            }
        }




    }

    public boolean concaveTest(float angle1, float angle2, float angle3){
        //TEST TO SEE IF ANGLE 3 IS WITHIN ACUTE PORTION OF ANGLE 1 AND ANGLE 2

        if (Math.max(angle1,angle2)-Math.min(angle1,angle2) > 3.14){
            if (angle3 > Math.max(angle1,angle2) || angle3<Math.min(angle1,angle2)){
                return true;
            }
        }else{
            if (angle3 < Math.max(angle1,angle2) && angle3 > Math.min(angle1,angle2)){
                return true;
            }
        }

        return false;
    }

    public boolean thresholdDist(float x,float y){
        float threshold =.01f;
        if (x>=y-threshold && x<=y+threshold){
            return true;
        }
        return false;
    }

    public void printAdjacencyMatrix(){
        int p=0;
        for(int k=0;k<maxBots;k++){
            if(turtleList[k].on==1){
                for(int i=0;i<maxObstacleSize*2+1;i++){
                    for(int j=0;i<maxObstacleSize*2+1;j++){
                        if(adjacencyMatrix[k][i][j]==1)
                            p++;
                    }
                }

            }
        }



    }

    public void calculateAdjacencyMatrixGoal(){


        int uniqueVertices=0;

        uniqueVertices=uniqueVertices+obstacleMapPoints.size();

        float xR1, xR2, yR1, yR2;  //Obstacle points trying to determine connection
        float xO1, xO2, yO1, yO2;  //Possible obstructions

        for(int i=0;i<uniqueVertices;i++){


            for(int k=0;k<maxBots;k++){
                if(turtleList[k].on==1){
                    //THIS 55 IS TEMP!!!!!
                    adjacencyMatrix[k][i+2][1]=0;
                    adjacencyMatrix[k][1][i+2]=0;
                    adjacencyMatrix[k][i+2][0]=0;
                    adjacencyMatrix[k][0][i+2]=0;

                }
            }

                    xR1=gaussArrayList.locX[0];
                    xR2=obstacleMapPoints.get(i).x;
                    yR1=gaussArrayList.locY[0];
                    yR2=obstacleMapPoints.get(i).y;

                    flag=false;

                    for (int k=0;k<obstacleLineList.size();k++){
                        xO1=obstacleLineList.get(k).x[0];
                        yO1=obstacleLineList.get(k).y[0];
                        xO2=obstacleLineList.get(k).x[1];
                        yO2=obstacleLineList.get(k).y[1];


                        if((thresholdDist(xR1,xO1) && thresholdDist(yR1,yO1)) || (thresholdDist(xR1,xO2) && thresholdDist(yR1,yO2)) || (thresholdDist(xR2,xO1) && thresholdDist(yR2,yO1)) || (thresholdDist(xR2,xO2) && thresholdDist(yR2,yO2))){

                        }else{
                            if (segmentIntersection(xR1, xR2, yR1, yR2, xO1, xO2, yO1, yO2)){
                                flag=true;
                            }
                            Log.d("myOutput", "<Aaron>  ----| i: " + i+ " angles:  "+ obstacleMapPoints.get(i).linkSize + "\n");

                        }

                    }

            if (obstacleMapPoints.get(i).linkSize>1){

                Log.d("myOutput", "<Aaron>  ----| i: " + i+ " angle:  "+ obstacleMapPoints.get(i).angle[0] + " angle2: "+obstacleMapPoints.get(i).angle[1]+"\n");

                if (concaveTest(obstacleMapPoints.get(i).angle[0],obstacleMapPoints.get(i).angle[1],returnTwoVectorAngle(xR2, yR2, xR2 + 1, yR2, xR1, yR1))){
                    flag=true;
                }
            }

                    if(flag==false){
                        for(int k=0;k<maxBots;k++){
                            if(turtleList[k].on==1){
                                //THIS 55 IS TEMP!!!!!
                                adjacencyMatrix[k][i+2][1]=1;
                                adjacencyMatrix[k][1][i+2]=1;
                                // Log.d("myOutput", "<Aaron>  ----| j: "+ j + ", i: "+i);
                            }
                        }
                    }

                }


        xR1=0;
        xR2=0;
        yR1=0;
        yR2=0;


        for(int j=0;j<maxBots;j++) {
            if (turtleList[j].on == 1) {
                xR1 = turtleList[j].x;
                yR1 = turtleList[j].y;
                adjacencyMatrix[j][1][0] = 0;
                adjacencyMatrix[j][0][1] = 0;
                adjacencyMatrix[j][1][0] = 0;
                adjacencyMatrix[j][0][1] = 0;
                for (int i = 0; i < uniqueVertices + 1; i++) {

                        adjacencyMatrix[j][i + 2][0] = 0;
                        adjacencyMatrix[j][0][i + 2] = 0;
                        adjacencyMatrix[j][i + 2][0] = 0;
                        adjacencyMatrix[j][0][i + 2] = 0;

                    if (i == uniqueVertices) {
                        xR2 = gaussArrayList.locX[0];
                        yR2 = gaussArrayList.locY[0];
                    } else {
                        xR2 = obstacleMapPoints.get(i).x;
                        yR2 = obstacleMapPoints.get(i).y;
                    }


                    flag = false;
                    for (int k = 0; k < obstacleLineList.size(); k++) {
                        xO1 = obstacleLineList.get(k).x[0];
                        yO1 = obstacleLineList.get(k).y[0];
                        xO2 = obstacleLineList.get(k).x[1];
                        yO2 = obstacleLineList.get(k).y[1];


                        if ((thresholdDist(xR1, xO1) && thresholdDist(yR1, yO1)) || (thresholdDist(xR1, xO2) && thresholdDist(yR1, yO2)) || (thresholdDist(xR2, xO1) && thresholdDist(yR2, yO1)) || (thresholdDist(xR2, xO2) && thresholdDist(yR2, yO2))) {

                        } else {
                            if (segmentIntersection(xR1, xR2, yR1, yR2, xO1, xO2, yO1, yO2)) {
                                flag = true;
                            }
                        }
                    }

                    if (i != uniqueVertices) {
                        if (obstacleMapPoints.get(i).linkSize > 1) {
                            if (concaveTest(obstacleMapPoints.get(i).angle[0], obstacleMapPoints.get(i).angle[1], returnTwoVectorAngle(xR2, yR2, xR2 + 1, yR2, xR1, yR1))) {
                                flag = true;
                            }
                        }
                    }

                    if (flag == false) {
                        if (i == uniqueVertices) {
                            adjacencyMatrix[j][1][0] = 1;
                            adjacencyMatrix[j][0][1] = 1;
                        } else {
                            adjacencyMatrix[j][i + 2][0] = 1;
                            adjacencyMatrix[j][0][i + 2] = 1;
                        }
                }

                }
            }
        }






/*
        int c=0;
        float x1,x2;
        float y1,y2;

        for (int j=0;j<100;j++){
            bLine[j].active=false;
        }



        for (int k=0;k<maxBots;k++){
            for (int i=0;i<56;i++){
                for (int j=0;j<56;j++){
                    if (turtleList[k].on==1 && adjacencyMatrix[k][i][j]==1){


                        if (i==1){
                            x1=gaussArrayList.locX[0];
                            y1=gaussArrayList.locY[0];

                        }else if(i==0){
                            x1=xR1;
                            y1=yR1;
                        }else{
                            x1=obstacleMapPoints.get(i-2).x;
                            y1=obstacleMapPoints.get(i-2).y;
                        }


                        if (j==1){
                            x2=gaussArrayList.locX[0];
                            y2=gaussArrayList.locY[0];

                        }else if(j==0){
                            x2=xR1;
                            y2=yR1;
                        }else{
                            x2=obstacleMapPoints.get(j-2).x;
                            y2=obstacleMapPoints.get(j-2).y;
                        }






                        cd = Math.cos(Math.atan((x1 - x2) / (y1 - y2)));
                        cy = Math.sin(Math.atan((x1-x2) / (y1-y2)));


                        Coords[0] = x1 + (float) cd * .005f/scale;
                        //Coords[0]=Coords[0]/scale;
                        Coords[1] = y1 - (float) cy * .005f/scale;
                        // Coords[1]=Coords[1]/scale;


                        Coords[9] = x2 + (float) cd * .005f/scale;
                        // Coords[9]=Coords[9]/scale;
                        Coords[10] =  y2 - (float) cy * .005f/scale;
                        //Coords[10]=Coords[10]/scale;

                        Coords[3] = x1 - (float) cd * .005f/scale;
                        // Coords[3]=Coords[3]/scale;
                        Coords[4] = y1 + (float) cy * .005f/scale;
                        // Coords[4]=Coords[4]/scale;

                        Coords[6] = x2 - (float) cd * .005f/scale;
                        // Coords[6]=Coords[6]/scale;
                        Coords[7] = y2 + (float) cy * .005f/scale;
                        // Coords[7]=Coords[7]/scale;

                        bLine[c].setSquareCoords(Coords);
                        bLine[c].active=true;
                        c++;


                    }
                }




            }



        }*/

    }

    public void calculateAdjacencyMatrixTurtle(){
        for (int j=0;j<maxBots;j++){
            if(turtleList[j].on==1){

            }
        }
    }

    public void Dijkstra() {


        int c=0;
        float x1,x2;
        float y1,y2;


        for (int i=0;i<100;i++){
            pathLine[i].active=false;
        }

        int setSize = obstacleMapPoints.size() + 2;
        int path[]= new int[setSize];

        Map<Integer, pointXY> Q = new HashMap<>();
        Map<Integer, pointXY> K = new HashMap<>();
        Set<Integer> setQ = new HashSet<>();

        int u;
        //pointXY u= new pointXY();
        pointXY source[] = new pointXY[maxBots];
        for (int i=0;i<maxBots;i++){
            source[i]= new pointXY();
        }

        pointXY goal = new pointXY();


        float   distArray[]  =   new float[setSize];
        int     prev[]  =   new int[setSize];

        for (int i=0;i<setSize;i++){
            distArray[i]=100000;
            prev[i]=-1;
            path[i]=0;
        }

        float   compareDist=    10000f;
        float tempDist=0;

        goal.x=gaussArrayList.locX[0];
        goal.y=gaussArrayList.locY[0];




        //CREATE SET OF VERTICES Q
        for (int j =0; j<maxBots;j++){
            if (turtleList[j].on==1){
                // INIT
                K.clear();
                Q.clear();
                setQ.clear();

                distArray[1]=100000;
                prev[0]=-1; //-1 ~ UNDEFINED
                prev[1]=-1;
                u=-1;


                source[j].x=turtleList[j].x;
                source[j].y=turtleList[j].y;


                for (int i = 2; i < setSize; i++) {
                    Q.put(i,obstacleMapPoints.get(i-2));
                    K.put(i,obstacleMapPoints.get(i-2));
                    setQ.add(i);
                    prev[i]=-1;
                    distArray[i]=1000000;
                }
                Q.put(1, source[j]);
                setQ.add(1);
                K.put(1, source[j]);
                Q.put(0, goal);
                setQ.add(0);
                K.put(0, goal);
                distArray[0]=0;

                //MAIN

                while(!Q.isEmpty()){
                    //Find min u
                    compareDist=10000;
                    u=1;

                    for (int i=0;i<setSize;i++){
                        if(distArray[i]<compareDist  &&  Q.containsKey(i)){
                            u=i;
                            compareDist=distArray[i];
                        }
                    }

                    Q.remove(u);
                    if(u==1){
                        break;
                    }

                        for (int v=0;v<setSize;v++){
                            if (adjacencyMatrix[j][v][u]==1){
                                tempDist = distArray[u]+dist(K.get(u).x,K.get(v).x,K.get(u).y,K.get(v).y);

                                if(tempDist < distArray[v]){
                                    distArray[v]=tempDist;
                                    prev[v]=u;
                                }
                            }
                        }
                }
                //Output the Shortest path
                //Output the Shortest path
                shortestPaths[j].clear();

                for (int g=0;g<setSize;g++){

                }


                int kk=0;

                while (prev[u]!=-1){
                        shortestPaths[j].add(0, K.get(u));
             //       Log.d("myOutput", "<Aaron>  ----| U: " + u) ;


                        path[kk]=u;
                        kk++;
                    u=prev[u];

                }
                    shortestPaths[j].add(0, K.get(u));
                    path[kk]=u;
          //      Log.d("myOutput", "<Aaron>  ----| U: " + u) ;









                for (int p=1;p<shortestPaths[j].size();p++){



                            x1=shortestPaths[j].get(p).x;
                            y1=shortestPaths[j].get(p).y;

                            x2=shortestPaths[j].get(p-1).x;
                            y2=shortestPaths[j].get(p-1).y;



                    if (path[p]>1){
                        x1=obstacleMapPoints.get(path[p]-2).x;
                        y1=obstacleMapPoints.get(path[p]-2).y;
                    }else if(path[p]>0){
                        x1=gaussArrayList.locX[0];
                        y1=gaussArrayList.locY[0];
                    }else{

                        x1=turtleList[j].x;
                        y1=turtleList[j].y;
                    }


                    if (path[p-1]>1){
                        x2=obstacleMapPoints.get(path[p-1]-2).x;
                        y2=obstacleMapPoints.get(path[p-1]-2).y;
                    }else if(path[p-1]>0){
                        x2=gaussArrayList.locX[0];
                        y2=gaussArrayList.locY[0];
                    }else{
                        x2=turtleList[j].x;
                        y2=turtleList[j].y;
                    }



                            cd = Math.cos(Math.atan((x1 - x2) / (y1 - y2)));
                            cy = Math.sin(Math.atan((x1-x2) / (y1-y2)));


                            Coords[0] = x1 + (float) cd * .005f/scale;
                            //Coords[0]=Coords[0]/scale;
                            Coords[1] = y1 - (float) cy * .005f/scale;
                            // Coords[1]=Coords[1]/scale;


                            Coords[9] = x2 + (float) cd * .005f/scale;
                            // Coords[9]=Coords[9]/scale;
                            Coords[10] =  y2 - (float) cy * .005f/scale;
                            //Coords[10]=Coords[10]/scale;

                            Coords[3] = x1 - (float) cd * .005f/scale;
                            // Coords[3]=Coords[3]/scale;
                            Coords[4] = y1 + (float) cy * .005f/scale;
                            // Coords[4]=Coords[4]/scale;

                            Coords[6] = x2 - (float) cd * .005f/scale;
                            // Coords[6]=Coords[6]/scale;
                            Coords[7] = y2 + (float) cy * .005f/scale;
                            // Coords[7]=Coords[7]/scale;

                            pathLine[c].setSquareCoords(Coords);
                            pathLine[c].active=true;
                            c++;


                        }

            }




        }

    }

    private static BigDecimal truncateDecimal(float x,int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_HALF_DOWN);
        }
    }

    void refreshFormationtext(){
        textListFormation.get(1).setText("Active Robots #: " + truncateDecimal(formation.val1,1));
        if (formation.val2==3){
            textListFormation.get(2).setText("Type: Triangle");
        }else{
            textListFormation.get(2).setText("Type: Custom");
        }
        textListFormation.get(3).setText("Scale: " + truncateDecimal(formation.val3,1));
        textListFormation.get(4).setText("Rotation: " + truncateDecimal(formation.val4*180/3.14f,1));

    }

    void resetFormation(){

        if (formation.val1==1){
            if (!setRelativeToTurtle(0,4)){
                relative_formation_locations.x[0]=0;
                relative_formation_locations.y[0]=0;
            }

            for (int i=0;i<formation.val1;i++){
                formation_locations.x[i]=formation.x+relative_formation_locations.x[i];
                formation_locations.y[i]=formation.y+relative_formation_locations.y[i];
            }
        }else if(formation.val1==2){
            if (!setRelativeToTurtle(0,4)){
                relative_formation_locations.x[0]=-.1f;
                relative_formation_locations.y[0]=0;
            }
            if (!setRelativeToTurtle(1,8)){
                relative_formation_locations.x[1]=.1f;
                relative_formation_locations.y[1]=0;
            }

            for (int i=0;i<formation.val1;i++){
                formation_locations.x[i]=formation.x+relative_formation_locations.x[i];
                formation_locations.y[i]=formation.y+relative_formation_locations.y[i];
            }
        }
        else if(formation.val1==3){
            if (!setRelativeToTurtle(0,4)) {
                relative_formation_locations.x[0]=-.1f;
                relative_formation_locations.y[0]=-.1f;
            }

            if (!setRelativeToTurtle(1,8)) {
                relative_formation_locations.x[1]=0f;
                relative_formation_locations.y[1]=.1f;
            }

            if (!setRelativeToTurtle(2,1)) {
                relative_formation_locations.x[2] = .1f;
                relative_formation_locations.y[2] = -.1f;
            }

            for (int i=0;i<formation.val1;i++){
                formation_locations.x[i]=formation.x+relative_formation_locations.x[i];
                formation_locations.y[i]=formation.y+relative_formation_locations.y[i];
            }
        }
        else if(formation.val1==4){

            if (!setRelativeToTurtle(0,4)) {
                relative_formation_locations.x[0]=-.1f;
                relative_formation_locations.y[0]=-.1f;
            }

            if (!setRelativeToTurtle(1,8)) {
                relative_formation_locations.x[1] = -.1f;
                relative_formation_locations.y[1] = .1f;
            }

            if (!setRelativeToTurtle(2,1)) {
                relative_formation_locations.x[2] = .1f;
                relative_formation_locations.y[2] = .1f;
            }

            if (!setRelativeToTurtle(3,5)) {
                relative_formation_locations.x[3] = .1f;
                relative_formation_locations.y[3] = -.1f;
            }

            for (int i=0;i<formation.val1;i++){
                formation_locations.x[i]=formation.x+relative_formation_locations.x[i];
                formation_locations.y[i]=formation.y+relative_formation_locations.y[i];
            }

        }
        else if(formation.val1==5){

            if (!setRelativeToTurtle(0,4)) {
                relative_formation_locations.x[0] = -.1f;
                relative_formation_locations.y[0] = -.1f;
            }

            if (!setRelativeToTurtle(1,8)) {
                relative_formation_locations.x[1] = -.1f;
                relative_formation_locations.y[1] = .1f;
            }

            if (!setRelativeToTurtle(2,1)) {
                relative_formation_locations.x[2] = .1f;
                relative_formation_locations.y[2] = .1f;
            }

            if (!setRelativeToTurtle(3,5)) {
                relative_formation_locations.x[3] = .1f;
                relative_formation_locations.y[3] = -.1f;
            }

            if (!setRelativeToTurtle(4,2)) {
                relative_formation_locations.x[4] = 0f;
                relative_formation_locations.y[4] = 0f;
            }

            for (int i=0;i<formation.val1;i++){
                formation_locations.x[i]=formation.x+relative_formation_locations.x[i];
                formation_locations.y[i]=formation.y+relative_formation_locations.y[i];
            }
        }
    }

    boolean setRelativeToTurtle(int relative_index, int turtle_index){
        if (turtleList[turtle_index].getOn()==1){
            relative_formation_locations.x[relative_index]=turtleList[turtle_index].x-formation.x;
            relative_formation_locations.y[relative_index]=turtleList[turtle_index].y-formation.y;
            return true;
        }
        return false;
    }

    void justifyFormationLocations(){
        for (int i=0;i<formation_locations.size;i++){
            formation_locations.x[i]=formation.x+formation.val3*(relative_formation_locations.x[i]*(float) Math.cos(formation.val4)-relative_formation_locations.y[i]*(float) Math.sin(formation.val4));
            formation_locations.y[i]=formation.y+formation.val3*(relative_formation_locations.x[i]*(float) Math.sin(formation.val4)+relative_formation_locations.y[i]*(float) Math.cos(formation.val4));
        }

    }

    void justifyRelativePositions(){
        for (int i=0;i<formation_locations.size;i++){
            relative_formation_locations.x[i]=formation_locations.x[i]-formation.x;
            relative_formation_locations.y[i]=formation_locations.y[i]-formation.y;
        }
    }

    void resetScaleAndRotation(){
        formation.val3=1;
        formation.val4=0;
    }

    void refreshCenter(){
        formation.x=0;
        formation.y=0;
        for (int i=0;i<formation.val1;i++){
            formation.x+=formation_locations.x[i];
            formation.y+=formation_locations.y[i];
        }
        formation.x/=formation.val1;
        formation.y/=formation.val1;
    }


}