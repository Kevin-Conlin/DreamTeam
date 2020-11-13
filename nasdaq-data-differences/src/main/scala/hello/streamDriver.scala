package hello

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.nio.file.{Files, Paths}

import org.apache.http.client.config.{CookieSpecs, RequestConfig}
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients

object streamDriver extends App {
  //get bearer token from environment vars
  val dirname = "twitterstream";
  val linesPerFile = 1000;
  val bearer = System.getenv("TWITTER_BEARER");
  //get standard cookie http client
  val httpClient = HttpClients.custom.setDefaultRequestConfig(RequestConfig.custom.setCookieSpec(CookieSpecs.STANDARD).build).build
  // build uri for twitter stream samples
  val uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/sample/stream")
  //  build the get request
  val httpGet = new HttpGet(uriBuilder.build)
  //  set the authorization token with bearer
  httpGet.setHeader("Authorization", String.format("Bearer %s", bearer))
  //  execute the get request
  val response = httpClient.execute(httpGet)
  // grab the entity object
  val entity = response.getEntity
  if (null != entity) {
    //create a reader
    val reader = new BufferedReader(new InputStreamReader(entity.getContent))
    //  get first line
    var line = reader.readLine
    // filewriter will get replaced on each line
    var fileWriter = new PrintWriter(Paths.get("tweetstream.tmp").toFile)
    var lineNumber = 1;
    val millis = System.currentTimeMillis
    while (line != null){
      if (lineNumber % linesPerFile == 0) {
        fileWriter.close()
        Files.move(
          Paths.get("tweetstream.tmp"),
          Paths.get(s"$dirname/tweetstream-$millis-${lineNumber/linesPerFile}"))
        fileWriter = new PrintWriter(Paths.get("tweetstream.tmp").toFile)
      }
      fileWriter.println(line)
      line = reader.readLine
      lineNumber += 1;
    }
  }
}
