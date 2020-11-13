package hello

import org.apache.spark.sql._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._

/**
 * requires the Nasdaq daily reports for the past 5 years from the nasdaq-data directory
 * creates and appends columns for the difference in value from 1 and 7 days previous
 * creates a column for the difference in opening, closing, lowest, and highest value
 */
object sqlDriver {
  def main(args: Array[String]) {
    getNasdaqDifferences("../nasdaq-data/AmazonPrices.csv");
    getNasdaqDifferences("../nasdaq-data/ApplePrices.csv");
    getNasdaqDifferences("../nasdaq-data/TeslaPrices.csv");
    getNasdaqDifferences("../nasdaq-data/TwitterPrices.csv");
  }
  def getNasdaqDifferences(infile: String) {
    val pattern = "../\\S+/(\\S+).csv".r
    var pattern(outfile) = infile;
    outfile += "_differences";
    val spark = SparkSession.builder()
      .appName("spark sql tryout")
      .master("local[*]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN");
    import spark.implicits._;
    val dateWindow = Window.orderBy("date");
    spark.read.option("header", "true").csv(infile)
      //  daily differences
      .withColumn("open_difference_daily",
        round($"open" - when(lag("open", 1).over(dateWindow).isNull, 0).otherwise(lag("open", 1).over(dateWindow)), 4)
      )
      .withColumn("open_difference_daily_percentage",
        round({
          val lagval = when(lag("open", 5).over(dateWindow).isNull, 0).otherwise(lag("open", 1).over(dateWindow));
          (($"open" - lagval)/lagval)*100
        }, 4)
      )
      .withColumn("close_difference_daily",
        round($"close" - when(lag("close", 1).over(dateWindow).isNull, 0).otherwise(lag("close", 1).over(dateWindow)), 4)
      )
      .withColumn("close_difference_daily_percentage",
        round({
          val lagval = when(lag("close", 1).over(dateWindow).isNull, 0).otherwise(lag("close", 1).over(dateWindow));
          (($"close" - lagval)/lagval)*100
        }, 4)
      )
      .withColumn("low_difference_daily",
        round($"low" - when(lag("low", 1).over(dateWindow).isNull, 0).otherwise(lag("low", 1).over(dateWindow)), 4)
      )
      .withColumn("low_difference_daily_percentage",
        round({
          val lagval = when(lag("low", 1).over(dateWindow).isNull, 0).otherwise(lag("low", 1).over(dateWindow));
          (($"low" - lagval)/lagval)*100
        }, 4)
      )
      .withColumn("high_difference_daily",
        round($"high" - when(lag("high", 1).over(dateWindow).isNull, 0).otherwise(lag("high", 1).over(dateWindow)), 4)
      )
      .withColumn("high_difference_daily_percentage",
        round({
          val lagval = when(lag("high", 1).over(dateWindow).isNull, 0).otherwise(lag("high", 1).over(dateWindow));
          (($"high" - lagval)/lagval)*100
        }, 4)
      )
      //  weekly differences
      .withColumn("open_difference_weekly",
        round($"open" - when(lag("open", 5).over(dateWindow).isNull, 0).otherwise(lag("open", 5).over(dateWindow)), 4)
      )
      .withColumn("open_difference_weekly_percentage",
        round({
          val lagval = when(lag("open", 5).over(dateWindow).isNull, 0).otherwise(lag("open", 5).over(dateWindow));
          (($"open" - lagval)/lagval)*100
        }, 4)
      )
      .withColumn("close_difference_weekly",
        round($"close" - when(lag("close", 5).over(dateWindow).isNull, 0).otherwise(lag("close", 5).over(dateWindow)), 4)
      )
      .withColumn("close_difference_weekly_percentage",
        round({
          val lagval = when(lag("close", 5).over(dateWindow).isNull, 0).otherwise(lag("close", 5).over(dateWindow));
          (($"close" - lagval)/lagval)*100
        }, 4)
      )
      .withColumn("low_difference_weekly",
        round($"low" - when(lag("low", 5).over(dateWindow).isNull, 0).otherwise(lag("low", 5).over(dateWindow)), 4)
      )
      .withColumn("low_difference_weekly_percentage",
        round({
          val lagval = when(lag("low", 5).over(dateWindow).isNull, 0).otherwise(lag("low", 5).over(dateWindow));
          (($"low" - lagval)/lagval)*100
        }, 4)
      )
      .withColumn("high_difference_weekly",
        round($"high" - when(lag("high", 5).over(dateWindow).isNull, 0).otherwise(lag("high", 5).over(dateWindow)), 4)
      )
      .withColumn("high_difference_weekly_percentage",
           round({
             val lagval = when(lag("high", 5).over(dateWindow).isNull, 0).otherwise(lag("high", 5).over(dateWindow));
             (($"high" - lagval)/lagval)*100
            }, 4)
      )

      .select($"date", $"volume",
        $"open", $"open_difference_daily", $"open_difference_daily_percentage",
        $"open_difference_weekly", $"open_difference_weekly_percentage",
        $"close", $"close_difference_daily", $"close_difference_daily_percentage",
        $"close_difference_weekly", $"close_difference_weekly_percentage",
        $"low", $"low_difference_daily", $"low_difference_daily_percentage",
        $"low_difference_weekly", $"low_difference_weekly_percentage",
        $"high", $"high_difference_daily", $"high_difference_daily_percentage",
        $"high_difference_weekly", $"high_difference_weekly_percentage")
      //.coalesce(1).write.option("header", "true").option("sep", ",").mode("overwrite").csv(outfile);
      .show(10)
  }
}
