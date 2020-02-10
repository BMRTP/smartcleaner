#include "DigitalButton.h"
#include "Arduino.h"

DigitalButton::DigitalButton(int pin){
  this->pin = pin;
  this->state = ButtonReleased;
  this->n = 0;
  pinMode(pin, INPUT);
}

void DigitalButton::addHandler(EventHandler handler){
  if(this->n >= MAX_BUTTON_EVENT_HANDLER){
    return;
  }
  this->handler[this->n] = handler;
  this->n++;
}

void DigitalButton::check(){
  ButtonState currentState = this->getState();
  if(this->state != currentState) { 
    this->state = currentState;
    for(int i = 0; i < n; i++) {
      this->handler[i](this->state);
    }
  } 
}

ButtonState DigitalButton::getState() {
  int v = 0;
  for(int i = 0; i < READS_COUNT; i++) {
    v += digitalRead(pin);
  }
  if(v > READS_COUNT / 2) {
    v = HIGH;
  } else {
    v = LOW;
  }
  return v == PRESS_VALUE ? ButtonPressed : ButtonReleased;
}
