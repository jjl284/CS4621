uniform mat4 unWorld;
uniform mat4 unVP;

attribute vec4 vPos;

varying vec4 fWorldPos;

void main() {
  fWorldPos = unWorld * vPos;
  gl_Position = unVP * fWorldPos;
}
