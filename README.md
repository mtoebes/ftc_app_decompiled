# ftc_app_decompiled 

This project's goal to expose the implementation of ftc_app's dependancy libraries in the hope of aiding opmode development

See [ftctechnh/ftc_app](https://github.com/ftctechnh/ftc_app) for the offical FTC app.

Currently, you are able to browse decompiled classes within android studio but it can be difficult to follow due to auto-generated variable/method names, lack of documentation, compile errors, and unintutive logic. In particular, the limited javadoc documentation is very frustrating. These are all issues I would like to see eased by this project. 

Any refactoring of logicis intended to aid readability, not enhance functionality. 

I <b>strongly</b> recommend that you still build your APK with the offical libraries to avoid any unforseen bugs. 

## Files of Note
* [Docompiler-Libs](https://github.com/mtoebes/ftc_app_decompiled/tree/master/Decompiled-Libs) : Raw output from JADX decompiler
* [doc/javadoc](https://github.com/mtoebes/ftc_app_decompiled/tree/master/doc/javadoc) : Documentation for this project. You can view it online [here](http://mtoebes.github.io/ftc_app_decompiled/doc/javadoc/)
* [FtcRobotController/src/.../qualcomm](https://github.com/mtoebes/ftc_app_decompiled/tree/master/FtcRobotController/src/main/java/com/qualcomm) : Java packages

## Libraries
High-level description of the functionality of the libraries 

* Analytics - Sends analytics to qualcomm server
* FtcCommon - App's user interface
* Hardware -  inplentation for communicating with phyical device
* MordernRobotics - handler for USB communication
* RobotCore - utils/models/logic for interacting with devices
* WirelessP2p - handler for wireless communication

## Current Project Status
[RobotCore](https://github.com/mtoebes/ftc_app_decompiled/tree/master/FtcRobotController/src/main/java/com/qualcomm/robotcore) has been added to FtcRobotController. The code has been refactored to be readable and has original javadoc comments. Most logic changes are to simplify code flow and enhance readability. Have not been able to fully test the compiled app.

ftc_app Release 15.11.04.001
