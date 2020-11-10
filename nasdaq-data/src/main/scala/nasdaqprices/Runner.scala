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

        val splitRecords = sc.textFile(inputNasdaqData)
            .map(_.split(",").mkString)
            .take(10)
            .foreach(println)
    }




}
