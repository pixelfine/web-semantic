/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Jena_project;

public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
    	/*
    	String[] arg = {"-r", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test1\\music.ttl", 
                		"-s", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test1\\music_schema.ttl",
                		"-q", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test1\\queryPerson.sparql",
                		"-i", "simple",
                		//"-o", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\output\\out",
                		"-n",
                		//"-f", "TTL",
        };
        */
    	// -r C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\youtube.ttl   -s C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\youtube_schema.ttl  -q C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\constructQuery.sparql -i simple -p 
    	/*
    	String[] arg = {
    			"-r", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\youtube.ttl", 
        		"-s", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\youtube_schema.ttl",
        		"-q", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\constructQuery.sparql",
        		"-i", "simple",
        		//"-p",
        		//"-o", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\output\\out",
        		//"-n",
        		//"-f", "TTL",
    	};
    	*/
    	/*
    	String[] arg = {
    			"-r", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test3\\artist.ttl", 
        		"-s", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test3\\artist_schema.ttl",
        		"-q", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test3\\selectQuery.sparql",
        		"-i", "simple",
        		//"-p",
        		//"-o", "C:\\Users\\Clinu\\OneDrive\\Bureau\\bds_3\\data\\test2\\output\\out",
        		//"-n",
        		//"-f", "TTL",
    	};
    	*/
    	Commands commands 	  = new Commands(args);
    	Interpreter interpret = new Interpreter(commands.parse());
    	interpret.exec();
    }
}
