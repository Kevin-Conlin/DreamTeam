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

        //remove comma from .csv and format
        val splitRecords = sc.textFile(inputNasdaqData)
            .map(_.split(","))
        val orderFields = splitRecords.map(arr => (arr(0), arr(3), arr(4), arr(5), arr(1)))

            orderFields.take(10).foreach(println)

    }




}
