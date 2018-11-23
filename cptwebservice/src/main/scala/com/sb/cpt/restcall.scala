package com.sb.cpt

import java.io.{ BufferedReader, IOException, InputStreamReader }
import java.net.SocketTimeoutException
import java.util.ArrayList
 
import org.apache.http.NameValuePair
import org.apache.http.auth.AuthenticationException
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{ CloseableHttpResponse, HttpPost }
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair

//case class ReqBody(AuthenticationSecret: String, IdStart: Integer, MaxRows: Integer, RequestId: String)

object restcall extends {
  def main(args: Array[String]) = {

    // simulate a json string
    //val jsonString = """{"AuthenticationSecret": "asdfkjkj","IdStart": 1,"MaxRows": 100,"RequestId": "sdgasgawwevq234wsv"}"""
    //val jsonString2 = """{ title: 'footballwa', body: 'bargharwa', userId: 111}"""
    // val url2 = "https://jsonplaceholder.typicode.com/posts"

    val url = args(0)
    val jsonString3 = args(1)

    val nameValuePairs = new ArrayList[NameValuePair]()
    nameValuePairs.add(new BasicNameValuePair("JSON", jsonString3))

    val client = HttpClients.createDefault()
    val post = new HttpPost(url)
    val reqConfig = RequestConfig.custom()
      .setConnectTimeout(500)
      .setConnectionRequestTimeout(500)
      .setSocketTimeout(500)
      .build()
    post.setConfig(reqConfig)
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs))

    //val abc = new DefaultHttpMethodRetryHandler(3, false)
    //post1.setParams(HttpMethodParams.RETRY_HANDLER)

    var response: CloseableHttpResponse = null

    try {
      response = client.execute(post)
      val responseStatus = response.getStatusLine.getStatusCode
      if (responseStatus >= 200 && responseStatus < 300) {
        val read = new BufferedReader(new InputStreamReader(response.getEntity.getContent))
        read.lines().forEach(println)
      } else println("Undesired response status: " + responseStatus, response.getStatusLine.getReasonPhrase)

    } catch {
      case conn: ConnectTimeoutException     => println("**Connection time out error.    " + conn.printStackTrace())
      case timeout: AuthenticationException  => println("**Authentication failed. Check the authentication key.   " + timeout.printStackTrace())
      case socketerr: SocketTimeoutException => println("**Socket time out." + socketerr.printStackTrace())
      case io: IOException                   => println("**IO exception.    " + io.printStackTrace())
      case _: Throwable                      => println("**Exception.")
    } finally {
      try {
        response.close()
      } catch {
        case io: IOException          => println("**IO Exception while closing the response.    " + io.printStackTrace())
        case np: NullPointerException => println("**Null pointer Exception while closing the response.    " + np.printStackTrace())
        case _: Throwable             => println("**Exception while closing the response.")
      }
    }
  }
}