#include "PID.h"
#include "Utils.h"
#include "Arduino.h"

float absoluteBound(float v, float max) {
  return abs(v) > max ? max * sign(v) : v;
}
float PID::p_term(float dt) {
  return this->error;
}
float PID::d_term(float dt) {
  return (this->error - this->lastError) / dt;
}
float PID::i_term(float dt) {
  if(sign(this->error) != sign(this->lastError)) {
    this->integral = 0;
  } else {
    this->integral += this->error * dt;
  }
  return integral;
}

PID::PID(float p, float i, float d, float maxOutput){
  this->setParameters(p, i, d);
  this->reset();
  this->maxOutput = maxOutput;
}

float PID::update(float error) {
  this->lastError = this->error;
  this->lastErrorTime = this->errorTime;
  
  this->error = error;
  this->errorTime = micros();

  if(this->first) {
    this->first = false;
    return 0.0;
  } else {
    float dt = (this->errorTime - this->lastErrorTime) / 1000000.0;
    float P = this->p * this->p_term(dt);

    float D = this->d * this->d_term(dt);
    float I = absoluteBound(this->i * this->i_term(dt), this->maxOutput) / (abs(D)+1);
    
    return absoluteBound(-(P + I + D), this->maxOutput);
  }
}

void PID::setParameters(float p, float i, float d) {
  this->p = p;
  this->i = i;
  this->d = d;
}

void PID::reset() {
  this->error = 0;
  this->errorTime = 0;
  this->lastError = 0;
  this->lastErrorTime = 0;
  this->integral = 0;
  this->first = true;
}
