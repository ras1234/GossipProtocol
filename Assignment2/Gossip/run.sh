#!/bin/bash

javac -classpath $PWD/src/gossip/protobuf-java-2.5.0.jar src/gossip/*.java
#gnome-terminal --tab -e 'bash -c "cd $PWD/src;java -cp gossip/protobuf-java-2.5.0.jar: gossip.Server ; exec bash"'
a="$1"
b="$2"
c="$3"
d="$4"
e=$5
f=$6
eventgenerators=5
totalprocess=10
inputfile="input.txt"
if [[ $# == 6 ]]
then
if [[ $a == "-p" ]];
then
   eventgenerators=$2
else 
	eventgenerators=5
fi

if [[ $c == "-n" ]] 
then
	totalprocess=$4
else
	totalprocess=10
fi

if [[ $e == "-i" ]]
then
	inputfile=$6
else
	inputfile="input.txt"
fi
elif [[ $# == 4 ]]
then
	eventgenerators=$1
	totalprocess=$2
	inputfile=$4
fi

for ((i=0;i<$eventgenerators;i+=1))
do		
 	gnome-terminal --tab -e "bash -c \"cd $PWD/src;java -cp gossip/protobuf-java-2.5.0.jar: gossip.Server $i $totalprocess -i $inputfile; exec bash\""

done	
for ((i=eventgenerators;i<$totalprocess;i+=1))
do 
	gnome-terminal --tab -e "bash -c \"cd $PWD/src;java -cp gossip/protobuf-java-2.5.0.jar: gossip.Server $i $totalprocess ; exec bash\""
done	

	

