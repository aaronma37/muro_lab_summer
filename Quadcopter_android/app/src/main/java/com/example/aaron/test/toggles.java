package com.example.aaron.test;

/**
 * Created by aaron on 7/6/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


/**
 * Created by aaron on 6/16/15.
 */

public class toggles
{
    //Reference to Activity Context
    private final Context mActivityContext;

    //Added for Textures
    private final FloatBuffer mCubeTextureCoordinates;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private final int mTextureCoordinateDataSize = 2;
    private int mTextureDataHandle, selectedTextureDataHandle, selectedTextureDataHandle2, selectedTextureDataHandle3;
    public float left,right,up,down;

    private final String vertexShaderCode =
//Test
            "attribute vec2 a_TexCoordinate;" +
                    "varying vec2 v_TexCoordinate;" +
//End Test
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position =  uMVPMatrix*vPosition;" +
                    //Test
                    "v_TexCoordinate = a_TexCoordinate;" +
                    //End Test
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
//Test
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
//End Test
                    "void main() {" +
//"gl_FragColor = vColor;" +
                    "gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));" +
                    "}";

    private final String gray_scale =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
//Test
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
//End Test
                    "void main() {" +
//"gl_FragColor = vColor;" +
                    "gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));" +
                    "}";

    public static final String fs_GrayAverage =
            "precision mediump float;" +
                    "varying vec2 v_texCoordinate;" +
                    "uniform sampler2D u_texture;" +
                    "void main() {" +
                    "  vec4 tex = texture2D( u_texture, v_texCoordinate );" +
                    "  float c;" +
                    "  c = (tex.r + tex.g + tex.b) / 3.0;" +
                    "  vec4 pixel = vec4( c, c, c, tex.a);" +
                    "  gl_FragColor = vec4(pixel);" +
                    "}";


    private final int shaderProgram;
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    public float x;
    public float y;
    public int val1=0;
    public int val2=0;
    public float val3=0;
    public float val4=0;
    public  boolean send=false;
    public  boolean active=false;
    public boolean blocked=false;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;

    static float spriteCoords[] = {
            -0.1f,  0.1f,   // top left
            -0.1f, -0.1f,   // bottom left
            0.1f, -0.1f,   // bottom right
            0.1f,  0.1f}; //top right

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; //Order to draw vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; //Bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    public float color[] = { 1f, 1f, 1f, 1f };

    public toggles(final Context activityContext)
    {
        mActivityContext = activityContext;

        //Initialize Vertex Byte Buffer for Shape Coordinates / # of coordinate values * 4 bytes per float
        ByteBuffer bb = ByteBuffer.allocateDirect(spriteCoords.length * 4);
        //Use the Device's Native Byte Order
        bb.order(ByteOrder.nativeOrder());
        //Create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        //Add the coordinates to the FloatBuffer
        vertexBuffer.put(spriteCoords);
        //Set the Buffer to Read the first coordinate
        vertexBuffer.position(0);

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData =
                {
                        //Front face
            /*0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f*/

                        1f,  0f,
                        1f, 1f,
                        0f, 1f,
                        0f, 0f
                };

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        //Initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(spriteCoords.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        int gray = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, fs_GrayAverage);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);


        //Texture Code
        GLES20.glBindAttribLocation(shaderProgram, 0, "a_TexCoordinate");

        GLES20.glLinkProgram(shaderProgram);

        //Load the texture
        mTextureDataHandle = loadTexture(mActivityContext, R.drawable.vortoggleoff);
        selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.vortoggleon);

        x=0;
        y=0;
    }



    public toggles(final Context activityContext, float s[], int j, float left1, float right1, float up1, float down1, boolean disabled)
    {
        if (disabled==false){
            color[0]=1f;color[1]=1f;color[2]=1f;color[3]=1f;
            blocked=false;
        }else{
            color[0]=1f;color[1]=1f;color[2]=1f;color[3]=.25f;
            blocked=true;
        }

        spriteCoords=s;
        mActivityContext = activityContext;
        left=left1;
        right=right1;
        up=up1;
        down=down1;

        //Initialize Vertex Byte Buffer for Shape Coordinates / # of coordinate values * 4 bytes per float
        ByteBuffer bb = ByteBuffer.allocateDirect(spriteCoords.length * 4);
        //Use the Device's Native Byte Order
        bb.order(ByteOrder.nativeOrder());
        //Create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        //Add the coordinates to the FloatBuffer
        vertexBuffer.put(spriteCoords);
        //Set the Buffer to Read the first coordinate
        vertexBuffer.position(0);

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData =
                {
                        //Front face
            /*0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f*/

                        1f,  0f,
                        1f, 1f,
                        0f, 1f,
                        0f, 0f
                };

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        //Initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(spriteCoords.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        int gray = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, fs_GrayAverage);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);

        //Texture Code
        GLES20.glBindAttribLocation(shaderProgram, 0, "a_TexCoordinate");

        GLES20.glLinkProgram(shaderProgram);

        //Load the texture
        if (j==0){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.vortoggleoff);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.vortoggleon);
        }
        else if (j==1){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.freedrawtoggle);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.freedrawtoggleon);
        }
        else if (j==2){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.waypointoff);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.waypointon);
        }
        else if (j==3){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.gaussoff);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.gausson);
        }
        else if (j==4){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.exit);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.exit);
        }
        else if (j==5){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.ardroneprefoff);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.ardroneprefon);
        }
        else if (j==6){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.ardroneadd);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.ardroneadd);
        }
        else if (j==7){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.tempoff);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.tempon);
        }
        else if (j==8){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.voronoicentroid1off);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.voronoicentroid1on);
        }
        else if (j==9){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.singlemode);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.multiplemode);
        }
        else if (j==10){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.centroidtrackingoption1);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.centroidtrackingoption2);
        }
        else if (j==11){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.timevaryingdensityfunction);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.lloyds);
        }
        else if (j==12){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.reset);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.reset);
        }
        else if (j==13){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.directionals);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.directionals);
        }
        else if (j==14){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.addturtle);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.addturtle);
        }
        else if (j==15){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.gaussianpathtoggle);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.gaussianpathtoggle);
        }
        else if (j==16){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.bar);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.bar);
        }
        else if (j==17){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.dial);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.dial);
        }
        else if (j==18){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.dragrobottoggleoff);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.dragrobottoggleon);
        }
        else if (j==19){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.freeboundarytoggleoff);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.freeboundarytoggleon);
        }
        else if (j==20){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.addpicasso);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.adddali);
            selectedTextureDataHandle2= loadTexture(mActivityContext,R.drawable.addgoya);
            selectedTextureDataHandle3= loadTexture(mActivityContext,R.drawable.tempoff);
        }
        else if (j==21){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.gaussoutline);
        }
        else if (j==22){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.trackgaussiantoggle);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.trackgaussiantoggleon);
        }
        else if (j==23){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.linetoggle);
        }
        else if (j==24){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.formation);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.formation_on);

        }
        else if (j==25){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.toggle_transform);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.toggle_transform);

        }
        else if (j==26){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.customformation);
            selectedTextureDataHandle = loadTexture(mActivityContext,R.drawable.customformation);
        }
        else if (j==27){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.add);
        }else if (j==28){
            mTextureDataHandle = loadTexture(mActivityContext, R.drawable.subtract);
        }


        x=0;
        y=0;

    }

    public float getUp(){
        return up;
    }

    public float getDown(){
        return down;
    }

    public float getLeft(){
        return left;
    }

    public float getRight(){
        return right;
    }

    public void Draw(float[] mvpMatrix, int s)
    {
        GLES20.glUseProgram(shaderProgram);

        //Get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");

        //Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        //Get Handle to Fragment Shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");

        //Set the Color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        //Set Texture Handles and bind Texture
        mTextureUniformHandle = GLES20.glGetAttribLocation(shaderProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");

        //Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Bind the texture to this unit.

        if (s==1){
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, selectedTextureDataHandle);}
        else if (s==2){
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, selectedTextureDataHandle2);
        }
        else if (s==-1){
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, selectedTextureDataHandle3);
        }
        else{
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        }
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, mCubeTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        //Get Handle to Shape's Transformation Matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");

        //Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        //Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        //Disable Vertex Array
        GLES20.glDisableVertexAttribArray(mPositionHandle);


    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

           // GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}
