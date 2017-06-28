package org.knime.data.world.restful

import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.FileEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import org.knime.data.world.prefs.DWPluginActivator

import com.google.gson.Gson

import java.io.File
import java.net.URI


case class PushResponse(message : String)


class PushUploadFile {
  val uriPrebuild = new URIBuilder() setScheme("https") setHost("api.data.world")

  val username : String = DWPluginActivator.getUsername
  val apiKey : String = DWPluginActivator.getAPIKey

  def pushSingleFile(dataset : String, filename : String) : (Int, PushResponse) = {
    val file = new File(filename)

    val uri = uriPrebuild setPath("/v0/uploads/" + username + "/" + dataset + "/files/" + file.getName) build
    
    return putRestFileUpload(uri, file)
  }

  def putRestFileUpload(url : URI, file : File) : (Int, PushResponse) = {
    val fileEntity = new FileEntity(file)
    
    // TODO: Update this if/when data.world changes this to use POST instead
    val httpPut = new HttpPut(url)
    val requestConfig = RequestConfig custom() setSocketTimeout(5000) setConnectTimeout(5000) build()
    httpPut setConfig(requestConfig)
    httpPut addHeader("Authorization", "Bearer " + apiKey)
    httpPut setEntity(fileEntity)

    val httpClient = HttpClients createDefault
    val httpResponse = httpClient execute(httpPut)
    
    val gson = new Gson
    var pushResponse : PushResponse = null
    var statusCode : Int = -1
    
    try {
      val responseEntity = httpResponse getEntity
      val content = EntityUtils toString(responseEntity)
      pushResponse = gson fromJson(content, classOf[PushResponse])
      statusCode = httpResponse getStatusLine() getStatusCode
    } finally {
      httpResponse close()
    }

    return (statusCode, pushResponse)
  }
}