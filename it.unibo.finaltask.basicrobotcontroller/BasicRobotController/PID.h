#ifndef __PID__
#define __PID__

class PID { 
public:
  PID(float p, float i, float d, float maxOutput);
  float update(float error);
  void setParameters(float p, float i, float d);
  void reset();
  
private:
  float p_term(float dt);
  float i_term(float dt);
  float d_term(float dt);

  float p,i,d;
  float error, lastError, integral;
  float maxOutput;
  long errorTime, lastErrorTime;
  bool first;
};

#endif
