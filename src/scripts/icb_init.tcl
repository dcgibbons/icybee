#
# $Id: icb_init.tcl,v 1.5 2003/08/14 20:18:36 dcgibbons Exp $
#
# Internet CB Client
#
# Copyright © 2001-2002 David C. Gibbons
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

#
#
#
c_usage add beep g "nick" "send someone a beep"
proc beep nick {
    s_beep $nick
}

#
#
#
c_usage add drop nickname "\[password\]" "disconnect someone who is using your registered nickname"
proc drop {args} {
    eval s_drop $args
}

#
#
#
c_usage add cp "\[old\]" "\[new\]" "change your registration password"
proc cp {args} {
    eval s_chpw $args
}

#
# motd command
#
c_usage add motd i "" "print the ICB message of the day"
proc motd {} {
    s_motd
}

#
# group commands
#
c_usage add boot m "nick" "kick someone out of your group"
proc boot {args} {
     eval s_group remove $args
}

c_usage add cancel m "nick" "cancel an invitation"
proc cancel nick {
    s_group cancel $nick
}

c_usage add group g "group" "change groups"
proc group group {
    s_group change $group
}
proc g group {
    s_group change $group
}

c_usage add hclear g "" "clear the \"tab\" history buffer"
proc hclear {} {
    c_hclear
}

c_usage add hdel g "" "remove a nickname from the \"tab\" history buffer"
proc hdel {nick} {
    c_hdel $nick
}

c_usage add invite m "nick" "invite people to your group"
proc invite nick {
    s_group invite $nick
}

c_usage add log g "\[filename\]" "toggle session logging"
proc log {args}	{
    eval c_log $args
}

c_usage add new m "group" "create your own group"
proc new group {
    s_group create $group
}

c_usage add pass m "\[nick\]" "pass moderation of your group"
proc pass {args} {
     s_group pass $args
}

c_usage add read m "" "read your messages"
proc read {args} {
	s_read
}

c_usage add status m "" "change or see the status of a group"
proc status {args} {
    s_group status $args
}

c_usage add topic m "topicname" "set your group's topic"
proc topic args {
    s_group topic $args
}

c_usage add write m "nick" "sends a message"
proc write {nick args} {
	 eval s_write $nick {$args}
}

#
# personal message
#
c_usage add m g "nick message" "send personal message"

proc m {args} {
     eval s_personal $args
}

proc message {args} {
     eval s_personal $args
}

#
# nickname registration
#
c_usage add register g "\[password\]" "register your nickname"
proc register {args} {
     s_register $args
}

c_usage add p g "\[password\]" "register your nickname"
proc p {args} {
     s_register $args
}

c_usage add send g "message" "send text to your group"
proc send args {
     s_send $args
}

#
#
#
c_usage add echoback g "on|off|verbose" "turn off/on echoback"
proc echoback onoff {
    s_echoback $onoff
}

c_usage add nick g "newnick" "change your nickname"
proc nick newnick {
    s_nick $newnick
}

#
#
#
c_usage add version i "\[-s|-c\]" "display server and/or client version info"
proc version {args} {
    if { [ string compare $args "-c" ] != 0 } {
        s_version
    }
    if { [ string compare $args "-s" ] != 0 } {
        c_version
    }
}

#
#
#
c_usage add who i "\[.\] \[group\]" "list users"
proc who {args} {
     s_who $args
}
proc w {args} {
     s_who $args
}

#
# admin commands
#
#proc a_su pass {
#    s_nick admin $pass
#}
#proc a_drops args {
#     s_admin drop $args
#}
proc a_shutdown {when args} {
     s_admin shutdown $when $args
}
proc a_wall args {
     s_admin wall $args
}
