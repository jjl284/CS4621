uniform sampler2D unTexShadow;
uniform mat4 unVPShadow;

varying vec4 fWorldPos;

void main() {
  vec4 shadowPos = unVPShadow * fWorldPos;
  shadowPos /= shadowPos.w;
  vec2 uvShadow = (shadowPos.xy + 1) * 0.5;
  float shadowMapZ = texture2D(unTexShadow, uvShadow).x;
  
  vec4 col = vec4(0, 0, 0, 1);
  if(shadowMapZ < shadowPos.z) col.x = 1.0;
  else col.y = 1.0;
  gl_FragColor = col;
}