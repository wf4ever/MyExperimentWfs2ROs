/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myexp2ropostrequests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author DGarijo
 */
public class DeleteMyExperimentRO {
    public static void deleteRO(String roID){
          HttpClient client = new HttpClient();    
          BufferedReader br = null;

          DeleteMethod method = new DeleteMethod(roID);//"http://sandbox.wf4ever-project.org/ROs/"+           
          method.addRequestHeader("token", Constants.authToken);
          try{
          int returnCode = client.executeMethod(method);

          if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Delete method is not implemented by this URI");            
                method.getResponseBodyAsString();
          }          
      } catch (Exception e) {
          System.err.println(e);
      } finally {
          method.releaseConnection();
          if(br != null) try { br.close(); } catch (Exception fe) {}
      }
        
    }
//    public static void main(String[] args){
//        DeleteMyExperimentRO.deleteRO("http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO-1977/");
////        DeleteMyExperimentRO.deleteRO("http://sandbox.wf4ever-project.org/rodl/ROs/Sample/");
////        DeleteMyExperimentRO.deleteRO("http://sandbox.wf4ever-project.org/rodl/ROs/Sample3/");
//    }
    
}
