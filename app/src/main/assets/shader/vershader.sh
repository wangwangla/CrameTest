attribute vec4 vPosition;
attribute vec2 inputTextureCoordinate;
varying vec2 textureCoordinate;
uniform mat4 vMatrix;
void main(){
    vec4 cc = vPosition * vMatrix;
    gl_Position = cc;
    textureCoordinate = inputTextureCoordinate;
}
