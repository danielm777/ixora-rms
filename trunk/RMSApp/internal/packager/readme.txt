Run package.bat to build zip files for all platforms.

The result is generated under the 'output' folder ready for deployment: two files
for each platform: with and without JRE.

VER IMPORTANT: to preserve file permissions for JREs, they MUST NOT:
 - be copied to/from a non-NTFS partition
 - be zipped/unzipped with WinZip (except for Windows)

To unpack on Windows: unzip <package-name>
To unpack on Linux:   tar -xzf <package-name>
To unpack on Solaris: bzip2 -dc <package-name> | tar -xf -
To unpack on AIX:     bzip2 -dc <package-name> | tar -xf -

