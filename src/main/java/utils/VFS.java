package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class VFS {
    public static final String USER_DIRECTORY = "user.dir";

    public static final String PROJECT_DIRECTORY = System.getProperty(USER_DIRECTORY) + '\\';

    public static String getAbsolutePath(String path) {
        return isAbsolute(path) ? path : PROJECT_DIRECTORY + path;
    }

    public static String getRelativePath(String path) {
        return isAbsolute(path) ? path.substring(PROJECT_DIRECTORY.length()) : path;
    }

    private static boolean isAbsolute(String path) {
        return path.startsWith(PROJECT_DIRECTORY);
    }

    /**
     * BreadthFirstSearchAlgorithm
     * @param path get Absolute Path
     * @return list of files that containt in that path
     * */
    public static List<File> getListOfFileByPath(String path) {
        path = getAbsolutePath(path);

        List<File> result = new LinkedList<File>();
        Queue<File> queue = new LinkedList<File>();

        File file = new File(path);
        queue.add(file);

        File[] tmp;
        while (queue.size() > 0) {
            file = queue.poll();
            if (file.isDirectory()) {
                tmp = file.listFiles();
                for (int counter = 0; counter < tmp.length; counter++)
                    queue.add(tmp[counter]);
            } else {
                result.add(file);
            }
        }
        return result;
    }

    public static void cleanFile(String path) {
        FileWriter writer = null;
        try {
            File file = new File(getAbsolutePath(path));
            writer = new FileWriter(file);
            writer.write("");
        } catch (Exception e) { e.printStackTrace();
        } finally {
            closeWriter(writer);
        }
    }

    public static void writeToFile(final String path, String data) {
        FileWriter writer = null;
        try {
            File file = new File(getAbsolutePath(path));
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file);
            writer.write(data);
        } catch (Exception e) { e.printStackTrace();
        } finally {
            closeWriter(writer);
        }
    }

    public static void writeToEndOfFile(String path, String data) {
        File file = new File(getAbsolutePath(path));
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(data);
        } catch (Exception e) { e.printStackTrace();
        } finally {
            closeWriter(writer);
        }
    }

    private static void closeWriter(FileWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) { e.printStackTrace();
            }
        }
    }

    public static String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        String line;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(getAbsolutePath(path));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) { e.printStackTrace();
        } finally {
            closeFileReder(fileReader);
        }
        return sb.toString();
    }

    private static void closeFileReder(FileReader fileReader) {
        if (fileReader != null) {
            try {
                fileReader.close();
            } catch (Exception e) { e.printStackTrace();
            }
        }
    }
}