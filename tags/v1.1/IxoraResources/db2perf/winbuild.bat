set MINC=..\..\..\..\DevTools\minc\bin
set JAVA_HOME=..\..\..\Tools\jdk1.5.0
"%MINC%\gcc" -shared -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -o ..\..\RMS\bin\x86-win32\bin\db2perf.dll db2mon.cpp names.cpp snapshot.cpp db2libs\db2api.lib -lsupc++ -lstdc++ 

pause