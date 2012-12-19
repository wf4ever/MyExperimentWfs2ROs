package myexp2ropostrequests;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

public class PostMyExperimentToRO {
    
  public static void postROToRODLFromMyExperimentID(String t2flowURI, String roID) throws InterruptedException{
      HttpClient client = new HttpClient();    
      BufferedReader br = null;
      
      PostMethod method = new PostMethod(Constants.apiURL);    
      method.addParameter("resource", t2flowURI);//"https://raw.github.com/wf4ever/provenance-corpus/master/Taverna_repository/workflow_2228_version_1/amiga_conesearch_from_a_file_of_targets_positions_268018.t2flow");
      method.addParameter("format", Constants.format);//"application/vnd.taverna.t2flow+xml"
      method.addParameter("ro", roID);//"http://sandbox.wf4ever-project.org/rodl/ROs/Sample2/"
      method.addParameter("token", Constants.authToken);//"541002e2-9ff9-4cff-b85c-2b4af2c33e98"
      method.addRequestHeader("Content-Type", Constants.contentType_URLencoded);//"application/x-www-form-urlencoded"

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
              String status = method.getResponseHeader("Location").getValue();
              
              System.out.println("Job uri: "+status);
              
              //wait for the response until the job is done              
              System.out.print("Waiting for the job to finish (ping every 2 seconds...\n");
              int retries = 0;
              while (checkJob(status).equals("RUNNING")){
                  retries++;
                  System.out.print(retries+"... ");
                  Thread.sleep(2000);
              }
              System.out.println("\nJob finished");
          }
      } catch (Exception e) {
          System.err.println(e);
      } finally {
          method.releaseConnection();
          if(br != null) try { br.close(); } catch (Exception fe) {}
      }   
      
  }
  
  private static String checkJob(String jobURI){
      HttpClient client = new HttpClient();    
      GetMethod method = new GetMethod(jobURI);//http://sandbox.wf4ever-project.org/wf-ro/jobs/cc7bfba9-d527-4815-a537-a45714a633cf");       

      try{
          int returnCode = client.executeMethod(method);

          if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Get method is not implemented by this URI");            
                method.getResponseBodyAsString();
          }else{
              InputStream is = method.getResponseBodyAsStream();
              String s = IOUtils.toString( is );
              JSONObject json = (JSONObject) JSONSerializer.toJSON(s); 
              return (String)json.get("status");              
          }
      } catch (Exception e) {
          System.err.println(e);
          return "FAIL";
      } finally {
          method.releaseConnection();
      }
      return "FAIL";
  }
  //RUNNING
  //for local tests
//  public static void main(String []args) throws InterruptedException{
//      //postROToRODLFromMyExperimentID("https://raw.github.com/wf4ever/provenance-corpus/master/Taverna_repository/workflow_2228_version_1/amiga_conesearch_from_a_file_of_targets_positions_268018.t2flow", "http://sandbox.wf4ever-project.org/rodl/ROs/Sample3/");
//      postROToRODLFromMyExperimentID("http://www.myexperiment.org/workflows/1977/download/pathways_and_gene_annotations_forqtl_region_900605.t2flow", "http://sandbox.wf4ever-project.org/rodl/ROs/Sample2/");
////      System.out.println(checkJob("a"));
//      
//  }


}
  