package nasdaqprices
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, functions}


object Runner {
    def main (args: Array[String]) = {
        // Name of the spark Job
        val appName = "Nasdaq Prices"

        // Input, set master and spark context:
        val conf = new SparkConf().setAppName(appName).setMaster("local[4]")
        val sc = new SparkContext(conf)

        //testing Spark SQL
        val spark = SparkSession
            .builder()
            .appName("Nasdaq Prices + Tweets")
            .config(conf)
            .getOrCreate()

        val inputNasdaqData = "TeslaNasdaqPrices.csv"
        val inputJSONData = "json.json"
        val inputCSVConvData = "out.csv"

        formatNasdaq(sc, inputNasdaqData)

        def formatNasdaq(context: SparkContext, file: String) = {
            //format .csv with desirable order and remove "volume" column
            val splitRecords = sc.textFile(file)
                .map(x => x.split(","))
            val orderFields = splitRecords
                .map(arr => (arr(0), arr(3), arr(4), arr(5), arr(1)))

            orderFields.take(10).foreach(println)


            // Print 10 formatted Tweets data
            val splitJSON = sc.textFile(inputCSVConvData)
                .map(x => x.split(",").mkString)

            splitJSON.take(10).foreach(println)

            // below continues Spark SQL/Dataframe testing
            // prints dataframe of .csv file in a Spark SQL table
            import spark.implicits._
            val df = spark.read.json(inputJSONData)
            //df.select("created_at", "retweet_count", "favorite_count").show()
            df.createOrReplaceTempView("tweets")
            val df2 = spark.sql(
                "SELECT unix_timestamp(created_at) AS Time, retweet_count AS Retweets, favorite_count AS Favorites, " +
                "ROUND(((retweet_count / favorite_count)*100),2) AS Percentage FROM tweets")

            df2.show()

            //testing for date conversion
            df2.select(functions.map(
        }
    }

}

E