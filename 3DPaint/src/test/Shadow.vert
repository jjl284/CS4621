uniform mat4 unWorld;
uniform mat4 unVP;

attribute vec4 vPos;

varying vec4 fPos;

void main() {
  vec4 worldPos = unWorld * vPos;
  fPos = unVP * worldPos;
  gl_Position = fPos;
}
