package resource;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import utils.SAXHandler;
import utils.VFS;

public class ResourceFactory {

	private HashMap<String, Resource> resource;
	private static ResourceFactory factory=null;

	private ResourceFactory() {
		this.resource = new HashMap<String, Resource>();
		List<File> resourses = VFS.bfs("/settings");
		Object[] resourseFiles = resourses.toArray();
		File file;
		String relativePath;
		for(int count=0;count<resourseFiles.length;count++){
			file=(File)resourseFiles[count];
			relativePath=VFS.getRelativePath(file.getAbsolutePath());
			getResource(relativePath);
		}
			
	}

	public static ResourceFactory instanse() {
		if (factory == null) {
			factory = new ResourceFactory();
		}
		return factory;
	}

	public Resource getResource(String path) {
		if (resource.containsKey(path)) {
			return resource.get(path);
		}
		else {
			try{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				SAXHandler saxHandler = new SAXHandler();
				parser.parse(new File(path), saxHandler);
				resource.put(path, (Resource) saxHandler.object);
				return (Resource) saxHandler.object;
			}
			catch(Exception e){
				System.err.println("\nError:");
				System.err.println("ResourceFactory, getResource");
				System.err.println(e.getMessage());
			}
			return null;
		}
	}
}