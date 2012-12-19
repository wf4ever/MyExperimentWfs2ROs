MyExperimentWfs2ROs
===================

This is a repository created to store the software developed in showcase 87: Towards hundreds of myExperiment ROs.

The exporter is uploaded in a Java project, which consists on 5 classes:

	* Constants: different constants used in the project.
	
	* AnnotateRO: class with a method to annotate an RO with a prov:wasDerivedFrom annotation.
	
	* DeleteMyExperimentRO: class with a method to delete a single RO.
	
	* PostMyExperimentRO: class with a method to call the t2flow 2 RO transformation API. 
	
	* WorkflowsImporter: main class. It recovers the workflows from myExperiment migrating them first to the RODL and then annotating them.
	The annotation is a prov:wasDerivedFrom the t2flow file from which the RO was originally derived.
	The Id of the RO is created by taking the ID of the original myExperiment workflow: http://sandbox.wf4ever-project.org/rodl/ROs/myExpRO_"+workflow.getID()+"/
