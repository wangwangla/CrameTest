#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES s_texture;
void modifyColor(vec4 color){
    color.r=max(min(color.r,1.0),0.0);
    color.g=max(min(color.g,1.0),0.0);
    color.b=max(min(color.b,1.0),0.0);
    color.a=max(min(color.a,1.0),0.0);
}
void main() {
    vec2 uv = textureCoordinate;
    if(uv.x <= 0.33){
         uv.x =uv.x * 3.0;
    }else if(uv.x <=0.66){
         uv.x = (uv.x - 0.33)*3.0;
    }else{
         uv.x = (uv.x - 0.66)*3.0;
    }
    if(uv.y <= 0.33){
          uv.y =uv.y * 3.0;
    }else if(uv.y <= 0.66){
          uv.y = (uv.y - 0.33)*3.0;
    }else{
          uv.y = (uv.y - 0.66)*3.0;
    }
    if(textureCoordinate.x<=0.33){
      if(textureCoordinate.y<=0.33){
        vec3 u_ChangeColor = vec3(0.1, 0.1, 0.0);
        vec4 nColor=texture2D(s_texture,uv);
        vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
        modifyColor(deltaColor);
        gl_FragColor=deltaColor;
      }else if(textureCoordinate.y<=0.66){
        vec4 nColor=texture2D(s_texture,uv);
        float c = nColor.r * 0.3 + nColor.g * 0.59 + nColor.b * 0.11;
        gl_FragColor=vec4(c,c,c,nColor.a);
      }else{
        vec3 u_ChangeColor = vec3(0.0, 0.0, 0.1);
        vec4 nColor=texture2D(s_texture,uv);
        vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
        modifyColor(deltaColor);
        gl_FragColor=deltaColor;
      }
    }else if(textureCoordinate.x<=0.66){

      if(textureCoordinate.y<=0.33){
        vec3 u_ChangeColor = vec3(0.1, 0.1, 0.0);
        vec4 nColor=texture2D(s_texture,uv);
        vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
        modifyColor(deltaColor);
        gl_FragColor=deltaColor;
      }else if(textureCoordinate.y<=0.66){
        vec4 nColor=texture2D(s_texture,uv);
        float c = nColor.r * 0.3 + nColor.g * 0.59 + nColor.b * 0.11;
        gl_FragColor=vec4(c,c,c,nColor.a);
      }else{
        vec3 u_ChangeColor = vec3(0.0, 0.0, 0.1);
        vec4 nColor=texture2D(s_texture,uv);
        vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
        modifyColor(deltaColor);
        gl_FragColor=deltaColor;
      }


    }else{
      if(textureCoordinate.y<=0.33){
        vec3 u_ChangeColor = vec3(0.1, 0.1, 0.0);
        vec4 nColor=texture2D(s_texture,uv);
        vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
        modifyColor(deltaColor);
        gl_FragColor=deltaColor;
      }else if(textureCoordinate.y<=0.66){
        vec4 nColor=texture2D(s_texture,uv);
        float c = nColor.r * 0.3 + nColor.g * 0.59 + nColor.b * 0.11;
        gl_FragColor=vec4(c,c,c,nColor.a);
      }else{
        vec3 u_ChangeColor = vec3(0.0, 0.0, 0.1);
        vec4 nColor=texture2D(s_texture,uv);
        vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
        modifyColor(deltaColor);
        gl_FragColor=deltaColor;
      }
    }
}