# ftc_app_decompiled 

This is an attempt to decompile the qualcomm libraries used by ftc_app's [opmodes](https://github.com/mtoebes/ftc_app_decompiled/tree/master/FtcRobotController/src/main/java/com/qualcomm/ftcrobotcontroller/opmodes)

This project is intended to expose the full ftc_app implementation and to allow for improvments to the documentation both within the logic and in the javadocs.

I strongly recommend that you still build your APK with the offical libraries to avoid any unforseen bugs. 

Places of note:
* [Docompiler-Libs](https://github.com/mtoebes/ftc_app_decompiled/tree/master/Decompiled-Libs)
* [doc/javadoc](https://github.com/mtoebes/ftc_app_decompiled/tree/master/doc/javadoc) documentation for this project. You can view it online [here](http://mtoebes.github.io/ftc_app_decompiled/doc/javadoc/)
* [FtcRobotController/src/.../qualcomm](https://github.com/mtoebes/ftc_app_decompiled/tree/master/FtcRobotController/src/main/java/com/qualcomm) java packages

Libraries:
* Analytics - Sends analytics to qualcomm server
* FtcCommon - App's user interface
* Hardware -  inplentation for communicating with phyical device
* MordernRobotics - handler for USB communication
* RobotCore - utils/models/logic for interacting with devices
* WirelessP2p - handler for wireless communication

status:
RobotCore has been added to FtcRobotController. The code has been refactored to be readable and has original javadoc comments. Most logic changes are to simplify code flow and enhance readability. Have not been able to fully test the compiled app.

See [ftctechnh/ftc_app](https://github.com/ftctechnh/ftc_app) for the offical FTC app.

ftc_app Release 15.11.04.001
