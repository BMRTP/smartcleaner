#ifndef __MPU6050__
#define __MPU6050__

#define MPU6050_READ_AVG_COUNT 1
#define MPU6050_INITIAL_OFFSET_GYX 32.5f
#define MPU6050_INITIAL_OFFSET_GYY 0.0f
#define MPU6050_INITIAL_OFFSET_GYZ 0.0f
#define MPU6050_THRESHOLD_GY 20.0f

class MPU6050 {
public:
  MPU6050();
  void update();
  void recalibrate(float factor);
  void resetAvgs();
  void resetAngles();
  void sendData();
  void autocalibration(bool v);
  float getAvgGyX();
  
  float AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;
  float angleX, angleY, angleZ;
  
private:
  void computeAngles();

  float lastGyX, lastGyY, lastGyZ;
  long currentReadMCUTime, lastReadMCUTime;
  float offsetGyX, offsetGyY, offsetGyZ;
  float avgSumGyX, avgSumGyY, avgSumGyZ;
  long avgCountGyX, avgCountGyY, avgCountGyZ;
  bool autocalib;
  float autocalibAngleX;
};



#endif
