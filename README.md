# Project: SmartCleaner - Laboratory of Software systems. (A. Natali)
A smart room cleaner, using the qak metamodel

## Deployment on virtual robot

Launch `gradle build eclipse` within the following folders:
  - `it.unibo.finaltask.project.robot`
  - `it.unibo.finaltask.project.detector`
  - `it.unibo.finaltask.project.plasticbox`
  - `it.unibo.finaltask.project.wroom`
  - `it.unibo.finaltask.project.coapserver`

Import each project in eclipse, open each .qak file. You will be asked to convert the project in a xtext project. After the acceptance, 
kotlin files will be generated by our software factory.

From now on, be sure to have a [virtual robot enviroment](https://github.com/anatali/iss2020Lab/tree/master/it.unibo.virtualRobot2020) instance running on your machine.

Launch `gradle run`, stricly in order within the following folders:
  - `it.unibo.finaltask.project.coapserver`
  - `it.unibo.finaltask.project.robot`
  - `it.unibo.finaltask.project.detector`
  - `it.unibo.finaltask.project.plasticbox`
  - `it.unibo.finaltask.project.wroom`
  
  To interact with the robot, launch `node index.js` within `it.unibo.finaltask.frontend`. (The first time `npm install` is required).


## Deployment on physical robot

To deploy the project on the real robot you have to make a little extra effort.

### Raspberry
First, create executable jars through the following commands:
- `gradle -b build_ctxWRoom.gradle distZip` within `it.unibo.finaltask.project.wroom` folder
- `gradle -b build_ctxRobot.gradle distZip` within `it.unibo.finaltask.project.robot` folder
Move the generated jars into a folder on the raspberry pi along side the *.pl and *.c files. The prolog files need to be edited appropriately in such a way that the robot can reach the other node of the system and vice versa (mainly ip addresses and port).  
Compile *.c files, be sure the compiled objects have the same file name as the source ones.  
Execute the jar files and enjoy.   

### Arduino
Compile and push the project at `it.unibo.finaltask.basicrobotcontroller/BasicRobotController` onto the Arduino.
______

Made with love by  
<edoardo.barbieri4@studio.unibo.it>  
<lorenzo.mondani@studio.unibo.it>  
<emanuele.pancisi@studio.unibo.it>  
<daniele.rossi18@studio.unibo.it>  
<giacomo.tontini@studio.unibo.it>  

