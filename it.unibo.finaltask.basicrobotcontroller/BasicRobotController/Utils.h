#ifndef __UTILS__
#define __UTILS__

bool isExpired(long start_time, long duration);
long now();
long seconds(float s);
float toSeconds(long l);

int sign(double v);
int sign(float v);
int sign(int v);
int sign(long v);

int between(int min, int max, int v);

float crop180Angle(float angle);

float absoluteMin(float v1, float v2);

#endif
