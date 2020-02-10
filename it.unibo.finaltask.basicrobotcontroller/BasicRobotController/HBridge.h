#ifndef __H_BRIDGE__
#define __H_BRIDGE__

class HBridge {
public:
  HBridge(int leftForward, int leftReverse, int rightForward, int rightReverse);
  void leftPower(float p);
  void rightPower(float p);
  void stop();
  void maxPWMOutput(int v);
private:
int leftForward, leftReverse, rightForward, rightReverse;
int maxPWM;
};

#endif
