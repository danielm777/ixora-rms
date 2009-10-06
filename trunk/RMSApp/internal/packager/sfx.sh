#!/bin/sh
echo ""
echo "IxoraRMS v1.0 - starting installation... please wait"
echo ""

# create a temp directory to extract to.
WRKDIR=`mktemp -d /tmp/selfextract.XXXXXX`
export WRKDIR

SKIP=`awk '/^__ARCHIVE_FOLLOWS__/ { print NR + 1; exit 0; }' $0`

# Take the TGZ portion of this file and pipe it to tar.
tail +$SKIP $0 | bzip2 -dc | tar -xpf -C $WRKDIR

# execute the installation script

PREV=`pwd`
cd $WRKDIR
#chmod -R 777 .
. ./setup.sh


# delete the temp files
cd $PREV
rm -rf $WRKDIR

exit 0

__ARCHIVE_FOLLOWS__
