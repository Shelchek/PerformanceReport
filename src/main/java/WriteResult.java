import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteResult {
    private final Logger logger;
    private final Path resultFolder;

    public WriteResult (Logger logger, Path coreFolder) {
        this.logger = logger;
        resultFolder = Paths.get(coreFolder.toString() + "\\Result");
        createResultFile();
    }

    private Path getPathForResultFile() {
        String fileName = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date()) +".csv";
        return Paths.get(resultFolder.toString() + "\\"+fileName);
    }

    private void createResultFile() {
        Path filePath = getPathForResultFile();
        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            logger.writeMessage("IOException can't create result file " + filePath);
        }
    }

    public void writeToResultFile (String string) {
        try {
            Files.write(getPathForResultFile(), string.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.writeMessage("IOException can't write to "+getPathForResultFile());
        }
    }
}
