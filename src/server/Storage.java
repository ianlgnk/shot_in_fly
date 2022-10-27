package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;

public class Storage {
    private BufferedWriter bew;

    public void storeMatch(String content) {
        try {
            Long now = new Date().getTime();
            String filePath = System.getProperty("user.dir") + "/server/storage/{fileName}.txt";

            System.out.println("\nSaving file at: " + filePath);

            bew = new BufferedWriter(
                    new FileWriter(
                            filePath.replace("{fileName}", String.valueOf(now))
                    )
            );
            bew.write(content);
            bew.close();
        } catch (Exception e) {
            System.out.println("Something went wrong trying to storage the match.");
        }
    }

}
