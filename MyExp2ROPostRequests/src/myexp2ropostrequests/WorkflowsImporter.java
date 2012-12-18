package myexp2ropostrequests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import wrappers.myexperiment.WorkflowWrapper;
import epnoi.model.Workflow;

public class WorkflowsImporter {

	private List<Workflow> workflows;

	public WorkflowsImporter() {

		this.workflows = new ArrayList<Workflow>();
	}

	public static void main(String[] args) {
		System.out.println("Starting the workflows import process");
//		Workflow w = WorkflowWrapper.extractWorkflow("http://www.myexperiment.org/workflow.xml?id=1977");
//                System.out.println("Content URI: "+w.getContentURI());
//                System.out.println("Type: "+w.getContentType());
//                System.out.println("ID :"+w.getID());
//                System.out.println("URI :"+w.getURI());
//                System.out.println("Resource: "+w.getResource());
//                System.out.println("Title "+w.getTitle());
              //Isolated test
//              PostMyExperimentToRO.postROToRODLFromMyExperimentID(w.getContentURI(), "http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO-"+w.getID()+"/");
//              AnnotateRO.annotateRO(w.getContentURI(), "http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO-"+w.getID()+"/");
                String currentWorkflow ="";
            try{                    
              WorkflowsImporter workflowsImporter = new WorkflowsImporter();                
		workflowsImporter.extractWorkflows();
		int workflowsCount = 0;
                
                //workflow extraction
                List<Workflow> list = workflowsImporter.getWorkflows();
                
                //Delete previous ROs (in case there are malformed ones)
                for (Workflow workflow : list) {
                    try{
                        currentWorkflow = workflow.getURI();
                        if ("application/vnd.taverna.t2flow+xml".equals(workflow
                                        .getContentType())) {
                                System.out.println("Deleting: http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO_"+workflow.getID()+"/");
                                DeleteMyExperimentRO.deleteRO("http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO_"+workflow.getID()+"/");
                                Thread.sleep(2000);//to leave the server time to clean up resources between creation of ROs.
                        }
                    }catch(Exception e){
                        System.out.println("Exception while creating the workflow "+currentWorkflow+" Exception: "+e.getMessage());
                    }
		}
                
                //RO creation
		for (Workflow workflow : list) {
                    try{
                        currentWorkflow = workflow.getURI();
                        if ("application/vnd.taverna.t2flow+xml".equals(workflow
                                        .getContentType())) {
                                workflowsCount++;
                                PostMyExperimentToRO.postROToRODLFromMyExperimentID(workflow.getContentURI(), "http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO_"+workflow.getID()+"/");
                                Thread.sleep(3000);//to leave the server time to clean up resources between creation of ROs.
                        }
                    }catch(Exception e){
                        System.out.println("Exception while creating the workflow "+currentWorkflow+" Exception: "+e.getMessage());
                    }
		}
                //RO annotation. We have to do it separately to wait until the ROs have been created.
                for(Workflow workflow :list){
                    try{
                        currentWorkflow = workflow.getURI();
                        if ("application/vnd.taverna.t2flow+xml".equals(workflow.getContentType())) {                                
                                AnnotateRO.annotateRO(workflow.getContentURI(), "http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO_"+workflow.getID()+"/");
                                Thread.sleep(1000);//to leave the server time to clean up resources between annotations.
                        }
                        
                    }catch(Exception e){
                        System.out.println("Exception while annotating the workflow "+currentWorkflow+" Exception: "+e.getMessage());
                    }
                }
		System.out.println("The total number of t2flow workflows is " + workflowsCount);
		System.out.println("Finishing the workflow import process");
                
            }catch(Exception e){
                System.out.println("Exception in the migration process. Exception: "+e.getMessage());
            }
	}

	public void extractWorkflows() {

		String workflowsQueryService = "http://rdf.myexperiment.org/sparql?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0Aselect+distinct+%3Fx+where+%7B%3Fx+rdf%3Atype+%3Chttp%3A%2F%2Frdf.myexperiment.org%2Fontologies%2Fcontributions%2FWorkflow%3E%7D+&formatting=XML&softlimit=5";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(workflowsQueryService);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList nodeList = null;
		try {
			nodeList = doc.getElementsByTagName("uri");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int s = 0; s < nodeList.getLength(); s++) {
			Node fstNode = nodeList.item(s);
			Element firstUserElement = (Element) fstNode;

			String workflowResource = firstUserElement.getTextContent();
			int indexOfWorkflows = workflowResource.indexOf("/workflows/");

			String workflowID = workflowResource.substring(
					indexOfWorkflows + 11, workflowResource.length());

			String workflowURI = "http://www.myexperiment.org/workflow.xml?id="
					+ workflowID;
			Workflow workflow = WorkflowWrapper.extractWorkflow(workflowURI);
			this.workflows.add(workflow);
		}

	}

	public List<Workflow> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(List<Workflow> workflows) {
		this.workflows = workflows;
	}
}
