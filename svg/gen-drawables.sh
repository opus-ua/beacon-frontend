#!/bin/bash

if [ -z "$(which inkscape)" ]; then
    echo "Inkscape must be installed. Use brew if on OS X."
    exit 1
fi

RES_NAMES=(ldpi mdpi hdpi xhdpi xxhdpi xxxhdpi)
RES_DIMS=(120 150 240 320 480 640)

for i in ${!RES_NAMES[*]}
do
    RES_NAME=${RES_NAMES[$i]}
    RES_DIM=${RES_DIMS[$i]}
    IN_DIR="../app/src/main/res/drawable-$RES_NAME"

    for IN_FILE in drawable/*.svg
    do
        # inkscape beacon_marker.svg -e out/beacon_marker.png -d 160    
        PNG_FILE=$(echo $IN_FILE | sed "s/\.svg/\.png/g" | sed "s/drawable\///g")
        OUT_FILE=$IN_DIR/$PNG_FILE
        inkscape $IN_FILE -e $OUT_FILE -d $RES_DIM -D
    done
done
