import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
//Headers
//Total number of transactions,0ms - 50ms,50ms - 100ms,100ms - 250ms,250ms -350ms,350ms - 500ms,500ms -1s,1s - 2s,2s - 3s,3s - 4s,4s - 5s,5s-55s,>55s,Average Execution time sec,Median Duration,200 - 400 count,Day Burstiness Score 200 - 400,400 - 800 count,Day Burstiness Score 400 - 800,800 - 1000 count,Day Burstiness Score 800 - 1000,>1000 count,Day Burstiness Score >1000

public class PrepareOutput {

    private static String rangesDuration(Parser parser) {
        StringBuilder stringBuilder = new StringBuilder();
        parser.durationRanges().forEach((k, v) -> stringBuilder.append(v).append(","));
        return stringBuilder.toString();
    }

    private static String string1SecPeriod(Parser parser) {
        StringBuilder stringBuilder = new StringBuilder();
        parser.periodSecAnalysis().forEach(
                (k,v) -> stringBuilder.append(v[0]).append(",").append(v[1]).append(",")
        );
        return stringBuilder.toString();
    }
//it's too messy to include such date to result file. It would be better to parse the file with manual parser if needed
//    private String over55SecDurationsAndId() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("requestID,Duration\n");
//        parser.getTimeOutIdAndDuration().forEach(
//                x -> stringBuilder.append(x).append("\n")
//        );
//        return stringBuilder.toString();
//    }

    public static String getCombinedParsedData(Parser parser, String dateOfLog) {
        return dateOfLog+ ","+ parser.amountOfTransactions() + "," + rangesDuration(parser) + parser.averageDuration() + "," + parser.getMedian() +
                "," + string1SecPeriod(parser);
    }

    public static String getHeaders() {
        return "Date,Total number of transactions,0ms - 50ms,50ms - 100ms,100ms - 250ms,250ms -350ms," +
                "350ms - 500ms,500ms -1s,1s - 2s,2s - 3s,3s - 4s,4s - 5s,5s-55s,>55s,Average Execution time sec,Median Duration," +
                "200 - 400 count,Day Burstiness Score 200 - 400,400 - 800 count,Day Burstiness Score 400 - 800,800 - 1000 count," +
                "Day Burstiness Score 800 - 1000,>1000 count,Day Burstiness Score >1000";
    }
}