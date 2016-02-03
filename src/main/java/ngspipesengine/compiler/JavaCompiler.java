package ngspipesengine.compiler;

import java.net.MalformedURLException;

import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.IO;

public class JavaCompiler extends PipelineCompiler {

	private static final String MAIN_CODE = "\tpublic static void main(String[] args) throws DSLException {\n" +
			"\t\tif(args[0].isEmpty() || args.length == 0)\n" + 
			"\t\t\tthrow new DSLException(\":: Error running pipeline :: no " +
			"repository uri especified.\");\n" +
			"\t\ttry {\n\t\t\tPipe pipe = loadPipeline(args[0]);" +
			"\n\t\t\tif(args.length == 2)\n\t\t\t\tpipe.run(Integer.parseInt(args[1]));" +
			"\n\t\t\t\telse\n\t\t\t\t\tif(args.length == 3)\n\t\t\t\t\t\t" +
			"pipe.run(Integer.parseInt(args[1]), Integer.parseInt(args[2]));" +
			"\n\t\t\t\t\telse\n\t\t\t\t\t\tpipe.run();\n" +
			"\t\t} catch(DSLException e) {\n" +
			"\t\t\tthrow new DSLException(\":: Error running pipeline :: \" + " + 
			"e.getMessage(), e);\n\t\t}\n\t}\n}";


	public JavaCompiler(CompilerProperties props) {
		super(props);
	}
	

	@Override
	protected String getExecutableJavaClass() throws MalformedURLException, EngineException {

		String classContent = IO.readFile(props.getPipelinePath());
		setRepositoryInfo(classContent);
		classContent = setClassName(classContent);
		classContent = classContent.substring(0, classContent.lastIndexOf("}"));
		
		StringBuilder executableClassContent = new StringBuilder(classContent);
		executableClassContent.append("\n").append(MAIN_CODE);
		
		return executableClassContent.toString();
	}
	

	private String setClassName(String classContent) {
		int idxBegin = classContent.indexOf("class ") + "class ".length();
		int indexEnd = classContent.indexOf("{");
		
		String fileClassName = classContent.substring(idxBegin, indexEnd);
		
		return classContent.replace(fileClassName, getName() + " ");
	}


	private void setRepositoryInfo(String conent) throws EngineException {
		int beginIdx = conent.indexOf("new Pipeline(") + "new Pipeline(".length();
		int endIdx = conent.indexOf(",", beginIdx);

		String repoType = conent.substring(beginIdx, endIdx);
		repoType.trim();
		repoType = repoType.replace("\"", "");		
		props.setRepositoryType(repoType);


		beginIdx = conent.indexOf("repositoryUri =") + "repositoryUri = ".length();
		endIdx = conent.indexOf(";", beginIdx);
		
		String repositoryUri = conent.substring(beginIdx, endIdx);
		repositoryUri.trim();
		repositoryUri = repositoryUri.replace("\"", "");
		props.setRepositoryUri(repositoryUri);
	}
}