package exam.utility;

import java.io.*;
import java.nio.file.Path;
import java.sql.Timestamp;

public class LogHelper {
    /**
     * Write the login attempt to the provided file.
     * @param username String
     * @param time Timestamp
     * @param success Boolean
     * @param filename String
     */
    public static void writeLoginAttempt(String username, Timestamp time, boolean success, String filename){
        String dir = System.getProperty("user.dir");
        var p = createFileIfNotExists(dir, filename);
        if(p != null)
        {
            try {
                var message = "[" + time + "]" + "Login attempted with user: " + username + " result: " + success;
                var fw = new FileWriter(filename, true);
                var bw = new BufferedWriter(fw);
                var pw = new PrintWriter(bw);

                pw.println(message);

                pw.flush();
                pw.close();
                fw.close();
            } catch(IOException e) {
                System.out.println("Failed to write log. " + e.getMessage());
            }
        }
    }

    /**
     * Check if the file that we want to write to exists, if not try to create it.
     * @param dir String directory name
     * @param filename String file name
     * @return Path of the file created or found. Null if one is not found or created.
     */
    public static Path createFileIfNotExists(String dir, String filename) {
        try {
            File myObj = new File(dir, filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            return Path.of(dir, filename);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }
}
