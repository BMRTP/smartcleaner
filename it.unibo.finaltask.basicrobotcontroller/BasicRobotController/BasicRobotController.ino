#include "HBridge.h"
#include "MPU6050.h"
#include "Led.h"
#include "Utils.h"
#include "PID.h"
#include "StateMachine.h"

#define BAUD_RATE 115200


//HBridge pins
#define LEFT_MOTOR_PIN 5
#define RIGHT_MOTOR_PIN 6
#define LEFT_MOTOR_INV_PIN 11
#define RIGHT_MOTOR_INV_PIN 10


//States
#define DO_NOTHING 0
#define FORWARD 1
#define FORWARDING 2
#define ROTATE 3
#define ROTATING 4
#define SHUTDOWN_MOTORS 5

#define AHEAD 1
#define BACKWARD -1
#define LEFT 1
#define RIGHT -1


float fullRotationX = 11385;

HBridge *motors;
MPU6050* mpu6050;
Led* debugLed;
PID* rotatePid;
PID* forwardPid;
StateMachine* stateMachine;

long forwarding_start = 0;
float forward_target_angle = 0;
float forward_direction = 1; //1 = forward, -1 = backward
float rotate_target_angle = 0;
long rotating_start = 0;
char lastMovementCmd = '?';
long forwarding_time = seconds(4.0), rotating_time = seconds(2);
long forward_start_time = now();

int forward_power = 255;
int rotate_power = 255;
int keep_position_power = 255;

void setup(){
  Serial.begin(BAUD_RATE);
  Serial.println("Initializing...");

  debugLed = new Led(13);
  
  motors = new HBridge(LEFT_MOTOR_PIN, LEFT_MOTOR_INV_PIN, RIGHT_MOTOR_PIN, RIGHT_MOTOR_INV_PIN);
  
  mpu6050 = new MPU6050();
  mpu6050->autocalibration(true);

  rotatePid = new PID(10, 50, 1.2f, 1);
  forwardPid = new PID(50, 10, 3, 2);

  initializeStateMachine();
  //keep_position(seconds(600));
  
  
  Serial.println("Ready!");
}
void loop() {
  debugLed->update();
  mpu6050->update();
  stateMachine->update();
  Serial.flush();
}

void initializeStateMachine() {
  stateMachine = new StateMachine(SHUTDOWN_MOTORS);
  
  stateMachine->setState(DO_NOTHING, []() -> void { });
  
  stateMachine->setState(SHUTDOWN_MOTORS, []() -> void { 
    debugLed->pulse(0.5);
    motors->stop();
    stateMachine->transition(DO_NOTHING);
  });
  
  stateMachine->setState(FORWARD, []() -> void { 
    forwarding_start = now();
    forwardPid->reset(); 
    stateMachine->transition(FORWARDING);
  });
  
  stateMachine->setState(FORWARDING, []() -> void {
    debugLed->pulse(10);
    if(!isExpired(forwarding_start, forwarding_time)) {
      float ta = forward_target_angle;
      float ca = mpu6050->angleX * 360.0f / fullRotationX;
      float da = crop180Angle(ca - ta);
      float error = absoluteMin(da, absoluteMin(da - 360.0f, da + 360.0f)) / 180.0f; //[-1,1]
      //Serial.print("TA: "); Serial.print(ta); Serial.print(" CA: "); Serial.print(ca); Serial.print(" DA: "); Serial.print(da); Serial.print(" ERR: "); Serial.println(error);
      float attenuation = forwardPid->update(error);
      motors->leftPower(forward_direction - attenuation);
      motors->rightPower(forward_direction + attenuation);
    } else {
      stateMachine->transition(SHUTDOWN_MOTORS);
    }
  });
  
  stateMachine->setState(ROTATE, []() -> void { 
    rotating_start = now();
    rotatePid->reset();
    stateMachine->transition(ROTATING);
  });
  
  stateMachine->setState(ROTATING, []() -> void { debugLed->pulse(20);
    if(!isExpired(rotating_start, rotating_time)) {
      float ta = rotate_target_angle;
      float ca = mpu6050->angleX * 360.0f / fullRotationX;
      float da = crop180Angle(ca - ta);
      float error = absoluteMin(da, absoluteMin(da - 360.0f, da + 360.0f)) / 180.0f; //[-1,1]
      //float error = mpu6050->angleX - ((rotate_target_angle / 360.0f) * fullRotationX);
      float power = rotatePid->update(error);
      motors->leftPower(-power);
      motors->rightPower(power);
    } else {
      stateMachine->transition(SHUTDOWN_MOTORS);
    }
  });

  stateMachine->setExernalInputHandler(handleCommands);
}

void handleCommands() {
  if(Serial.available() > 0) {
    char v = (char)Serial.read();
   
    if(v == 'h') {
      if(stateMachine->getState() == FORWARDING) { //keep goin straight while decelerating
        float forward_total_time = toSeconds(now() - forward_start_time);
        float x = forward_total_time / 0.7f;              //X normalization
        float y = (1.0f)/(1.0f+pow(2.718f,(0.5f-x)*10));  //sigmoid
        float brake_time = y * 0.1f;                      //y normalization
        forward(-forward_direction, 0, seconds(brake_time));
        //keep_position(seconds(1));
      } else {                                     //no need to counter rotate
        shutdown_motors();
      }
      Serial.println("h");
    } else if(v == 'w') {
      forward(AHEAD, 0, seconds(60));
      Serial.println("w");
    } else if(v == 's') {
      forward(BACKWARD, 0, seconds(60));
      Serial.println("s");
    } else if(v == 'd') {
      rotate(RIGHT, 90, seconds(2), true);
      Serial.println("d");
    } else if(v == 'a') {
      rotate(LEFT, 90, seconds(2), true);
      Serial.println("a");
    } else if(v == 'o') {
      keep_position(seconds(60));
      Serial.println("o");
    } else if(v == 'p') {
       int p = between(0, 255, Serial.parseInt());
       Serial.print("Max power output: "); Serial.println(p);
       motors->maxPWMOutput(p);
    } else if(v == 't') {
      if(stateMachine->getState() == FORWARDING && forward_direction == AHEAD) {
        forward_target_angle = Serial.parseInt();
      } else {
        forward(AHEAD, Serial.parseInt(), seconds(60));
      }
      Serial.println("t");
    }
    
    
    else if(v == 'k') {
      mpu6050->resetAngles();
      Serial.println("Resetted X angle reference");
    } else if(v == 'm') {
      Serial.print("Angle X: "); Serial.println(mpu6050->angleX);
      Serial.print("Avg GyX: "); Serial.println(mpu6050->getAvgGyX());
    } else if(v == 'j') {
      fullRotationX = mpu6050->angleX;
      Serial.print("360 rotation set to: "); Serial.println(abs(fullRotationX));
    } else if(v == 'f') {
      update_pid_from_serial(forwardPid);
    } else if(v == 'r') {
      update_pid_from_serial(rotatePid);
    }

    if(v == 'w' || v == 's' || v == 'a' || v == 'd') {
      lastMovementCmd = v;
    }
  }
}
void update_pid_from_serial(PID* pid) {
  int p = Serial.parseInt();
  int i = Serial.parseInt();
  int d = Serial.parseInt();
  pid->setParameters(p / 100.0f, i / 100.0f, d / 100.0f);
  Serial.print("pid: ");
  Serial.print(p); Serial.print(" ,"); Serial.print(i); Serial.print(" ,"); Serial.println(d);
}

/* ACTIONS */
void forward(float direction, float target_angle, long duration) {
  stateMachine->transition(FORWARD);
  forward_direction = direction;
  forward_target_angle = crop180Angle(target_angle);
  if(lastMovementCmd == 'a' || lastMovementCmd == 'd') {
    mpu6050->resetAngles();
  }
  forwarding_time = duration;
  motors->maxPWMOutput(forward_power);

  if(direction > 0) {//TODO remove
    mpu6050->resetAngles(); //TODO remove
  }//TODO remove
  forward_start_time = now();
}
void rotate(float direction, float target_angle, long duration, bool resetAngles) {
  stateMachine->transition(ROTATE);
  rotate_target_angle = crop180Angle(direction * target_angle);
  rotating_time = duration;
  if(resetAngles) {
    mpu6050->resetAngles();
  }
  mpu6050->resetAngles(); //TODO remove
  motors->maxPWMOutput(rotate_power);
}
void keep_position(long duration) {
  rotate(LEFT, 0, duration, false);
  motors->maxPWMOutput(keep_position_power);
}
void shutdown_motors() {
  stateMachine->transition(SHUTDOWN_MOTORS);
}
