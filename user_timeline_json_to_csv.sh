#!/usr/bin/bash

if [ $# -ne 2 ] 
	then 
		echo "<input json path> <output csv path>" 
else 
	FILEPATH=`realpath $1`
	JSON=`jshon -F $FILEPATH`
	COUNT=`echo $JSON | jshon -l`
    for i in `seq 1 $(($COUNT-1))`;
    do 
        CREATED_AT=`echo $JSON | jshon -e $i | jshon -e created_at`
        ID=`echo $JSON | jshon -e $i | jshon -e id_str`
        RETWEETS=`echo $JSON | jshon -e $i | jshon -e retweet_count`
        FAVORITES=`echo $JSON | jshon -e $i | jshon -e favorite_count`
        TEXT=`echo $JSON | jshon -e $i | jshon -e text`
        
        echo "$ID, $CREATED_AT, $RETWEETS, $FAVORITES, $TEXT" >> $2
    done

fi
