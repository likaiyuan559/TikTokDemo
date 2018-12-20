package com.heyhou.social.video;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Created by starjiang on 17-7-18.
 */

public class HeyhouPlayerRender implements GLSurfaceView.Renderer {

    public static final String VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "varying vec2 textureCoordinate;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";

    private static final String FRAGMENT_SHADER = ""+
            "precision highp float;\n" +
            "uniform sampler2D texY;\n" +
            "uniform sampler2D texU;\n" +
            "uniform sampler2D texV;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "void main() {\n" +
            "    vec4 color = vec4((texture2D(texY, textureCoordinate).r - 16./255.) * 1.164);\n" +
            "    vec4 U = vec4(texture2D(texU, textureCoordinate).r - 128./255.);\n" +
            "    vec4 V = vec4(texture2D(texV, textureCoordinate).r - 128./255.);\n" +
            "    color += V * vec4(1.596, -0.813, 0, 0);\n" +
            "    color += U * vec4(0, -0.392, 2.017, 0);\n" +
            "    color.a = 1.0;\n" +
            "    gl_FragColor = color;\n" +
            "}\n";


    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTextureY;
    protected int mGLUniformTextureU;
    protected int mGLUniformTextureV;
    protected int mGLAttribTextureCoordinate;
    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;
    protected int yTextureId;
    protected int uTextureId;
    protected int vTextureId;
    protected Context context;
    protected GLSurfaceView mTargetSurface;
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;
    private int videoWidth;
    private int videoHeight;
    private int surfaceWidth;
    private int surfaceHeight;
    private Rotation rotation;

    public  HeyhouPlayerRender(Context context, GLSurfaceView targetSurface){

        mGLCubeBuffer = ByteBuffer
                .allocateDirect(TextureRotationUtil.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer
                .allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, false)).position(0);
        this.context = context;
        this.mTargetSurface = targetSurface;
        yTextureId = -1;
        uTextureId = -1;
        vTextureId = -1;
        mGLProgId = -1;
        this.rotation = Rotation.NORMAL;

    }

    public void setRotation(Rotation rotation){
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(rotation, false, false)).position(0);
        this.rotation = rotation;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mGLProgId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        mGLUniformTextureY = GLES20.glGetUniformLocation(mGLProgId, "texY");
        mGLUniformTextureU = GLES20.glGetUniformLocation(mGLProgId, "texU");
        mGLUniformTextureV = GLES20.glGetUniformLocation(mGLProgId, "texV");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId, "inputTextureCoordinate");
        Log.e("render","version "+ GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION));

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        synchronized (this) {

            Log.e("render","size changed w="+width+",height="+height);
            surfaceWidth = width;
            surfaceHeight  = height;

            GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {

            if(y == null){
                return;
            }

            if(rotation == Rotation.NORMAL  || rotation == Rotation.ROTATION_180){

                if(videoWidth > videoHeight){

                    int newHeight = (int)((surfaceWidth/(videoWidth*1.0f))* videoHeight);

                    float c1 = ((surfaceHeight-newHeight)/1.0f)/surfaceHeight;

                    if(c1 <= 0){
                        c1 = 0;
                    }
                    mGLCubeBuffer.position(0);
                    mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

                    mGLCubeBuffer.put(1,TextureRotationUtil.CUBE[1]+c1);
                    mGLCubeBuffer.put(3,TextureRotationUtil.CUBE[3]+c1);

                    mGLCubeBuffer.put(5,TextureRotationUtil.CUBE[5]-c1);
                    mGLCubeBuffer.put(7,TextureRotationUtil.CUBE[7]-c1);
                }else{

                    int newWidth = (int)((surfaceHeight/(videoHeight*1.0f))* videoWidth);

                    float c1 = ((surfaceWidth-newWidth)/1.0f)/surfaceWidth;

                    if(c1 <= 0){
                        c1 = 0;
                    }

                    mGLCubeBuffer.position(0);
                    mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

                    mGLCubeBuffer.put(0,TextureRotationUtil.CUBE[0]+c1);
                    mGLCubeBuffer.put(2,TextureRotationUtil.CUBE[2]-c1);

                    mGLCubeBuffer.put(4,TextureRotationUtil.CUBE[4]+c1);
                    mGLCubeBuffer.put(6,TextureRotationUtil.CUBE[6]-c1);
                }

            }

            if(rotation == Rotation.ROTATION_90 || rotation == Rotation.ROTATION_270){

                if(videoWidth < videoHeight){

                    int newHeight = (int)((surfaceWidth/(videoHeight*1.0f))* videoWidth);

                    float c1 = ((surfaceHeight-newHeight)/1.0f)/surfaceHeight;

                    if(c1 <= 0){
                        c1 = 0;
                    }
                    mGLCubeBuffer.position(0);
                    mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

                    mGLCubeBuffer.put(1,TextureRotationUtil.CUBE[1]+c1);
                    mGLCubeBuffer.put(3,TextureRotationUtil.CUBE[3]+c1);

                    mGLCubeBuffer.put(5,TextureRotationUtil.CUBE[5]-c1);
                    mGLCubeBuffer.put(7,TextureRotationUtil.CUBE[7]-c1);
                }else{

                    int newWidth = (int)((surfaceHeight/(videoWidth*1.0f))* videoHeight);

                    float c1 = ((surfaceWidth-newWidth)/1.0f)/surfaceWidth;

                    if(c1 <= 0){
                        c1 = 0;
                    }


                    mGLCubeBuffer.position(0);
                    mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

                    mGLCubeBuffer.put(0,TextureRotationUtil.CUBE[0]+c1);
                    mGLCubeBuffer.put(2,TextureRotationUtil.CUBE[2]-c1);

                    mGLCubeBuffer.put(4,TextureRotationUtil.CUBE[4]+c1);
                    mGLCubeBuffer.put(6,TextureRotationUtil.CUBE[6]-c1);
                }


            }

            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glClearColor(0,0,0,1.0f);

            buildTextures(y,u,v,videoWidth,videoHeight);

            GLES20.glUseProgram(mGLProgId);

            mGLCubeBuffer.position(0);
            GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
            GLES20.glEnableVertexAttribArray(mGLAttribPosition);
            mGLTextureBuffer.position(0);
            GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
            GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

            if (yTextureId != OpenGlUtils.NO_TEXTURE) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureId);
                GLES20.glUniform1i(mGLUniformTextureY, 0);
            }

            if (uTextureId != OpenGlUtils.NO_TEXTURE) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureId);
                GLES20.glUniform1i(mGLUniformTextureU, 1);
            }

            if (vTextureId != OpenGlUtils.NO_TEXTURE) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureId);
                GLES20.glUniform1i(mGLUniformTextureV, 2);
            }

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(mGLAttribPosition);
            GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }

    public void destroy(){
        if(mGLProgId != -1){
            GLES20.glDeleteProgram(mGLProgId);
        }
    }

    /**
     * this method will be called from native code, it's used for passing yuv data to me.
     */
    public void update(byte[] yuvdata,int width,int height,int rotation) {
        synchronized (this) {

            int frameSize = width * height;

            if(videoWidth != width || videoHeight != height){
                y = ByteBuffer.allocate(frameSize);
                u = ByteBuffer.allocate(frameSize/4);
                v = ByteBuffer.allocate(frameSize/4);
            }

            y.clear();
            u.clear();
            v.clear();

            y.put(yuvdata, 0, frameSize);
            u.put(yuvdata, frameSize, frameSize/4);
            v.put(yuvdata, frameSize+frameSize/4, frameSize/4);

            this.videoWidth = width;
            this.videoHeight = height;

            if(rotation == 0){
                setRotation(Rotation.NORMAL);
            }else if(rotation == 90){
                setRotation(Rotation.ROTATION_90);
            }else if(rotation == 180) {
                setRotation(Rotation.ROTATION_180);
            }else if(rotation == 270){
                setRotation(Rotation.ROTATION_270);
            }

            mTargetSurface.requestRender();
        }
    }

    public void buildTextures(Buffer y, Buffer u, Buffer v, int width, int height) {

        y.position(0);
        u.position(0);
        v.position(0);

        // building texture for Y data
        if (yTextureId < 0) {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            OpenGlUtils.checkGlError("glGenTextures");
            yTextureId = textures[0];
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureId);
        OpenGlUtils.checkGlError("glBindTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width, height, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);
        OpenGlUtils.checkGlError("glTexImage2D");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // building texture for U data
        if (uTextureId < 0 ) {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            OpenGlUtils.checkGlError("glGenTextures");
            uTextureId = textures[0];
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width / 2, height / 2, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // building texture for V data
        if (vTextureId < 0) {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            OpenGlUtils.checkGlError("glGenTextures");
            vTextureId = textures[0];
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width / 2, height / 2, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

}
