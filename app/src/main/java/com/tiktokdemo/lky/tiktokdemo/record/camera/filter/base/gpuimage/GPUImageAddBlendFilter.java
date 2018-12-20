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

public class GPUImageAddBlendFilter extends GPUImageTwoInputFilter {
    public static final String ADD_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "\n" +
            "   mediump float r;\n" +
            "   if (overlay.r * base.a + base.r * overlay.a >= overlay.a * base.a) {\n" +
            "     r = overlay.a * base.a + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     r = overlay.r + base.r;\n" +
            "   }\n" +
            "\n" +
            "   mediump float g;\n" +
            "   if (overlay.g * base.a + base.g * overlay.a >= overlay.a * base.a) {\n" +
            "     g = overlay.a * base.a + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     g = overlay.g + base.g;\n" +
            "   }\n" +
            "\n" +
            "   mediump float b;\n" +
            "   if (overlay.b * base.a + base.b * overlay.a >= overlay.a * base.a) {\n" +
            "     b = overlay.a * base.a + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     b = overlay.b + base.b;\n" +
            "   }\n" +
            "\n" +
            "   mediump float a = overlay.a + base.a - overlay.a * base.a;\n" +
            "   \n" +
            "   gl_FragColor = vec4(r, g, b, a);\n" +
            " }";

    public GPUImageAddBlendFilter() {
        super(ADD_BLEND_FRAGMENT_SHADER);
    }

//    private int[] mFrameBuffers = null;
//    private int[] mFrameBufferTextures = null;
//    private int mFrameWidth = -1;
//    private int mFrameHeight = -1;
//
//    @Override
//    public void onInit() {
//        super.onInit();
//    }
//
//    @Override
//    public int onDrawFrame(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
//
//        GLES20.glUseProgram(mGLProgId);
//        runPendingOnDrawTasks();
//        if (!mIsInitialized) {
//            return OpenGlUtils.NOT_INIT;
//        }
//
//        cubeBuffer.position(0);
//        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
//        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
//        textureBuffer.position(0);
//        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
//                textureBuffer);
//        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
//        if (textureId != OpenGlUtils.NO_TEXTURE) {
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//            GLES20.glUniform1i(mGLUniformTexture, 0);
//        }
//        onDrawArraysPre();
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
////        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
////        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
//        onDrawArraysAfter();
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//
//        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
//        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
//
//        return OpenGlUtils.ON_DRAWN;
//    }
//
//    public int onDrawToTexture(final int textureId) {
//        if(mFrameBuffers == null)
//            return OpenGlUtils.NO_TEXTURE;
//        GLES20.glUseProgram(mGLProgId);
//        runPendingOnDrawTasks();
//        if (!mIsInitialized) {
//            return OpenGlUtils.NOT_INIT;
//        }
//        FloatBuffer cubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE_BAAB.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        cubeBuffer.put(TextureRotationUtil.CUBE_BAAB).position(0);
//
//        FloatBuffer textureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        textureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
//        cubeBuffer.position(0);
//        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
//        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
//        textureBuffer.position(0);
//        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
//                textureBuffer);
//        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
//        if (textureId != OpenGlUtils.NO_TEXTURE) {
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
//            GLES20.glUniform1i(mGLUniformTexture, 0);
//        }
//        onDrawArraysPre();
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
//        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
//        onDrawArraysAfter();
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//
//        return mFrameBufferTextures[0];
//    }
//
//    public void initCameraFrameBuffer(int width, int height) {
//        if(mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height))
//            destroyFramebuffers();
//        if (mFrameBuffers == null) {
//            mFrameWidth = width;
//            mFrameHeight = height;
//            mFrameBuffers = new int[1];
//            mFrameBufferTextures = new int[1];
//
//            GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
//            GLES20.glGenTextures(1, mFrameBufferTextures, 0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
//            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
//                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
//            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
//                    GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//        }
//    }
//
//    public void destroyFramebuffers() {
//        if (mFrameBufferTextures != null) {
//            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
//            mFrameBufferTextures = null;
//        }
//        if (mFrameBuffers != null) {
//            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
//            mFrameBuffers = null;
//        }
//        mFrameWidth = -1;
//        mFrameHeight = -1;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        destroyFramebuffers();
//    }
}
