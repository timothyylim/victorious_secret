#!/bin/bash

extension=.gz
extensionXML=.xml

#Run all the matches specified by config files in the folder 'batchMatchesToRun'
for i in $( ls batchMatchesToRun/ ); do
    ant -Dbc.conf=batchMatchesToRun/$i headless
done

#Once the matches have been run. Clean up all the replay files into xml format
directory="rms_replays/"
for i in $( ls $directory ); do
    echo "Processing replay file $i"
    filename="$directory$i"
    mv $filename $filename$extension
    gunzip $filename$extension
    mv $filename $filename$extensionXML
done

#Once all the rms files have been processed move them to the xml_replays file
echo "moving all xml replay files to xml_replays folder"
newDirectory="xml_replays/"
mv $directory* $newDirectory

#FOR BATCH ANALYSIS

#for i in $( ls $newDirectory ); do
#    echo "Analysing replay file $i"
#    filename="$newDirectory$i"
#    python analysis.py $filename
#done
