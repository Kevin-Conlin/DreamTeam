package nasdaqprices
import org.apache.spark.{SparkConf, SparkContext}

object Runner {
    def main (args: Array[String]) = {
        // Name of the spark Job
        val appName = "Nasdaq Prices"

        // Input, set master and spark context:
        val conf = new SparkConf().setAppName(appName).setMaster("local[4]")
        val sc = new SparkContext(conf)
        val inputNasdaqData = "TeslaNasdaqPrices.csv"
        val inputJSONData = "out.csv"

        formatNasdaq(sc, inputNasdaqData)

        def formatNasdaq(context: SparkContext, file: String) = {
            //format .csv with desirable order and remove "volume" column
            val splitRecords = sc.textFile(file)
                .map(_.split(","))
            val orderFields = splitRecords
                .map(arr => (arr(0), arr(3), arr(4), arr(5), arr(1)))

            //print 10 formatted and ordered Nasdaq prices
            orderFields.take(10).foreach(println)


            // Print 10 formatted Tweets data
            val splitJSON = sc.textFile(inputJSONData)
                .map(_.split(",").mkString)
            //testing
            //val orderJSONFields = splitJSON
            //    .map(arr => (arr(0), arr(3), arr(4), arr(5), arr(1)))

            splitJSON.take(10).foreach(println)
        }
    }




}
