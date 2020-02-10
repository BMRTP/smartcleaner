#include "HBridge.h"
#include "Arduino.h"

void power(float p, int pinForward, int pinReverse, int maxPWM) {
  p = max(-1, min(1, p));
  int pwm = (int)(abs(p) * maxPWM + 0.5f);
  if(p >= 0) {
    analogWrite(pinForward, min(maxPWM, pwm));
    analogWrite(pinReverse, 0);
  } else {
    analogWrite(pinForward, 0);
    analogWrite(pinReverse, min(maxPWM, pwm));
  }
}

HBridge::HBridge(int leftForward, int leftReverse, int rightForward, int rightReverse){
  this->leftForward = leftForward;
  this->leftReverse = leftReverse;
  this->rightForward = rightForward;
  this->rightReverse = rightReverse;
  this->maxPWM = 255;
  
  pinMode(leftForward, OUTPUT);
  pinMode(leftReverse, OUTPUT);
  pinMode(rightForward, OUTPUT);
  pinMode(rightReverse, OUTPUT);
  this->stop();
}

void HBridge::maxPWMOutput(int v) {
  this->maxPWM = v;
}
void HBridge::leftPower(float p) {
  power(p, leftForward, leftReverse, this->maxPWM);
}
void HBridge::rightPower(float p) {
  power(p, rightForward, rightReverse, this->maxPWM);
}
void HBridge::stop() {
  analogWrite(rightForward, 0);
  analogWrite(rightReverse, 0);
  analogWrite(leftForward, 0);
  analogWrite(leftReverse, 0);
}
