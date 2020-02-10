# Project: SmartCleaner - Laboratory of Software systems. (A. Natali)
A smart room cleaner, using the qak metamodel

### Deployment on virtual robot

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


_______

Made with love by  
<edoardo.barbieri4@studio.unibo.it>  
<lorenzo.mondani@studio.unibo.it>  
<emanuele.pancisi@studio.unibo.it>  
<daniele.rossi18@studio.unibo.it>  
<giacomo.tontini@studio.unibo.it>  

