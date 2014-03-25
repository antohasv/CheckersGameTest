package resource;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import utils.SAXHandler;
import utils.VFS;

public class ResourceFactory {
    public static final String RESOURCE_DIRECTORY = "/settings";

    private HashMap<String, Resource> resource;
    private static ResourceFactory factory = null;

    private ResourceFactory() {
        this.resource = new HashMap<String, Resource>();
        initializeResources();
    }

    private void initializeResources() {
        List<File> resourses = VFS.getListOfFileByPath(RESOURCE_DIRECTORY);
        for (File file : resourses) {
            getResource(VFS.getRelativePath(file.getAbsolutePath()));
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
        } else {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                SAXHandler saxHandler = new SAXHandler();
                parser.parse(new File(path), saxHandler);
                resource.put(path, (Resource) saxHandler.object);
                return (Resource) saxHandler.object;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
}