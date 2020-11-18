##  gets 1500 most recent tweets from various twitter acounts and places them into csv files
##  or as many as twitter will give us twitter appears to have an inconsistent metric
##  for how many tweets to archive for a given user
##  csv format :    ID, TIME_CREATED, NUM_RETWEETS, NUM_FAVORITES, TEXT
## 15 minute maximum reply records is 1500
## most of these dont get close to that twitter appears to have some convoluted metrics for 
## how many tweets to archive for which user
## requires you have an environment variable TWITTER_BEARER for your bearer key

usernames=( tim_cook officialmcafee jeffbezos jack eholmes2003 elonmusk )
for user in ${usernames[*]};
do
#   first 200 tweets
    JSON=`curl \
     --get "https://api.twitter.com/1.1/statuses/user_timeline.json" \
     --data "screen_name=$user&count=200&trim_user=t" \
     --header "Authorization: Bearer $TWITTER_BEARER" \
     -H "Content-type: application/json" \
     -H "Accept: application/json"`
    OLDEST_ID=`echo $JSON | jshon -e 199 -e id`
    N_RESULTS=`echo $JSON | jshon -l`
    for i in `seq 0 ${N_RESULTS-1}`;
    do
            CREATED_AT=`echo $JSON | jshon -e $i -e created_at`
            ID=`echo $JSON | jshon -e $i -e id_str`
            RETWEETS=`echo $JSON | jshon -e $i -e retweet_count`
            FAVORITES=`echo $JSON | jshon -e $i -e favorite_count`
            TEXT=`echo $JSON | jshon -e $i -e text`
            if [ ! -z "$ID" -a ! -z "$CREATED_AT" ]; then
                echo "$ID,$CREATED_AT,$RETWEETS,$FAVORITES,$TEXT" >> $user.csv
            fi
    echo $user
    done
#   201-400, 401-600, 601-800, 801-1000, 1001-1200, 1201-1400
    for p in `seq 1 6`;
    do 
        JSON=`curl \
         --get "https://api.twitter.com/1.1/statuses/user_timeline.json" \
         --data "screen_name=$user&count=200&trim_user=t&max_id=$(($OLDEST_ID-1))" \
         --header "Authorization: Bearer $TWITTER_BEARER" \
         -H "Content-type: application/json" \
         -H "Accept: application/json"`
        OLDEST_ID=`echo $JSON | jshon -e 199 -e id`
        N_RESULTS=`echo $JSON | jshon -l`
        for i in `seq 0 ${N_RESULTS-1}`;
        do
                CREATED_AT=`echo $JSON | jshon -e $i -e created_at`
                ID=`echo $JSON | jshon -e $i -e id_str`
                RETWEETS=`echo $JSON | jshon -e $i -e retweet_count`
                FAVORITES=`echo $JSON | jshon -e $i -e favorite_count`
                TEXT=`echo $JSON | jshon -e $i -e text`
                if [ ! -z "$ID" -a ! -z "$CREATED_AT" ]; then
                    echo "$ID,$CREATED_AT,$RETWEETS,$FAVORITES,$TEXT" >> $user.csv
                fi
        done
    done 
    echo $user
#   last 100 tweets
    JSON=`curl \
     --get "https://api.twitter.com/1.1/statuses/user_timeline.json" \
     --data "screen_name=$user&count=100&trim_user=t&max_id=$(($OLDEST_ID-1))" \
     --header "Authorization: Bearer $TWITTER_BEARER" \
     -H "Content-type: application/json" \
     -H "Accept: application/json"`
    N_RESULTS=`echo $JSON | jshon -l`
    for i in `seq 0 ${N_RESULTS-1}`;
    do
            CREATED_AT=`echo $JSON | jshon -e $i -e created_at`
            ID=`echo $JSON | jshon -e $i -e id_str`
            RETWEETS=`echo $JSON | jshon -e $i -e retweet_count`
            FAVORITES=`echo $JSON | jshon -e $i -e favorite_count`
            TEXT=`echo $JSON | jshon -e $i -e text`
            if [ ! -z "$ID" -a ! -z "$CREATED_AT" ]; then
                echo "$ID,$CREATED_AT,$RETWEETS,$FAVORITES,$TEXT" >> $user.csv
            fi
    done
    echo $user
    vim -c "%s/ +0000 / /g | wq" $user.csv
done

