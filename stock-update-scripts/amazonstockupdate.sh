#!/bin/bash
## Gets stock values for Amazon current day and appends them to stock csv
## Absolute file paths have been replaced with *path* and must be properly changed based on server environment for
## script to function and API key in Get request has been replaced with *key*


curl -X GET 'https://marketdata.websol.barchart.com/getQuote.csv?apikey=*key*&symbols=AMZN&fields=close%2Copen%2Chigh%2Clow' -o *path*/amazoncurrent.csv
echo "date","close","volume","open","high","low" > /tmp/temp.txt
sed -n 2p *path*/amazoncurrent.csv | awk -F "\"*,\"*" '{print $8","$15","$12","$13","$14}' | cut -c1-10,26- >> /tmp/temp.txt
tail -n +2 *path*/AmazonPrices.csv > *path*/temp.tmp && mv *path*/temp.tmp *path*/temp.txt
cat *path*/bezos/temp.txt >> /tmp/temp.txt
mv /tmp/temp.txt *path*/AmazonPrices.csv
sed -i 's+-+/+g' *path*/AmazonPrices.csv
rm *path*/temp.txt
