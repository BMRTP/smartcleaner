#include "Led.h"
#include "Arduino.h"

Led::Led(int pin){
  this->pin = pin;
  pinMode(pin, OUTPUT);
  this->switchOff();
  this->lastPulseTime = 0;
  this->pulsePeriodMicros = 0;
}

void Led::switchOn(){
  digitalWrite(pin, HIGH);
  this->state = HIGH;
}

void Led::switchOff(){
  digitalWrite(pin, LOW);
  this->state = LOW;
};

void Led::toggle(){
  if(this->state) {
    this->switchOff();
  } else {
    this->switchOn();
  }
};

void Led::pulse(float frequency){
  this->pulsePeriodMicros = (long)((1.0f / frequency) * 1000000);
};

void Led::update(){
  if(this->pulsePeriodMicros > 0.0f) {
    if(micros() - this->lastPulseTime > this->pulsePeriodMicros) {
      this->toggle();
      this->lastPulseTime = micros();
    }
  }
};

int Led::getState() {
  return this->state;
}
