package TweetStockTables

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._



object Runner {
    def main (args: Array[String]) = {
        // Name of the spark Job
        val appName = "Tweets vs Stock Prices"

        //Sets master and spark context:
        val conf = new SparkConf().setAppName(appName).setMaster("local[4]")
        val sc = new SparkContext(conf)

        //Creates SparkSession
        val spark = SparkSession
            .builder()
            .appName("Nasdaq Prices + Tweets")
            .config(conf)
            .getOrCreate()

        //CSV files used
        val inputNasdaqData = "../nasdaq-data/TeslaPrices.csv"
        val inputTweetData = "ElonSpecTweets.csv"

        tweetStockTable(sc, inputNasdaqData, inputTweetData)

        def tweetStockTable(context: SparkContext, stockData: String, tweetData: String) = {

          import spark.implicits._

          //Creates tweet table view from input csv
          val df = spark.read.csv(tweetData)
          df.createOrReplaceTempView("tweets")

          //Translates Twitter data timestamp to appropriate format
          //for joining with Nasdaq data
          spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")
          val df2 = spark.sql("SELECT _c0 AS id, (from_unixtime(unix_timestamp(_c1, 'yyyy-MM-dd'), " +
            "'yyyy/MM/dd')) AS date, _c2 AS retweets, _c3 AS favorites, _c4 AS text FROM tweets")
          df2.createOrReplaceTempView("clean_tweets")

          //Creates stock table view with columns containing percentages of change for stock prices by day and week
          val dateWindow = Window.orderBy("date");
          val df3 = spark.read.option("header", "true").csv(stockData)
              .withColumn("close_difference_daily",
              round(($"close" - when( lag("close", 1 ).over(dateWindow).isNull, 0).otherwise(lag("close", 1).over(dateWindow)))/(when( lag("close", 1 ).over(dateWindow).isNull, 0).otherwise(lag("close", 1).over(dateWindow))) * 100, 4) )
            .withColumn("close_difference_next_day",
              round(($"close" - when( lead("close", 1 ).over(dateWindow).isNull, 0).otherwise(lead("close", 1).over(dateWindow)))/$"close" * 100, 4) )
               .withColumn("close_difference_weekly",
              round(($"close" - when( lag("close", 7 ).over(dateWindow).isNull, 0).otherwise(lag("close", 7).over(dateWindow)))/(when( lag("close", 7 ).over(dateWindow).isNull, 0).otherwise(lag("close", 7).over(dateWindow))) * 100, 4) )
            .withColumn("close_difference_next_week",
              round(($"close" - when( lead("close", 7 ).over(dateWindow).isNull, 0).otherwise(lead("close", 7).over(dateWindow)))/$"close" * 100, 4) )
            .select($"date", $"volume", $"close",
              $"close_difference_daily", $"close_difference_next_day",
              $"close_difference_weekly", $"close_difference_next_week")
          df3.createOrReplaceTempView("stock")

          //Joins tweet and stock views to create a DataFrame containing relevant columns
          val df4 = spark.sql("SELECT OUTER.date AS Date, OUTER.text AS Text, " +
            "ROUND(stock.close, 2) AS Closing_Price, stock.close_difference_daily AS Percent_Change_Prev_Day, " +
            "stock.close_difference_weekly AS Percent_Change_Prev_Week,  stock.close_difference_next_day AS Percent_Change_Following_Day, " +
            "stock.close_difference_next_week AS Percent_Change_Following_Week, " + "" +
            "ROUND((OUTER.favorites/OUTER.retweets), 2) AS Fave_to_Retweet_Ratio  " +
            "FROM clean_tweets OUTER LEFT JOIN stock ON OUTER.date = stock.date ORDER BY Fave_to_Retweet_Ratio")

//          val df4 = spark.sql("SELECT date AS Date, id AS ID, " +
//            "ROUND((favorites/retweets), 2) AS Fave_to_Retweet_Ratio " +
//            "FROM clean_tweets ORDER BY date ASC")
            df4.show(true)

        }
    }
}
