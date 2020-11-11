
JSON=`curl \
 --get "https://api.twitter.com/1.1/statuses/user_timeline.json" \
 --data "screen_name=jeffbezos&count=200&trim_user=t" \
 --header "Authorization: Bearer AAAAAAAAAAAAAAAAAAAAAJ11JgEAAAAASxCRw9gMUY82AyTWqSQsMIn3NP4%3DUo0dRAYVTBswh7e6UJARKDuAlfEpunKimam6iPpQiYCcNtePSf" \
 -H "Content-type: application/json" \
 -H "Accept: application/json"`
OLDEST_ID=`echo $JSON | jshon -e 199 -e id`
for i in `seq 0 199`;
do
        CREATED_AT=`echo $JSON | jshon -e $i -e created_at`
        ID=`echo $JSON | jshon -e $i -e id_str`
        RETWEETS=`echo $JSON | jshon -e $i -e retweet_count`
        FAVORITES=`echo $JSON | jshon -e $i -e favorite_count`
        TEXT=`echo $JSON | jshon -e $i -e text`
        echo "$ID,$CREATED_AT,$RETWEETS,$FAVORITES,$TEXT" >> jeffbezos.csv
done

#   201-400, 401-600, 601-800, 801-1000, 1001-1200, 1201-1400
for p in `seq 1 6`;
do 
    JSON=`curl \
     --get "https://api.twitter.com/1.1/statuses/user_timeline.json" \
     --data "screen_name=jeffbezos&count=200&trim_user=t&max_id=$(($OLDEST_ID-1))" \
     --header "Authorization: Bearer AAAAAAAAAAAAAAAAAAAAAJ11JgEAAAAASxCRw9gMUY82AyTWqSQsMIn3NP4%3DUo0dRAYVTBswh7e6UJARKDuAlfEpunKimam6iPpQiYCcNtePSf" \
     -H "Content-type: application/json" \
     -H "Accept: application/json"`
    OLDEST_ID=`echo $JSON | jshon -e 199 -e id`
    for i in `seq 0 199`;
    do
            CREATED_AT=`echo $JSON | jshon -e $i -e created_at`
            ID=`echo $JSON | jshon -e $i -e id_str`
            RETWEETS=`echo $JSON | jshon -e $i -e retweet_count`
            FAVORITES=`echo $JSON | jshon -e $i -e favorite_count`
            TEXT=`echo $JSON | jshon -e $i -e text`
            echo "$ID,$CREATED_AT,$RETWEETS,$FAVORITES,$TEXT" >> jeffbezos.csv
    done
done 
#   last 100 tweets
JSON=`curl \
 --get "https://api.twitter.com/1.1/statuses/user_timeline.json" \
 --data "screen_name=jeffbezos&count=100&trim_user=t&max_id=$(($OLDEST_ID-1))" \
 --header "Authorization: Bearer AAAAAAAAAAAAAAAAAAAAAJ11JgEAAAAASxCRw9gMUY82AyTWqSQsMIn3NP4%3DUo0dRAYVTBswh7e6UJARKDuAlfEpunKimam6iPpQiYCcNtePSf" \
 -H "Content-type: application/json" \
 -H "Accept: application/json"`
for i in `seq 0 99`;
do
        CREATED_AT=`echo $JSON | jshon -e $i -e created_at`
        ID=`echo $JSON | jshon -e $i -e id_str`
        RETWEETS=`echo $JSON | jshon -e $i -e retweet_count`
        FAVORITES=`echo $JSON | jshon -e $i -e favorite_count`
        TEXT=`echo $JSON | jshon -e $i -e text`
        echo "$ID,$CREATED_AT,$RETWEETS,$FAVORITES,$TEXT" >> jeffbezos.csv
done
