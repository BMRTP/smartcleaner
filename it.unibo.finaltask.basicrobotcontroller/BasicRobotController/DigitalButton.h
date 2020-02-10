#ifndef __DIGITAL_BUTTON__
#define __DIGITAL_BUTTON__

#include "Button.h"

#define READS_COUNT 1
#define PRESS_VALUE HIGH
#define MAX_BUTTON_EVENT_HANDLER 2


class DigitalButton: public Button {
 
public: 
  DigitalButton(int pin);
  ButtonState getState();
  void addHandler(EventHandler e);
  void check();
private:
  int pin, n;
  ButtonState state;
  EventHandler handler[MAX_BUTTON_EVENT_HANDLER];
};

#endif
