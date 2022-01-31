package Jena_project;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.*;

public class Interpreter {
	public CommandLine cmd;
	public Interpreter(CommandLine cmd) {
		this.cmd=cmd;
	}
	
	public void reportInvalid(InfModel infmodel) {
		ValidityReport validity = infmodel.validate();
		if (validity.isValid()) {
		} else {
		    System.out.println("\nConflicts");
		    for (Iterator<Report> i = validity.getReports(); i.hasNext(); ) {
		        ValidityReport.Report report = (ValidityReport.Report)i.next();
		        System.out.println(" - " + report);
		    }
		}
	}
	
	public Reasoner execInfOpt(String value) {
		Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(null);
		if(value == null) reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_DEFAULT);
		else {
			switch(value.toLowerCase()){
			case "simple"   : reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE); break;
			case "default"  : reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_DEFAULT);break;
			case "full"		: reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_FULL);   break;
			case "none"		: return null;
			default 		: reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_DEFAULT);break;
			}
		}
		return reasoner;
	}
	
	public void exec() {
		Model schema = RDFDataMgr.loadModel(cmd.getOptionValue('s'));
		Model data   = RDFDataMgr.loadModel(cmd.getOptionValue('r'));
		Reasoner reasoner = execInfOpt(cmd.getOptionValue('i'));
		
		InfModel infmodel = null;
		if(reasoner == null) {
			infmodel = ModelFactory.createRDFSModel(schema, data);
		}else {
			Reasoner boundReasoner = reasoner.bindSchema(schema);
			boundReasoner.setDerivationLogging(true);
			infmodel = ModelFactory.createInfModel(boundReasoner, data);
		}
		reportInvalid(infmodel);
		
		Model model = null;
		if(cmd.hasOption('n')) {
			model = getDerivationModel(infmodel); //ModelFactory.createRDFSModel(infmodel.getDeductionsModel(), data);
		}else {
			model = infmodel;
		}
		if(cmd.hasOption('p')) RDFDataMgr.write(System.out, model, execOutputLang(cmd.getOptionValue('f')));
		execQuery(model);
	}
	
	public Model getDerivationModel(InfModel infmodel) {
		ArrayList<Statement> deriv = new ArrayList<Statement>();
		for (StmtIterator i = infmodel.listStatements(); i.hasNext(); ) {
		    Statement s = i.nextStatement();
		    Iterator<Derivation> id = infmodel.getDerivation(s);
		    if(id.hasNext()) deriv.add(s);
		}
		return ModelFactory.createDefaultModel().add(deriv);
	}
	
	public void execQuery(Model model) {
		Dataset dataset = DatasetFactory.create() ;
		dataset.setDefaultModel(model) ;
		Query query = QueryFactory.read(cmd.getOptionValue('q'));
		if(query.isSelectType()) {
			execSelectType(query, dataset);
		}else if(query.isConstructType()) {
			execConstructType(query, dataset);
		}else if(query.isAskType()) {
			execAskType(query, dataset);
		}else if(query.isDescribeType()) {
			execDescribeType(query, dataset);
		}
	}
	private void execSelectType(Query query, Dataset dataset) {
		try(QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
			ResultSet results = qexec.execSelect() ;
		    execOut(results);
		    qexec.close() ;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void execOut(Object results) {
		try {
			if(cmd.hasOption('o')) {
				File outFile = new File(cmd.getOptionValue('o'));
				outFile.getParentFile().mkdirs();
				outFile.createNewFile();
			}
			if(results instanceof ResultSet) {
				ResultSet res = (ResultSet) results;
				ArrayList<String> outSet = new ArrayList<String>();
				for ( ; res.hasNext() ; ){
				    QuerySolution soln = res.nextSolution() ;
				    outSet.add(soln.toString() );
				}
				if(cmd.hasOption('o') && Commands.checkFile(cmd.getOptionValue('o'))) {
					FileWriter writer = new FileWriter(cmd.getOptionValue('o')); 
					for(String str: outSet) {
					  writer.write(str + System.lineSeparator());
					}
					writer.close();
				}else {
					outSet.forEach(System.out::println);
				}
			}else if(results instanceof Model) {
				Model res = (Model) results;
				if(cmd.hasOption('o') && Commands.checkFile(cmd.getOptionValue('o'))) {
					OutputStream out = new FileOutputStream(cmd.getOptionValue('o'));
					RDFDataMgr.write(out, res, execOutputLang(cmd.getOptionValue('f')));
					out.close();
				}else {
					RDFDataMgr.write(System.out, res, execOutputLang(cmd.getOptionValue('f'))) ;
				}
			}else if(results instanceof Boolean) {
				Boolean res = (Boolean) results;
				if(cmd.hasOption('o') && Commands.checkFile(cmd.getOptionValue('o'))) {
					FileWriter writer = new FileWriter(cmd.getOptionValue('o')); 
					writer.write(res.toString());
					writer.close();
				}else {
					System.out.println(res);
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Lang execOutputLang(String optionValue) {
		if(optionValue==null) return Lang.TURTLE;
		switch(optionValue.toLowerCase()) {
		case "ttl" 				: return Lang.TURTLE;
		case "nt"  				: return Lang.NT;
		case "rdf/xml" 			: return Lang.RDFXML;
		case "n3" 				: return Lang.N3;
		case "rdf/json" 		: return Lang.RDFJSON;
		case "json-ld" 			: return Lang.JSONLD;
		default : return Lang.TURTLE;
		}
	}

	private void execDescribeType(Query query, Dataset dataset) {
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
		Model resultModel = qexec.execDescribe() ;
		execOut(resultModel);
		qexec.close() ;
	}

	private void execAskType(Query query, Dataset dataset) {
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
		Boolean result = qexec.execAsk();
		execOut(result);
		qexec.close() ;
	}

	private void execConstructType(Query query, Dataset dataset) {
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
		Model resultModel = qexec.execConstruct() ;
		execOut(resultModel);
		qexec.close() ;
		
	}

}