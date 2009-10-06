C:\DevTools\minc\bin\gcc -shared -IC:\Java\jdk1.6.0_10\include -IC:\Java\jdk1.6.0_10\include\win32 -IC:\Dev\Tools\minc\include\c++\3.4.2 -IC:\Dev\Tools\minc\include\c++\3.4.2\mingw32 -LC:\Dev\Tools\minc\lib -DLIB -D_WIN32 -Wl,--add-stdcall-alias -o ../../RMSApp/bin/x86-win32/bin/ping.dll ping.cpp -lwsock32 -lstdc++
strip ../../RMSApp/bin/x86-win32/bin/ping.dll

pause