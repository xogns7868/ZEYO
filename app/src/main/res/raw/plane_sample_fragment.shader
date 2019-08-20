uniform sampler2D u_Texture;
varying vec3 v_TexCoordAlpha;

void main() {
  gl_FragColor = texture2D(u_Texture, v_TexCoordAlpha.xy);
}