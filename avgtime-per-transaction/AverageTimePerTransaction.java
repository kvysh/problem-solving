import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/*
 * Main class which reads the file through console and calculates the average time per transaction
 * 
 */
public class AverageTimePerTransaction {

	/*
	 * transactionMap contains the transaction name and log time (till END statement
	 * occurs)
	 */
	private static final Map<String, LocalDateTime> transactionMap = new HashMap<String, LocalDateTime>();

	/*
	 * duration field contains the calculated time of each transaction
	 */
	private static final Map<String, Long> duration = new HashMap<String, Long>();

	enum State {
		START, END
	}

	enum Time {
		IN_MINUTES, IN_SECONDS, IN_MILLISECONDS
	}

	private static final String MSG_INPUT = "Please give the file path";
	private static final String MSG_OUTPUT = "Average time per transaction: ";

	public static void main(String[] args) {
		System.out.println(MSG_INPUT);

		// Read file path dynamically
		Scanner scanner = new Scanner(System.in);
		String file = scanner.nextLine();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				parseTransaction(line);
			}

			System.out.println(MSG_OUTPUT + getAverageTime(Time.IN_MINUTES));
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

		scanner.close();
	}

	private static void parseTransaction(String line) {
		Optional<Transaction> optional = Optional.ofNullable(TransactionParser.parse(line));
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			if (transaction.getState().equalsIgnoreCase(State.END.toString())
					&& transactionMap.containsKey(transaction.getName())) {
				calDuration(transaction);
			} else if (transaction.getState().equalsIgnoreCase(State.START.toString())) {
				transactionMap.putIfAbsent(transaction.getName(), transaction.getLoggedAt());
			}
		}
	}

	private static void calDuration(Transaction transaction) {

		Long minutes = Duration.between(transactionMap.get(transaction.getName()), transaction.getLoggedAt())
				.toMinutes();
		duration.put(transaction.getName(), minutes);

		transactionMap.remove(transaction.getName());
	}

	private static long getAverageTime(Time format) {
		switch (format) {
		case IN_MINUTES:
			return getAverageTimeInMinutes();
		case IN_SECONDS:
			return getAverageTimeInSeconds();
		case IN_MILLISECONDS:
			return getAverageTimeInMilliseconds();
		default:
			return 0;
		}
	}

	private static long getAverageTimeInMinutes() {
		long avgTime = 0;
		if (duration.size() > 0) {
			avgTime = getTotalTime() / duration.size();
		}
		return avgTime;
	}

	private static long getAverageTimeInSeconds() {
		long avgTime = 0;
		if (duration.size() > 0) {
			avgTime = (getTotalTime() * 60) / duration.size();
		}
		return avgTime;
	}

	private static long getAverageTimeInMilliseconds() {
		long avgTime = 0;
		if (duration.size() > 0) {
			avgTime = (getTotalTime() * 60 * 1000) / duration.size();
		}
		return avgTime;
	}

	/*
	 * return total time in minutes
	 */
	private static long getTotalTime() {
		return duration.values().stream().reduce(0l, (sum, i) -> (sum + i));
	}
}

/*
 * TransactionParser - parse the given text and finds out the name, loggedAt and
 * state
 * 
 */
class TransactionParser {
	public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

	public static final String DELIMITER = ",";
	public static final int NUMBER_OF_TOKENS = 4;

	public static Transaction parse(String text) {
		String[] tokens = text.split(DELIMITER);
		if (NUMBER_OF_TOKENS == tokens.length) {
			try {
				return new Transaction(tokens[0], formatDateTime(tokens[1], tokens[2]), tokens[3].trim());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}

	public static Transaction parse(String text, DateTimeFormatter dateFormat, DateTimeFormatter timeFormat) {
		if (dateFormat != null)
			DATE_FORMAT = dateFormat;
		if (timeFormat != null)
			TIME_FORMAT = timeFormat;
		return parse(text);
	}

	private static LocalDateTime formatDateTime(String date, String time) {
		// format the date and time
		date = date.replaceAll("\\s", "");
		time = time.trim().toUpperCase();
		return LocalDateTime.of(LocalDate.parse(date, DATE_FORMAT), LocalTime.parse(time, TIME_FORMAT));
	}
}

/*
 * Transaction structure
 */
class Transaction {
	private final String name;
	private final LocalDateTime loggedAt;
	private final String state;

	Transaction(String name, LocalDateTime loggedAt, String state) {
		this.name = name;
		this.loggedAt = loggedAt;
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getLoggedAt() {
		return loggedAt;
	}

	public String getState() {
		return state;
	}
}

