package com.tiktokdemo.lky.tiktokdemo.record.camera.filter.base.gpuimage;

/**
 * Created by lky on 2017/5/19.
 */

public class GPUImageMiniLookupFilter extends GPUImageTwoInputFilter {

    public static final String LOOKUP_FRAGMENT_SHADER =
            " varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2; // TODO: This is not used\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; // lookup texture\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     mediump float blueColor = textureColor.b * 15.0;\n" +
            "     \n" +
            "     mediump vec2 quad1;\n" +
            "     quad1.y = floor(floor(blueColor) / 2.0);\n" +
            "     quad1.x = floor(blueColor) - (quad1.y * 2.0);\n" +
            "     \n" +
            "     mediump vec2 quad2;\n" +
            "     quad2.y = floor(ceil(blueColor) / 2.0);\n" +
            "     quad2.x = ceil(blueColor) - (quad2.y * 2.0);\n" +
            "     \n" +
            "     highp vec2 texPos1;\n" +
            "     texPos1.x = (quad1.x * 0.03125) + 0.125/64.0 + ((0.03125 - 0.25/64.0) * textureColor.r);\n" +
            "     texPos1.y = (quad1.y * 0.03125) + 0.125/64.0 + ((0.03125 - 0.25/64.0) * textureColor.g);\n" +
            "     \n" +
            "     highp vec2 texPos2;\n" +
            "     texPos2.x = (quad2.x * 0.03125) + 0.125/64.0 + ((0.03125 - 0.25/64.0) * textureColor.r);\n" +
            "     texPos2.y = (quad2.y * 0.03125) + 0.125/64.0 + ((0.03125 - 0.25/64.0) * textureColor.g);\n" +
            "     \n" +
            "     lowp vec4 newColor1 = texture2D(inputImageTexture2, texPos1);\n" +
            "     lowp vec4 newColor2 = texture2D(inputImageTexture2, texPos2);\n" +
            "     \n" +
            "     lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
            "     gl_FragColor = vec4(newColor.rgb, textureColor.w);\n" +
            " }";


    public GPUImageMiniLookupFilter() {
        super(LOOKUP_FRAGMENT_SHADER);
    }
}
