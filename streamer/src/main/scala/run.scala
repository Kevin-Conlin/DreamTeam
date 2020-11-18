import java.io.FileWriter

import twitter4j._


object run {

  val elonmusk = 44196397L;
  val jeffbezos = 15506669L;
  val jack = 12L;
  val lloydblankfein = 313381067L;
  val tim_cook = 1636590253L;
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(System.getenv( "TWITTER_CONSUMER_KEY"))
    .setOAuthConsumerSecret( System.getenv("TWITTER_CONSUMER_SECRET"))
    .setOAuthAccessToken( System.getenv( "TWITTER_ACCESS_TOKEN"))
    .setOAuthAccessTokenSecret(System.getenv( "TWITTER_ACCESS_TOKEN_SECRET"))
    .build

  def main(args: Array[String]) {
    val twitterStream = new TwitterStreamFactory(config).getInstance
    val query = new FilterQuery()
    query.follow( elonmusk, jeffbezos, jack, lloydblankfein, tim_cook)
    twitterStream.addListener(simpleStatusListener)
    twitterStream.filter(query)
    while(true){};
    twitterStream.cleanUp
    twitterStream.shutdown
  }

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) {
      status.getUser.getId match {
        case 44196397L => printToFile(status,"musk/elonmusk.tsv")
        case 15506669L => printToFile(status, "bezos/jeffbezos.tsv")
        case 12L => printToFile(status,"dorsey/jack.tsv")
        case 313381067L => printToFile(status,"blankfein/lloydblankfein.tsv")
        case 1636590253L => printToFile(status,"cook/tim_cook.tsv")
        case _ => {}
      }
    }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
    def printToFile(status: Status, file: String) {
      println(s"${status.getId}\t${status.getCreatedAt}\t${status.getRetweetCount}\t${status.getFavoriteCount}\t${status.getText}")
      val fw = new FileWriter(file, true)
      fw.write(s"${status.getId}\t${status.getCreatedAt}\t${status.getRetweetCount}\t${status.getFavoriteCount}\t${status.getText}\n")
      fw.close()
      println("===========");
    }

  }
}

