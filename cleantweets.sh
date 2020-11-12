#!

INPUT=$1
OUTPUT=$2


for i in `seq 0 7`;
do
    DATE=`jshon -e data < $INPUT | jshon -e $i -e created_at | cut -c2-11`
    ID=`jshon -e data < $INPUT | jshon -e $i -e id`
    TEXT=`jshon -e data < $INPUT | jshon -e $i -e text`
    RETWEETS=`jshon -e data < $INPUT | jshon -e $i jshon -e public_metrics -e retweet_count`
    FAVORITES=`jshon -e data < $INPUT | jshon -e $i jshon -e public_metrics -e like_count`
    echo "$ID,$DATE,$RETWEETS,$FAVORITES,$TEXT" >> $OUTPUT
done
