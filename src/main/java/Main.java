import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final Path LOGS_FOLDER = Paths.get("F:\\MT4BridgeLogs");
    private static final Logger logger = new Logger(getPathCoreFolder());
    private static final WriteResult writeResult = new WriteResult(logger, getPathCoreFolder());

    public static void main(String[] args) {
        for (Path folder : getListOfLogsFolders()) {
            writeResult.writeToResultFile(folder.getFileName().toString() + "\n");
            writeResult.writeToResultFile( PrepareOutput.getHeaders() + "\n");
            //To sort parsed data by the date from the name of the file
            Map<String, String> allParsedDataFromOneFolder = new TreeMap<>();
            for (Path file : getListOfFiles(folder)) {
               try {
                   if(isFileValid(file)) {
                       Path filePath = file;
                       if (filePath.getFileName().toString().contains(".7z")) {
                           filePath = unarchive(file);
                           //Something went wrong with unarchiving. Thus, it's null and loop cycle needs to be skipped
                           if (filePath == null) continue;
                       }
                       Parser parser = new Parser(filePath, logger);
                       String dateOfLog =  filePath.getFileName().toString().split("_")[0];
                       allParsedDataFromOneFolder.put(dateOfLog, PrepareOutput.getCombinedParsedData(parser, dateOfLog));
                       deleteFile(filePath);
                   }
                   deleteFile(file);
               } catch (ParseException e) {
                   logger.writeMessage("Something with name of "+ file+".Can't get the date from name. It won't be parsed");
               }
            }
            allParsedDataFromOneFolder.forEach((k,v) -> writeResult.writeToResultFile(v+"\n"));
        }
    }

    static Path getPathCoreFolder() {
        Path coreFolder = Paths.get("");
        try {
            coreFolder = Paths.get(Main.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException ignored) {
        }
        return coreFolder;
    }

    static List<Path> getListOfLogsFolders() {
        List<Path> folders = new ArrayList<>();
        try (Stream<Path> stream = Files.list(LOGS_FOLDER)) {
            folders = stream.filter(Files::isDirectory).collect(Collectors.toList());
        } catch (IOException e) {
            logger.writeMessage("IOException. Can't get list of folders with logs");
        }
        return folders;
    }

    static List<Path> getListOfFiles(Path folder) {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(folder)) {
            files = stream.filter(Files::isRegularFile).collect(Collectors.toList());

        } catch (IOException e) {
            logger.writeMessage("IOException. Can't get list of files in the folder: " + folder.getFileName().toString());
        }
        return files;
    }

    /**
     * Checks if file is transaction log and the time of the log is within Mon-Fri period for the past week.
     * It's expected that program will be launched only on Sunday
     */
    static boolean isFileValid(Path file) throws ParseException {
        if (!file.getFileName().toString().contains("transaction")) {
            return false;
        }
        long oneDayMillisec = 86400000;
        long latestDate = new Date().getTime() - oneDayMillisec * 2;
        long earliestDate = latestDate - oneDayMillisec * 5;
        SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date fileDate = dateFormatFile.parse(file.getFileName().toString().split("_")[0]);
        return fileDate.getTime() < latestDate && fileDate.getTime() > earliestDate;
    }

    static Path unarchive(Path archivedLog) {
        try (SevenZFile sevenZFile = new SevenZFile(new File(archivedLog.toString()))) {
            SevenZArchiveEntry archiveEntry = sevenZFile.getNextEntry();
            Path unarchived = Paths.get(archivedLog.getParent().toString() +"\\"+ archiveEntry.getName());
            File un7z = new File(unarchived.toString());
            byte[] content = new byte[(int) archiveEntry.getSize()];
            sevenZFile.read(content);
            Files.write(un7z.toPath(), content);
            return unarchived;
        } catch (IOException e) {
            logger.writeMessage("IOException Can't unarchive " + archivedLog);
        }
        return null;
    }

    static void deleteFile (Path file) {
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
        }
        catch (IOException e) {
            logger.writeMessage("IOException Can't delete "+file);
        }
    }
}