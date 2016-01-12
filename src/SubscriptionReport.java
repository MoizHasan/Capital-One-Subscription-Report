import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * outputs a list of subscription IDs, their subscription type (daily, monthly,
 * yearly, one-off), and the duration of their subscription.
 * 
 * Bonus Questions (not required): 1. Give annual revenue numbers for all years
 * between 1966 and 2014. Which years had the highest revenue growth, and
 * highest revenue loss? 2. Predict annual revenue for year 2015 (based on
 * historical retention and new subscribers)
 */
public class SubscriptionReport {

	public enum SubType {
		// enum used to keep track of the subscription type.
		DAILY, MONTHLY, YEARLY, ONEOFF
	};

	// Stores the type of the account.
	private SubType type;

	// The first day the account was accessed.
	private String firstDay;
	// The last day the account was accessed.
	private String lastDay;
	// The most recent day the account was accessed.
	private String recentDay;

	// We don't need to set recentDay for accounts only accessed once.
	public SubscriptionReport(SubType type, String firstDay, String lastDay) {
		this.type = type;
		this.firstDay = firstDay;
		this.lastDay = lastDay;
	}

	public int getDuration(String firstDay, String lastDay) {
		// convert raw data into date object for first and last.
		// Calculate difference in days after parsing.
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		Date first = null;
		Date last = null;
		try {
			first = format.parse(firstDay);
			last = format.parse(lastDay);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long diff = last.getTime() - first.getTime();
		long diffInDays = diff / (24 * 60 * 60 * 1000);
		return (int) diffInDays;
	}

	public String getDurationString() {
		int duration = this.getDuration(getFirstDay(), getLastDay());
		int years = duration / 365;
		int months = (duration - years * 365) / 30;
		int days = (duration % 365) % 30;
		String durationString = "";
		if (this.getType() == SubType.ONEOFF) {
			durationString = " is a One-Off Account";
		}
		if (this.getType() == SubType.DAILY) {
			durationString = " is a Daily Account that has been open for "
					+ years + " years, " + months + " months and " + days
					+ " days";
		} else if (this.getType() == SubType.MONTHLY) {
			durationString = " is a Monthly Account that has been open for "
					+ years + " years, " + months + " months and " + days
					+ " days";
		} else if (this.getType() == SubType.YEARLY) {
			durationString = " is a Yearly Account that has been open for "
					+ years + " years, " + months + " months and " + days
					+ " days";
		}

		return durationString;
	}

	public static void printSubscribers(
			HashMap<String, SubscriptionReport> subMap) {

		// Iterate over the hashmap of subscription and print a string of
		// relevant information
		Iterator<Entry<String, SubscriptionReport>> it = subMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, SubscriptionReport> pair = (Entry<String, SubscriptionReport>) it
					.next();
			System.out.println("Subscription " + pair.getKey()
					+ pair.getValue().getDurationString());
		}
	}

	public static void printRevenues(TreeMap<String, Integer> revenueMap) {
		int changeInRevenue = 0;
		int highestGrowth = 0;
		int highestLoss = 0;
		String highYear = "";
		String lowYear = "";
		String prev = "";
		boolean isFirst = true;
		Iterator<Entry<String, Integer>> it = revenueMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pair = (Entry<String, Integer>) it
					.next();
			if (isFirst == false) {
				changeInRevenue = pair.getValue() - revenueMap.get(prev);
				System.out.println("Total Annual Revenue for " + pair.getKey()
						+ " was " + pair.getValue() + " Dollars. A "
						+ changeInRevenue
						+ " Dollar change in revenue was seen.");
				prev = pair.getKey();
				if (changeInRevenue > highestGrowth) {
					highestGrowth = changeInRevenue;
					highYear = pair.getKey();
				}
				if (changeInRevenue < highestLoss) {
					highestLoss = changeInRevenue;
					lowYear = pair.getKey();
				}
			} else {
				System.out.println("Total Annual Revenue for " + pair.getKey()
						+ " was " + pair.getValue() + " Dollars.");
				isFirst = false;
				prev = pair.getKey();
			}
		}
		System.out.println("The year with the highest revenue growth was "
				+ highYear + " with a gain of " + highestGrowth);
		System.out.println("The year with the highest revenue loss was "
				+ lowYear + " with a loss of " + highestLoss);
	}

	public SubType getType() {
		return type;
	}

	public String getFirstDay() {
		return this.firstDay;
	}

	public String getLastDay() {
		return this.lastDay;
	}

	public String getRecentDay() {
		return this.recentDay;
	}

	public void setType(SubType type) {
		this.type = type;
	}

	public void setFirstDay(String firstDay) {
		this.firstDay = firstDay;
	}

	public void setLastDay(String lastDay) {
		this.lastDay = lastDay;
	}

	public void setRecentDay(String recentDay) {
		this.recentDay = recentDay;
	}

	public static void main(String[] args) {
		// for predictive model where index 0 is 2010 and index 4 is 2014
		// Store subscribers who made their first transaction in each respective
		// year.
		int[] subscribersGained = new int[5];
		// Where num of previous subscribers who made a transaction in each year
		// are tracked
		// following same methodology as subscribersGained.
		// Does not include oneoffs
		int[] uniqueSubscribersRetained = new int[5];
		String line;
		HashMap<String, SubscriptionReport> subMap = new HashMap<String, SubscriptionReport>();

		// Store year as key and value as revenue for that year.
		// TreeMap used to preserve chronological order.
		TreeMap<String, Integer> revenueMap = new TreeMap<String, Integer>();
		try {
			// make sure that subscription report is in the correct folder.
			BufferedReader buffer = new BufferedReader(new FileReader(
					"subscription_report.csv"));
			while ((line = buffer.readLine()) != null) {
				if (line.contains("/")) {
					String[] lineArray = line.split(",");
					String subID = lineArray[1];
					int revenue = Integer.parseInt(lineArray[2]);
					String date = lineArray[3];
					String year = date.substring(date.lastIndexOf("/") + 1,date.length());
					int yearAsInt = Integer.parseInt(year);
					if (revenueMap.containsKey(year)) {
						revenueMap.put(year, revenueMap.get(year) + revenue);
					} else {
						revenueMap.put(year, revenue);
					}

					if (subMap.containsKey(subID)) {
						// update last day to be current day
						// update recent day to be last day after calculations.
						// update subscription type.
						subMap.get(subID).setLastDay(date);
						int daysBetweenUse = subMap.get(subID).getDuration(
								subMap.get(subID).getFirstDay(),
								subMap.get(subID).getRecentDay());
						if (daysBetweenUse >= 365
								&& subMap.get(subID).getType() == SubType.ONEOFF) {
							subMap.get(subID).setType(SubType.YEARLY);
						} else if (daysBetweenUse > 30 && daysBetweenUse < 365
								&& subMap.get(subID).getType() != SubType.DAILY) {
							subMap.get(subID).setType(SubType.MONTHLY);
						} else if (daysBetweenUse < 30 && daysBetweenUse > 0) {
							subMap.get(subID).setType(SubType.DAILY);
						}
						String recentYear = subMap.get(subID).getRecentDay().substring(subMap.get(subID).getRecentDay().lastIndexOf("/") + 1, subMap.get(subID).getRecentDay().length());
						if (yearAsInt > 2009 &&  !recentYear.equals(year)) {
							//add subscribers that had previous transactions 
							//and make sure to only count each subscriberID once per year
							uniqueSubscribersRetained[yearAsInt - 2010] += 1;
						}
						subMap.get(subID).setRecentDay(date);
					}

					else {
						// create new Subscription report- set type to oneoff
						// for now both dates are set to current val.
						// subscription report becomes value assoc. with subID
						// in hashmap.
						SubscriptionReport report = new SubscriptionReport(
								SubType.ONEOFF, date, date);
						report.setRecentDay(date);
						subMap.put(subID, report);
						if (yearAsInt > 2009) {
							subscribersGained[yearAsInt - 2010] += 1;
						}

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		

		String s = "";
		while (!s.contains("exit")) {
			System.out
					.println("\n"
							+ "Enter info to view subscriber info, enter revenue to view revenue numbers, exit to escape.");
			Scanner scan = new Scanner(System.in);
			s = scan.next();
			if (s.contains("info")) {
				printSubscribers(subMap);
			}
			if (s.contains("revenue")) {
				printRevenues(revenueMap);
			}
		}
	}
}
