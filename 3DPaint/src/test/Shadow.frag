varying vec4 fPos;

void main() {
  float z = fPos.z / fPos.w;
  gl_FragColor = vec4(z, z, z, 1);
}