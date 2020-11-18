#!/bin/bash
## Gets stock values for Tesla current day and appends them to stock csv
## Absolute file paths have been replaced with *path* and must be properly changed based on server environment for
## script to function and API key in Get request has been replaced with *key*

curl -X GET 'https://marketdata.websol.barchart.com/getQuote.csv?apikey=*key*=TSLA&fields=close%2Copen%2Chigh%2Clow' -o *path*/teslacurrent.csv
echo "date","close","volume","open","high","low" > /tmp/temp.txt
sed -n 2p *path*/teslacurrent.csv | awk -F "\"*,\"*" '{print $8","$15","$12","$13","$14}' | cut -c1-10,26- >> /tmp/temp.txt
tail -n +2 *path*/TeslaPrices.csv > *path*/temp.tmp && mv *path*/temp.tmp *path*/temp.txt
cat *path*/temp.txt >> /tmp/temp.txt
mv /tmp/temp.txt *path*/TeslaPrices.csv
sed -i 's+-+/+g' *path*/TeslaPrices.csv
rm *path*/temp.txt
