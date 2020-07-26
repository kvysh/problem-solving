import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

/*
 * Main class which reads the file through console and calculates the average time per transaction
 * 
 */
public class AverageTimePerTransaction {

	/*
	 * transactionList contains the transaction name and log time (till END
	 * statement occurs)
	 */
	private static HashMap<String, LocalDateTime> transactionList = new HashMap<String, LocalDateTime>();

	/*
	 * duration field contains the calculated time of each transaction
	 */
	private static HashMap<String, Long> duration = new HashMap<String, Long>();

	private static final String STATE_START = "start";
	private static final String STATE_END = "end";
	private static final String MSG_INPUT = "Please give the file path";
	private static final String MSG_OUTPUT = "Average time per transaction (in Minutes): ";

	public static void main(String[] args) {
		System.out.println(MSG_INPUT);

		// Read file path dynamically
		Scanner scanner = new Scanner(System.in);
		String file = scanner.nextLine();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				parseAndCalculate(line);
			}

			System.out.println(MSG_OUTPUT + getAverageTime());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

		scanner.close();
	}

	private static void parseAndCalculate(String line) {
		Optional<Transaction> optional = Optional.ofNullable(TransactionParser.parse(line));
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			if (transaction.getState().equalsIgnoreCase(STATE_END)) {
				if (transactionList.containsKey(transaction.getName()))
					calDuration(transaction);
			} else if (transaction.getState().equalsIgnoreCase(STATE_START)) {
				transactionList.putIfAbsent(transaction.getName(), transaction.getLoggedAt());
			}
		}
	}

	private static void calDuration(Transaction transaction) {
		duration.put(transaction.getName(),
				Duration.between(transactionList.get(transaction.getName()), transaction.getLoggedAt()).toMinutes());
		transactionList.remove(transaction.getName());
	}

	private static long getAverageTime() {
		long avgTime = 0;
		if (duration.size() > 0) {
			long total = duration.values().stream().reduce(0l, (sum, i) -> {
				return sum + i;
			});
			avgTime = total / duration.size();
		}
		return avgTime;
	}
}

/*
 * TransactionParser - parse the given text and finds out the name, loggedAt and
 * state
 * 
 */
class TransactionParser {
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd h:mm a";
	public static final String DELIMITER = ",";
	public static final int NUMBER_OF_TOKENS = 4;

	public static Transaction parse(String text) {
		String[] tokens = text.split(DELIMITER);
		if (tokens.length == NUMBER_OF_TOKENS) {
			try {
				return new Transaction(tokens[0], formatDateTime(tokens[1], tokens[2]), tokens[3].trim());
			} catch (Exception ex) {
				return null;
			}
		}
		return null;
	}

	private static LocalDateTime formatDateTime(String date, String time) {
		// format the date and time
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
		date = date.replaceAll("\\s", "");
		time = time.trim().toUpperCase();
		return LocalDateTime.parse(date + " " + time, format);
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
