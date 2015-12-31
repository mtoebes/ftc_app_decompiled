# Decompiled-Libs

raw output from [JADX decompiler](http://www.javadecompilers.com)

The library binaries are found under [FtcRobotController/libs](https://github.com/mtoebes/ftc_app_decompiled/tree/master/FtcRobotController/libs)

Steps to Reproduce:
1. Rename extension from .aar to .jar

2. Extract and open the contents of the jar

3. Upload the classes.jar to http://www.javadecompilers.com and choose Jadx
  * note: I originally tried to decompile them locally using fernflower but the decompiler had trouble handling the obfuscation. Its most likly that I missed setting a certain flag. 

4. Download and save the output to the source directory 
  * I'm storing this to have unaltered output for comparing against newer library versions

5. Extract contents
  * Output java files can be found under com directory  
  
6. Extract original classes.jar and compare filenames to ensure all class files were decompiled. 


## Extra

If you want to compile these libraries as standalone modules you will need to add some missing resources.

You are not required to fix this as the end goal is to extract the jara files into the main module. But if you want to do it anyway, you will need to create res/values/values.xml 

Your values.xml should include an AppTheme style and all the resources listed in R.txt You can use either RobotCore's or Ftcommon's values.xml as template 



