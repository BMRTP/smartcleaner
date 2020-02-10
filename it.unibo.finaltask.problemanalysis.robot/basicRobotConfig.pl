robotType(virtual). %% virtual | real

virtualRobot(8999). %% VIRTUAL_PORT
%%realRobot(8999, localhost:8080). %% SERIAL_PORT, FRONTEND_URL

robot( virtual, 8999 ).     %%the port is the default used by clientWenvObjTcp.kt
%%robot( real, "COM21" ).  %% /dev/ttyUSB0
