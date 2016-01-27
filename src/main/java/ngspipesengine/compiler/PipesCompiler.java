package ngspipesengine.compiler;

import java.net.MalformedURLException;

import ngspipesengine.exceptions.EngineException;
import ngspipesengine.parsers.java.PipesJavaParser;
import ngspipesengine.utils.IO;

public class PipesCompiler extends PipelineCompiler {

	private static final String PIPES_BASE_PATH = "resources/";

	public PipesCompiler(CompilerProperties props) {
		super(props);
	}


	@Override
	protected String getExecutableJavaClass() throws MalformedURLException, EngineException {
		
		String pipesName = getName();
		StringBuilder pipeClassContent = PipesJavaParser.getPipelineAsJava(props.getPipelinePath(), props.getLog());

		
		StringBuilder repoType = new StringBuilder();
		int repoFinishLineIdx = getRepoType(pipeClassContent, repoType);
		String pipeContent = pipeClassContent.substring(repoFinishLineIdx + 1);

		props.setRepositoryType(repoType.toString());

		String pipeClass = IO.readFile(PIPES_BASE_PATH + props.getBaseClassName());
		pipeClass = String.format(pipeClass, pipesName, "\"" + repoType + "\"", pipeContent);

		return pipeClass;
	}

	
	private int getRepoType(StringBuilder pipeClassContent, StringBuilder repoType) {
		int endTypeIdx = pipeClassContent.indexOf(", ");
		int endLineIdx = pipeClassContent.indexOf(").");
		int sepLength = "\"".length();

		repoType.append(pipeClassContent.substring((pipeClassContent.indexOf("\"") + sepLength) , (endTypeIdx - sepLength)));
		props.setRepositoryUri(pipeClassContent.substring(endTypeIdx + ", \"".length(), endLineIdx - ")".length()));

		return endLineIdx + ".".length();
	}
}
