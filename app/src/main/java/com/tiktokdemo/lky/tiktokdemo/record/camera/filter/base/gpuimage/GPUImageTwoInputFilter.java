/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import com.tiktokdemo.lky.tiktokdemo.record.camera.utils.OpenGlUtils;
import com.tiktokdemo.lky.tiktokdemo.record.camera.utils.Rotation;
import com.tiktokdemo.lky.tiktokdemo.record.camera.utils.TextureRotationUtil;


public class GPUImageTwoInputFilter extends GPUImageFilter {
    private static final String VERTEX_SHADER =
            "attribute vec4 position;\n" +
                    "attribute vec4 inputTextureCoordinate;\n" +
                    "attribute vec4 inputTextureCoordinate2;\n" +
                    " \n" +
                    "varying vec2 textureCoordinate;\n" +
                    "varying vec2 textureCoordinate2;\n" +
                    " \n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_Position = position;\n" +
                    "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                    "    textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
                    "}";

    public int mFilterSecondTextureCoordinateAttribute;
    public int mFilterInputTextureUniform2;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;
    protected Bitmap mBitmap;
    private int mTexturePosition = 2;

    public GPUImageTwoInputFilter(String fragmentShader) {
        this(VERTEX_SHADER, fragmentShader);
    }

    public GPUImageTwoInputFilter(String fragmentShader, int texturePosition){
        this(getVertexShader(texturePosition), fragmentShader);
        mTexturePosition = texturePosition;
    }

    public GPUImageTwoInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    public static String getVertexShader(int vertexPosition){
        return "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                "attribute vec4 inputTextureCoordinate" + vertexPosition + ";\n" +
                " \n" +
                "varying vec2 textureCoordinate;\n" +
                "varying vec2 textureCoordinate" + vertexPosition + ";\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = position;\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "    textureCoordinate" + vertexPosition + " = inputTextureCoordinate" + vertexPosition + ".xy;\n" +
                "}";
    }

    @Override
    public void onInit() {
        super.onInit();
        mFilterSecondTextureCoordinateAttribute = GLES20
                .glGetAttribLocation(getProgram(), "inputTextureCoordinate" + mTexturePosition);
        mFilterInputTextureUniform2 = GLES20
                .glGetUniformLocation(getProgram(), "inputImageTexture" + mTexturePosition); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader

        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);

        if (mBitmap != null&&!mBitmap.isRecycled()) {
            setBitmap(mBitmap);
        }
    }

    public void setBitmap(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {
            public void run() {
                if (mFilterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
                }
            }
        });
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void recycleBitmap() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{
                mFilterSourceTexture2
        }, 0);
        mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture2);
        GLES20.glUniform1i(mFilterInputTextureUniform2, 3);

//        mTexture2CoordinatesBuffer.position(0);
//        mTexture2CoordinatesBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_ROTATED_90.length * 4)
//                .order(ByteOrder.nativeOrder());
//        FloatBuffer fBuffer = mTexture2CoordinatesBuffer.asFloatBuffer();
//        fBuffer.put(TEXTURE_ROTATED_90);
//        fBuffer.flip();
        GLES20.glVertexAttribPointer(mFilterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture2CoordinatesBuffer);

    }

    public void setFilterSecondTextureCoordinateAttribute(int width,int height,int left,int top,int right,int bottom){
        float widthScale = width/(float)(right-left);
        float heightScale = height/(float)(bottom-top);
        Log.e("MagicFace","MagicFace:widthScale:" + widthScale + ",heightScale:" + heightScale);
        float v1x = -left/(float)width*widthScale;
        float v1y = heightScale-top/(float)height*heightScale;
        float v2x = widthScale-left/(float)width*widthScale;
        float v2y = heightScale-top/(float)height*heightScale;
        float v3x = -left/(float)width*widthScale;
        float v3y = -top/(float)height*heightScale;
        float v4x = widthScale-left/(float)width*widthScale;
        float v4y = -top/(float)height*heightScale;
        float[] testArray = {
                v1x, v1y,
                v2x, v2y,
                v3x, v3y,
                v4x, v4y
        };

        mTexture2CoordinatesBuffer = ByteBuffer.allocateDirect(testArray.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = mTexture2CoordinatesBuffer.asFloatBuffer();
        fBuffer.put(testArray);
        fBuffer.flip();
    }

    /**
     *                    y
     *
     *                    |
     *                    |
     *             v3     |         v4
     *                    |
     *                    |
     *                    |
     * ------------------------------------------->         x
     *                    |
     *                    |
     *                    |
     *             v1     |         v2
     *                    |
     *                    |
     *                    |
     */
    public void setFilterSecondTextureCoordinateAttribute(int width, int height, PointF v1p, PointF v2p, PointF v3p, PointF v4p){
        float widthScale = width/(float)(Math
                .sqrt((v2p.x-v1p.x)*(v2p.x-v1p.x)+(v2p.y-v1p.y)*(v2p.y-v1p.y)));
        float heightScale = height/(float)(Math
                .sqrt((v1p.x-v3p.x)*(v1p.x-v3p.x)+(v1p.y-v3p.y)*(v1p.y-v3p.y)));
//        Log.e("MagicFace","MagicFace:widthScale:" + widthScale + ",heightScale:" + heightScale);
        float v1x = -v1p.x/(float) width*widthScale;
        float v1y = -heightScale+v1p.y/(float)height*heightScale+1f;
        float v2x = widthScale-v2p.x/(float)width*widthScale+1f;
        float v2y = -heightScale+v2p.y/(float)height*heightScale+1f;
        float v3x = -v3p.x/(float)width*widthScale;
        float v3y = v3p.y/(float)height*heightScale;
        float v4x = widthScale-v4p.x/(float)width*widthScale+1f;
        float v4y = v4p.y/(float)height*heightScale;
        float[] testArray = {
                v1x, v1y,
                v2x, v2y,
                v3x, v3y,
                v4x, v4y
        };

        mTexture2CoordinatesBuffer = ByteBuffer.allocateDirect(testArray.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = mTexture2CoordinatesBuffer.asFloatBuffer();
        fBuffer.put(testArray);
        fBuffer.flip();
    }

    public void setFilterSecondTextureCoordinateAttribute(float[] array){
        mTexture2CoordinatesBuffer = ByteBuffer.allocateDirect(array.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = mTexture2CoordinatesBuffer.asFloatBuffer();
        fBuffer.put(array);
        fBuffer.flip();
    }


    public void setFilterSecondTextureCoordinateAttribute(int width, int height, int left, int top, int right, int bottom, PointF centerPoint, float cosignx, float singnx){
        float widthScale = width/(float)(right-left);
        float heightScale = height/(float)(bottom-top);
        Log.e("MagicFace","MagicFace:widthScale:" + widthScale + ",heightScale:" + heightScale);
        float v1x = (((left-centerPoint.x)*cosignx-(top-centerPoint.y)*singnx + centerPoint.x)/width*2-1)*widthScale;
        float v1y = (((left-centerPoint.x)*singnx+(top-centerPoint.y)*cosignx+centerPoint.y)/height*2-1)*heightScale;
        float v2x = (((right-centerPoint.x)*cosignx-(top-centerPoint.y)*singnx+centerPoint.x)/width*2-1)*widthScale;
        float v2y = (((right-centerPoint.x)*singnx+(top-centerPoint.y)*cosignx+centerPoint.y)/height*2-1)*heightScale;
        float v3x = (((left-centerPoint.x)*cosignx-(bottom-centerPoint.y)*singnx+centerPoint.x)/width*2-1)*widthScale;
        float v3y = (((left-centerPoint.x)*singnx+(bottom-centerPoint.y)*cosignx+centerPoint.y)/height*2-1)*heightScale;
        float v4x = (((right-centerPoint.x)*cosignx-(bottom-centerPoint.y)*singnx+centerPoint.x)/width*2-1)*widthScale;
        float v4y = (((right-centerPoint.x)*singnx+(bottom-centerPoint.y)*cosignx+centerPoint.y)/height*2-1)*heightScale;
        float[] testArray = {
                v1x, v1y,
                v2x, v2y,
                v3x, v3y,
                v4x, v4y
        };

        mTexture2CoordinatesBuffer = ByteBuffer.allocateDirect(testArray.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = mTexture2CoordinatesBuffer.asFloatBuffer();
        fBuffer.put(testArray);
        fBuffer.flip();
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture2CoordinatesBuffer = bBuffer;

    }
}
