package is.hi.foodo;

public class Filter {
	public static CharSequence ratingFrom = "0.0";
	public static CharSequence ratingTo = "5.0";
	public static int radius = 10000;
	public static CharSequence priceFrom = "0";
	public static CharSequence priceTo = "10000";
	// Do we want this hard coded?
	// -Arnar
	public static CharSequence[] types = {"Fast", "Fine dining",
		"Family", "Casual", "Sea", "Launch", "Mexican",
		"Asian", "Vegetarian", "Buffet", "Sandwiches",
		"Bistro", "Drive-in", "Take out", "Steakhouse",
		"Sushi"};
	public static boolean[] checkedTypes = {true, true,
		true, true, true, true, true,
		true, true, true, true,
		true, true, true, true,
		true};
}
