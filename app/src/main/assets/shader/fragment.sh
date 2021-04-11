#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES s_texture;
void main() {
    vec2 uv = textureCoordinate;
    vec4 nColor=texture2D(s_texture,uv);
    gl_FragColor=nColor;
}