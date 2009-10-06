#!/bin/sh

# Creating package for x86 Solaris target
mkdir output/x86-solaris
mv ./setup.jar ./x86-solaris
./makeself.sh --bzip2 --nomd5 --nocrc ./x86-solaris ./output/x86-solaris/install.bin "IxoraRMS v1.0" ./setup.sh
mv ./x86-solaris/setup.jar ./
