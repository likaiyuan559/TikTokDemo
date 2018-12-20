#extension GL_OES_EGL_image_external : require

precision mediump float;

varying mediump vec2 textureCoordinate;

uniform samplerExternalOES inputImageTexture;
uniform vec2 singleStepOffset;
uniform mediump float params;
const vec4 params1 = vec4(0.7, 0.67, 0.16, 0.16);//r,g,b,a

const highp vec3 W = vec3(0.299,0.587,0.114);
const mat3 saturateMatrix = mat3(
                                1.1102,-0.0598,-0.061,
                                -0.0774,1.0826,-0.1186,
                                -0.0228,-0.0228,1.1772);

vec2 blurCoordinates[8];

float hardLight(float color)
{
	if(color <= 0.5)
		color = color * color * 2.0;
	else
		color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);
	return color;
}

void main(){

    vec3 centralColor = texture2D(inputImageTexture, textureCoordinate).rgb;
    if(params != 0.0){
        blurCoordinates[0] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, -2.0);
        blurCoordinates[1] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, 2.0);
        blurCoordinates[2] = textureCoordinate.xy + singleStepOffset * vec2(2.0, -2.0);
        blurCoordinates[3] = textureCoordinate.xy + singleStepOffset * vec2(2.0, 2.0);
        blurCoordinates[4] = textureCoordinate.xy + singleStepOffset * vec2(-1.0, -1.0);
        blurCoordinates[5] = textureCoordinate.xy + singleStepOffset * vec2(-1.0, 1.0);
        blurCoordinates[6] = textureCoordinate.xy + singleStepOffset * vec2(1.0, -1.0);
        blurCoordinates[7] = textureCoordinate.xy + singleStepOffset * vec2(1.0, 1.0);

        float sampleColor = centralColor.g * 22.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[0]).g * 4.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[1]).g * 4.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[2]).g * 4.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[3]).g * 4.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[4]).g *6.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[5]).g *6.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[6]).g *6.0;
        sampleColor += texture2D(inputImageTexture, blurCoordinates[7]).g *6.0;

        sampleColor = sampleColor / 62.0;

        float highPass = centralColor.g - sampleColor + 0.5;

        for(int i = 0; i < 5;i++)
        {
            highPass = hardLight(highPass);
        }
        float luminance = dot(centralColor, W);

        float alpha = pow(luminance, params1.r);

        vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;

        smoothColor.r = clamp(pow(smoothColor.r, params1.g),0.0,1.0);
        smoothColor.g = clamp(pow(smoothColor.g, params1.g),0.0,1.0);
        smoothColor.b = clamp(pow(smoothColor.b, params1.g),0.0,1.0);

        vec3 screen = vec3(1.0) - (vec3(1.0)-smoothColor) * (vec3(1.0)-centralColor);
        vec3 lighten = max(smoothColor, centralColor);
        vec3 softLight = 2.0 * centralColor*smoothColor + centralColor*centralColor
                             - 2.0 * centralColor*centralColor * smoothColor;
        vec3 greyScaleColor = vec3(luminance);
        gl_FragColor = vec4(((centralColor - vec3(0.5)) * 0.94 + vec3(0.5)), 1.0);
        gl_FragColor = vec4(mix(greyScaleColor, gl_FragColor.rgb, 1.11), gl_FragColor.w);
        gl_FragColor = vec4(mix(gl_FragColor.rgb, screen, alpha), gl_FragColor.w);

        gl_FragColor = vec4((gl_FragColor.rgb + vec3(0.06)), gl_FragColor.w);

        gl_FragColor.rgb = mix(gl_FragColor.rgb, lighten, alpha);
        gl_FragColor.rgb = mix(gl_FragColor.rgb, softLight, params1.b);

        vec3 satColor = gl_FragColor.rgb * saturateMatrix;
        gl_FragColor.rgb = mix(gl_FragColor.rgb, satColor, params1.a);
    }else{
        gl_FragColor = vec4(centralColor.rgb,1.0);
    }
}