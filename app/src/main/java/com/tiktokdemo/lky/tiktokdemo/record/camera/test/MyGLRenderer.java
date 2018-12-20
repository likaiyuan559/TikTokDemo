package com.tiktokdemo.lky.tiktokdemo.record.camera.test;

/**
 * Created by lky on 2017/4/17.
 */

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

/**
 * @date 2014年10月20日 下午2:49:17
 * @author Zheng Haibo
 * @Description: TODO
 */
public class MyGLRenderer implements Renderer {
    Context context; // Application's context
    Random r = new Random();
    //private Square square;
    private GLBitmap glBitmap;
    private int width = 0;
    private int height = 0;
    private long frameSeq = 0;
    private int viewportOffset = 0;
    private int maxOffset = 400;

    public MyGLRenderer(Context context) {
        this.context = context;
        //square = new Square();
        glBitmap = new GLBitmap();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) { // Prevent A Divide By Zero By
            height = 1; // Making Height Equal One
        }
        this.width = width;
        this.height = height;
        gl.glViewport(0, 0, width, height); // Reset The
        // Current
        // Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
        gl.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
                100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
        gl.glLoadIdentity();
    }

    /**
     * 每隔16ms调用一次
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // Reset the Modelview Matrix
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f); // move 5 units INTO the screen is
        // the same as moving the camera 5
        // units away
        // square.draw(gl);
        glBitmap.draw(gl);
        changeGLViewport(gl);
    }

    /**
     * 通过改变gl的视角获取
     *
     * @param gl
     */
    private void changeGLViewport(GL10 gl) {
        System.out.println("time=" + System.currentTimeMillis());
        frameSeq++;
        viewportOffset++;
        // The
        // Current
        if (frameSeq % 100 == 0) {// 每隔100帧，重置
            gl.glViewport(0, 0, width, height);
            viewportOffset = 0;
        } else {
            int k = 4;
            gl.glViewport(-maxOffset + viewportOffset * k, -maxOffset
                    + viewportOffset * k, this.width - viewportOffset * 2 * k
                    + maxOffset * 2, this.height - viewportOffset * 2 * k
                    + maxOffset * 2);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl,
                                 javax.microedition.khronos.egl.EGLConfig arg1) {
        glBitmap.loadGLTexture(gl, this.context);

        gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
        gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
        gl.glClearDepthf(1.0f); // Depth Buffer Setup
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
        gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }
}
