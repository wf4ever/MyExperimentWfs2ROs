/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myexp2ropostrequests;

import com.sun.jersey.api.client.ClientResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 *
 * @author DGarijo
 */
public class AnnotateRO {
    public static void annotateRO(String t2flowURI, String roID){
      HttpClient client = new HttpClient();    
      BufferedReader br = null;
      
      PostMethod method = new PostMethod(roID);    
      method.addRequestHeader("token", Constants.authToken);
      method.addRequestHeader("Content-Type", "application/rdf+xml");
      method.addRequestHeader("Slug", Constants.slug);
      method.addRequestHeader("Link", "<"+roID+">; rel=\"http://purl.org/ao/annotatesResource\"");

      String body = "<?xml version=\"1.0\"?>"+
                    "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:prov=\"http://www.w3.org/ns/prov#\">"+
                    "<rdf:Description rdf:about=\""+roID+"\">"+
                    "<prov:wasDerivedFrom rdf:resource=\""+t2flowURI+"\" />"+
                    "</rdf:Description>"+
                    "</rdf:RDF>";
      method.setRequestEntity(new StringRequestEntity(body));      
      
      
      try{
          int returnCode = client.executeMethod(method);

          if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");            
                method.getResponseBodyAsString();
          }else{
              br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
              String readLine;        
              while(((readLine = br.readLine()) != null)) {
                  System.err.println(readLine);
              }
              System.out.println(method.getStatusText());
              System.out.println("Link :"+method.getResponseHeader("Link").getValue());
          }
      } catch (Exception e) {
          System.err.println(e);
      } finally {
          method.releaseConnection();
          if(br != null) try { br.close(); } catch (Exception fe) {}
      }
  }
//    public static void main(String[] args){
//        AnnotateRO.annotateRO("https://raw.github.com/wf4ever/provenance-corpus/master/Taverna_repository/workflow_2228_version_1/amiga_conesearch_from_a_file_of_targets_positions_268018.t2flow", "http://sandbox.wf4ever-project.org/rodl/ROs/Sample2/");
//    }
    
}