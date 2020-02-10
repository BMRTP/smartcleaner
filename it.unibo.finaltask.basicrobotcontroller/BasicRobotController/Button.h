#ifndef __BUTTON__
#define __BUTTON__

typedef enum {ButtonPressed = 1, ButtonReleased = 0} ButtonState;
typedef void (*EventHandler) (ButtonState state);

class Button {
public:
  virtual ButtonState getState() = 0;
  virtual void addHandler(EventHandler e) = 0;
  virtual void check() = 0;
};



#endif

