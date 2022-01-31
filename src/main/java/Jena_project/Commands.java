package Jena_project;
import java.io.File;
import java.util.Scanner;
import org.apache.commons.cli.*;

public class Commands {
	public String args[];
	private Options options = new Options();
	
	public Commands(String [] args) {
		if(args.length == 0) {
			System.out.println("Your args :");
			Scanner sc = new Scanner(System.in).useDelimiter("\\n");
			this.args = sc.next().split("\\s+");
			sc.close();
		}else {
			this.args = args;
		}
		addOptions();
	}
	
	private void addOptions() {
		Option rdf		= new Option("r", "RDF", true, "RDF file path");
        rdf.setRequired(true);
        options.addOption(rdf);

        Option rdfs 	= new Option("s", "RDFS", true, "RDFS file path");
        rdfs.setRequired(true);
        options.addOption(rdfs);
        
        Option query 	= new Option("q", "QUERY", true, "SPARQL file path");
        query.setRequired(true);
        options.addOption(query);
        
        Option inference= new Option("i", "INFERENCE", true, "Inference type : full, default, simple, none");
        options.addOption(inference);
        
        Option format 	= new Option("f", "FORMAT", true, "Format type :TTL, NT, RDF/XML, N3, RDF/JSON, JSON-LD, RDF/XML-ABBREV");
        options.addOption(format);
        
        Option newfacts = new Option("n", "NEWFACTS", false, "Evaluate only infered data");
        options.addOption(newfacts);
        
        Option out 		= new Option("o", "OUT", true, "file path to write the output");
        options.addOption(out);
        
        Option model 		= new Option("p", "PRINTMODEL", false, "Print the model before querying");
        options.addOption(model);
        
	}
	
	public CommandLine parse() {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("The following lines represents all the options that is supported.\n\tOnly [\'-r\', \'-s\', \'-q\'] options are mandatory.", options);
            System.exit(0);
        }
        parseFilePath(cmd);
        return cmd;
	}
	
	public static boolean checkFile(String s){
		if (new File(s).isFile()) {
			return true;
		}else {
			System.out.println("invalid path :"+s);
			return false;
		}
	}
	
	private void parseFilePath(CommandLine cmd) {
		String rdfFile = cmd.getOptionValue("RDF");
		String rdfsFile = cmd.getOptionValue("RDFS");
		String sparqlFile = cmd.getOptionValue("QUERY");
		if (!(Commands.checkFile(rdfFile) && Commands.checkFile(rdfsFile) && Commands.checkFile(sparqlFile))){
        	System.exit(0);
        }
	}
}
