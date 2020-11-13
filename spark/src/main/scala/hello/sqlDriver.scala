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
    getNasdaqDifferences("../nasdaq-data/AmazonNasdaqPrices.csv");
    getNasdaqDifferences("../nasdaq-data/AppleNasdaqPrices.csv");
    getNasdaqDifferences("../nasdaq-data/TeslaNasdaqPrices.csv");
    getNasdaqDifferences("../nasdaq-data/TwitterNasdaqPrices.csv");
  }
  def getNasdaqDifferences(infile: String) {
    val pattern = "../\\S+/(\\S+).csv".r
    var pattern(outfile) = infile;
    outfile += "_differences";
    val spark = SparkSession.builder()
      .appName("spark sql tryout")
      .master("local[6]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN");
    import spark.implicits._;
    val dateWindow = Window.orderBy("date");
    spark.read.option("header", "true").csv(infile)
      .withColumn("open_difference_daily",
        round($"open" - when(lag("open", 1).over(dateWindow).isNull, 0).otherwise(lag("open", 1).over(dateWindow)), 4))
      .withColumn("close_difference_daily",
        round($"close" - when(lag("close", 1).over(dateWindow).isNull, 0).otherwise(lag("close", 1).over(dateWindow)), 4))
      .withColumn("low_difference_daily",
        round($"low" - when(lag("low", 1).over(dateWindow).isNull, 0).otherwise(lag("low", 1).over(dateWindow)), 4))
      .withColumn("high_difference_daily",
        round($"high" - when(lag("high", 1).over(dateWindow).isNull, 0).otherwise(lag("high", 1).over(dateWindow)), 4))
      .withColumn("open_difference_weekly",
        round($"open" - when(lag("open", 7).over(dateWindow).isNull, 0).otherwise(lag("open", 7).over(dateWindow)), 4))
      .withColumn("close_difference_weekly",
        round($"close" - when(lag("close", 7).over(dateWindow).isNull, 0).otherwise(lag("close", 7).over(dateWindow)), 4))
      .withColumn("low_difference_weekly",
        round($"low" - when(lag("low", 7).over(dateWindow).isNull, 0).otherwise(lag("low", 7).over(dateWindow)), 4))
      .withColumn("high_difference_weekly",
        round($"high" - when(lag("high", 7).over(dateWindow).isNull, 0).otherwise(lag("high", 7).over(dateWindow)), 4))
      .select($"date", $"volume",
        $"open", $"open_difference_daily", $"open_difference_weekly",
        $"close", $"close_difference_daily", $"close_difference_weekly",
        $"low", $"low_difference_daily", $"low_difference_weekly",
        $"high", $"high_difference_daily", $"high_difference_weekly")
      .coalesce(1).write.option("header", "true").option("sep", ",").mode("overwrite").csv(outfile);
  }
}
