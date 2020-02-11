#include "Arduino.h"
#include "Utils.h"

#define SIGN_EXPR(v) (v>0?1:(v<0?-1:0))
bool isExpired(long start_time, long duration) {
  return (micros() - start_time) >= duration;
}
long now() {
  return micros();
}
long seconds(float s) {
  return (long)(s * 1000000);
}
float toSeconds(long l) {
  return l / 1000000.0f;
}
int sign(double v) {
  return SIGN_EXPR(v);
}
int sign(float v) {
  return SIGN_EXPR(v);
}
int sign(int v) {
  return SIGN_EXPR(v);
}
int sign(long v) {
  return SIGN_EXPR(v);
}

int between(int mi, int ma, int v) {
  return max(min(ma, v), mi);
}

float crop180Angle(float angle) {
  return angle < -180.0f ? crop180Angle(angle + 360.0f) : (angle > 180.0f ? crop180Angle(angle - 360.0f) : angle);
}

float absoluteMin(float v1, float v2) {
  return abs(v1) <= abs(v2) ? v1 : v2;
}
