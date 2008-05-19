#!/bin/sh
#
# IcyBee - http://www.nuclearbunny.org/icybee/
# A client for the Internet CB Network - http://www.icb.net/
#
# Copyright (C) 2000-2008 David C. Gibbons
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#

if [ "$JAVA_HOME" = "" ] ; then
    echo You must set the JAVA_HOME environment variable to point at your JDK.
    exit 1
fi

_ANT_HOME=../jakarta-ant
if [ "$ANT_HOME" != "" ] ; then
    _ANT_HOME=$ANT_HOME
fi
export _ANT_HOME
echo Using _ANT_HOME=$_ANT_HOME

_CLASSPATH=$_ANT_HOME/lib/ant.jar:$JAVA_HOME/lib/tools.jar
if [ "$CLASSPATH" != "" ] ; then
    _CLASSPATH=$_CLASSPATH:$CLASSPATH
fi
export _CLASSPATH

echo Building with classpath $_CLASSPATH
echo Using _ANT_OPTS=$_ANT_OPTS

$JAVA_HOME/bin/java -classpath "$_CLASSPATH" org.apache.tools.ant.Main $_ANT_OPTS $*

