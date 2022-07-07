import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private final Path logFolder;
    private final SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final SimpleDateFormat dateFormatMessage = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private Path logFile;

    public Logger(Path coreFolder) {
        logFolder = Paths.get(coreFolder.toString() + "\\Logs");
        createNewLogFile();
    }

    private void createNewLogFile() {
        String logName = "\\" + dateFormatFile.format(new Date()) + ".txt";
        Path fullNewLogPath = Paths.get(logFolder.toString() + logName);
        logFile = fullNewLogPath;
        if (Files.notExists(fullNewLogPath)) {
            try {
                Files.createFile(fullNewLogPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void writeMessage(String message) {
        String toLog = dateFormatMessage.format(new Date()) + " " + message + "\n";
        try {
            Files.write(logFile, toLog.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
