#ifndef __LED__
#define __LED__


class Led { 
public:
  Led(int pin);
  void switchOn();
  void switchOff();
  void toggle(); 
  int getState();
  void pulse(float frequency);
  void update();
private:
  int state;
  int pin;
  long lastPulseTime;  
  long pulsePeriodMicros;
};

#endif
