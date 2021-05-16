#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES xxxx;
varying vec2 vTextureCoord;
void main()
{
    gl_FragColor = texture2D(xxxx, vTextureCoord);
}
