#include <iostream>
#include <wiringPi.h>
#include <fstream>
#include <cmath>
#include <string>


#include <stdio.h> 
#include <sys/socket.h> 
#include <arpa/inet.h> 
#include <unistd.h> 
#include <string.h> 
#include <sys/time.h>

#define ADDRESS "127.0.0.1"
#define PORT 8018

#define OBSTACLE_THRESHOLD 15
#define MAXIMUM_DELTA 15


#define TRIG 0
#define ECHO 7

using namespace std;

char *tosend = "msg(obstacle,event,sonaralone,none,obstacle(1),1)\n";
int sock = 0, valread; 
struct sockaddr_in serv_addr; 

void setupSonar() {
	pinMode(TRIG, OUTPUT);
    digitalWrite(TRIG, LOW);
    pinMode(ECHO, INPUT);

    delay(30);
}

int setup() {

    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) 
    { 
        printf("\n Socket creation error \n"); 
        return -1; 
    } 
   
    serv_addr.sin_family = AF_INET; 
    serv_addr.sin_port = htons(PORT); 
       
    // Convert IPv4 and IPv6 addresses from text to binary form 
    if(inet_pton(AF_INET, ADDRESS, &serv_addr.sin_addr)<=0)  
    { 
        printf("\nInvalid address/ Address not supported \n"); 
        return -1; 
    } 
   
    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0) 
    { 
        printf("\nConnection Failed \n"); 
        return -1; 
    } 

	wiringPiSetup();

	setupSonar();

	return 0;
}


long getMicrotime(){
  struct timeval currentTime;
  gettimeofday(&currentTime, NULL);
  
  return currentTime.tv_sec * (int)1e6 + currentTime.tv_usec;
}

int getCM() {
	
	digitalWrite(TRIG, HIGH);
  usleep(10);
  digitalWrite(TRIG, LOW);

  int echo, previousEcho, lowHigh, highLow;
  long startTime, stopTime, difference;
  float rangeCm;
  lowHigh = highLow = echo = previousEcho = 0;
  int count = 0;
  while(0 == lowHigh || highLow == 0) {
    if(count > 100000) {
      return -1;
    }
    count++;
    previousEcho = echo;
    echo = digitalRead(ECHO);
    if(0 == lowHigh && 0 == previousEcho && 1 == echo) {
      lowHigh = 1;
      startTime = getMicrotime();
    }
    if(1 == lowHigh && 1 == previousEcho && 0 == echo) {
      highLow = 1;
      stopTime = getMicrotime();
    }
  }
  difference = stopTime - startTime;
  rangeCm = difference / 58;
	
	return rangeCm;
}

int main(void) {
	int cm;
  int lastcm;	
 	if(setup() != 0) {
		cout <<  "Setup failed!" <<   endl;
		return -1;
	}
	cout <<  "Setup succeeded!" <<   endl;
	
  lastcm = -1;
  
  while(1) {
		cm = getCM();
    lastcm = lastcm == -1 ? cm : lastcm;
		cout <<  cm <<   endl;
		
    if ((lastcm-cm) <= MAXIMUM_DELTA && cm < OBSTACLE_THRESHOLD && cm != -1) {
			send(sock , tosend , strlen(tosend) , 0 ); 
			printf("Obstacle event sent\n"); 
		} else if(cm == -1) {
      setupSonar();
    }
    
    lastcm = cm == -1 ? lastcm : cm;
	}
	return 0;
}
