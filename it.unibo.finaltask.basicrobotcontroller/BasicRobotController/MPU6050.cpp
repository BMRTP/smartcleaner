#include<Wire.h>
#include "arduino.h"
#include "MPU6050.h"
#include "Utils.h"

float computeArea(float v1, float v2, float dt) {
  //Compute area as rectangle trapeze
  if (v1 <= 0 && v2 <= 0 || v1 >= 0 && v2 >= 0) {
    return sign(v1) * (max(abs(v1), abs(v2)) * dt - (abs(v2 - v1) * dt / 2));
  }
  float x = -v1 * dt / (v2 - v1);
  return v1 * x / 2 + v2 * (dt - x) / 2;
}


void MPU6050::recalibrate(float factor) {
  offsetGyX -= avgSumGyX / avgCountGyX * factor;
  offsetGyY -= avgSumGyY / avgCountGyY * factor;
  offsetGyZ -= avgSumGyZ / avgCountGyZ * factor;
  resetAvgs();
}
float MPU6050::getAvgGyX() {
  return avgSumGyX / avgCountGyX;
}
void MPU6050::autocalibration(bool v) {
  this->autocalib = v;
  if(v) {
    this->resetAvgs();
  }
}
void MPU6050::resetAvgs() {
  avgSumGyX = avgSumGyY = avgSumGyZ = 0;
  avgCountGyX = avgCountGyY = avgCountGyZ = 0;
}
void MPU6050::resetAngles() {
  angleX = angleY = angleZ = 0;
}
void MPU6050::computeAngles() {
  float dt = (currentReadMCUTime - lastReadMCUTime) / 1000000.0f;
  float v1 = lastGyX + offsetGyX;
  float v2 = GyX + offsetGyX;
  if (abs(v2) > MPU6050_THRESHOLD_GY) {
    angleX += computeArea(v1, v2, dt);
  } else {
    avgSumGyX += v2;
    avgCountGyX++;
  }
  if(this->autocalib) {
    if(abs(autocalibAngleX - angleX) > abs(this->getAvgGyX())) {
      this->resetAvgs();
      autocalibAngleX = angleX;
    } else {
      if(this->avgCountGyX >= 2000 && abs(this->getAvgGyX()) > 0.1) {
        this->recalibrate(1.0f);
        Serial.println("Autocalibrated");
      }
    }
  }
}

void MPU6050::update() {
  lastGyX = GyX;
  lastGyY = GyY;
  lastGyZ = GyZ;
  lastReadMCUTime = currentReadMCUTime;
  AcX=0;
  AcY=0;
  AcZ=0;
  Tmp=0;
  GyX=0;
  GyY=0;
  GyZ=0;
  for(int i = 0; i < MPU6050_READ_AVG_COUNT; i++)
  {
    Wire.beginTransmission(0x68);
    Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
    Wire.endTransmission(false);
    Wire.requestFrom(0x68,14,true);  // request a total of 14 registers
    AcX+=Wire.read()<<8|Wire.read();  // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)     
    AcY+=Wire.read()<<8|Wire.read();  // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
    AcZ+=Wire.read()<<8|Wire.read();  // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
    Tmp+=Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)
    GyX+=Wire.read()<<8|Wire.read();  // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
    GyY+=Wire.read()<<8|Wire.read();  // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
    GyZ+=Wire.read()<<8|Wire.read();  // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)
  }
  currentReadMCUTime = micros();
  AcX /= MPU6050_READ_AVG_COUNT;
  AcY /= MPU6050_READ_AVG_COUNT;
  AcZ /= MPU6050_READ_AVG_COUNT;
  Tmp /= MPU6050_READ_AVG_COUNT;
  GyX /= MPU6050_READ_AVG_COUNT;
  GyY /= MPU6050_READ_AVG_COUNT;
  GyZ /= MPU6050_READ_AVG_COUNT;
  computeAngles();
}

MPU6050::MPU6050() {
  offsetGyX = MPU6050_INITIAL_OFFSET_GYX;
  offsetGyY = MPU6050_INITIAL_OFFSET_GYY;
  offsetGyZ = MPU6050_INITIAL_OFFSET_GYZ;
  lastReadMCUTime = 0;
  currentReadMCUTime = 0;
  AcX = AcY = AcZ = Tmp = GyX = GyY = GyZ = 0;
  lastGyX = lastGyY = lastGyZ = 0;
  angleX = angleY = angleZ = 0;
  avgSumGyX = avgSumGyY = avgSumGyZ = 0;
  avgCountGyX = avgCountGyY = avgCountGyZ = 0;
  autocalib = false;
  autocalibAngleX = 0.0f;
  
  Wire.begin();
  Wire.beginTransmission(0x68);           //begin, Send the slave adress (in this case 68)              
  Wire.write(0x6B);                       //make the reset (place a 0 into the 6B register)
  Wire.write(0x00);
  Wire.endTransmission(true);             //end the transmission
  //Gyro config
  Wire.beginTransmission(0x68);           //begin, Send the slave adress (in this case 68) 
  Wire.write(0x1B);                       //We want to write to the GYRO_CONFIG register (1B hex)
  Wire.write(0x10);                       //Set the register bits as 00010000 (1000dps full scale)
  Wire.endTransmission(true);             //End the transmission with the gyro
  //Acc config
  Wire.beginTransmission(0x68);           //Start communication with the address found during search.
  Wire.write(0x1C);                       //We want to write to the ACCEL_CONFIG register
  Wire.write(0x10);                       //Set the register bits as 00010000 (+/- 8g full scale range)
  Wire.endTransmission(true); 
}

void send16(int value)
{
  Serial.write((byte)(value & 0xFF));
  Serial.write((byte)((value >> 8) & 0xFF));
}
void MPU6050::sendData() {
  send16(0);
  send16(65535);
  send16(0);
  send16(65535);
  send16((int16_t)AcX);
  send16((int16_t)AcY);
  send16((int16_t)AcZ);
  send16((int16_t)Tmp);
  send16((int16_t)GyX);
  send16((int16_t)GyY);
  send16((int16_t)GyZ);
}
