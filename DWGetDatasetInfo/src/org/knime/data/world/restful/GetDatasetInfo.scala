package org.knime.data.world.restful

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.client.utils.URIBuilder
import org.apache.http.util.EntityUtils
import java.net.URI
import javax.json.Json

class GetDatasetInfo {
  val uriPrebuild = new URIBuilder() setScheme("https") setHost("api.data.world")

  def request(username : String, dataset : String, apiKey : String) : String = {
    val uri = uriPrebuild setPath("/v0/datasets/" + username + "/" + dataset) build
    
    return getRestJsonContent(uri, apiKey)
  }

  def getRestJsonContent(url : URI, apiKey : String) : String = {
    val httpClient = HttpClients createDefault()
    val httpGet = new HttpGet(url)
    httpGet addHeader("Authorization", "Bearer " + apiKey)
    val httpResponse = httpClient execute(httpGet)
    var content = ""
    
    try {
      val entity = httpResponse getEntity()
      content = EntityUtils toString(entity)
      // TODO: val j = Json createReader(entity getContent)
      //       or this: val gson = new GsonBuilder() create
    } finally {
      httpResponse close()
    }

    return content
  }
}