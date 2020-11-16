arr="Amazon Apple Tesla Twitter"

for i in $arr;
do
    mv ${i}Prices_differences/part* ./${i}Prices_differences.csv
    rm -r ${i}Prices_differences/
done
