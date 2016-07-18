# Minecraft Ultrahardcore Admin

This is a minecraft server plugin that works with any minecraft server.

## Prerequisites
* Prefered tool of choice : [Intellij](http://www.jetbrains.com/idea/)
* JRE 1.8

## Install
##### Install JRE1.8:
###### windows
download and install from oracle site
###### debian
create file: /etc/apt/sources.list.d/webupd8team-java.list

add to that file: 
```
deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main
```
run command:
```
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
apt-get update
apt-get install oracle-java8-installer
```

##### Create a jar from source
- make a run configuration in the maven projects windows with the following command line parameters: clean package shade:shade
- Build the program with this new config, copy mua.jar and mua.xml to your directory of choice, edit mua.xml and run program with 

```
java -jar mua.jar
```

## Contributing
see [Contributing](CONTRIBUTING.md)
