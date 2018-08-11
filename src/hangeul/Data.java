package hangeul;

import java.util.HashMap;

/**
 * This data class contains data for romanized Korean characters and is able to retrieve
 * the proper Korean character combination. Korean characters are formed with
 * a consonant followed by a vowel, which may or may not be followed by an ending
 * consonant sound
 * @author tim
 *
 */
public class Data {
	
	public static HashMap<String, Character> roma_han = new HashMap<>();
	
	private static final int UNICODE_MIN = 44032;
	private static final int UNICODE_MAX = 55203;
	
	/**
	 * initializes the main roma_han String->Character HashMap with the romanized Korean
	 * mapping to the proper unicode value. Calls fillCombos() first to populate the array
	 * that will contain the 11,172 Korean character combinations, then uses that array
	 * to populate the HashMap
	 */
	private static void initialize() {
		fillCombos();
		for (int i = UNICODE_MIN; i < UNICODE_MAX + 1; i++) {
			roma_han.put(combos[i - UNICODE_MIN], (char) i);
		}
	}
	
	//fills the empty combos array with all 11,172 Korean character combinations
	private static void fillCombos() {
		int count = 0;
		for (int i = 0; i < Data.consonant.length; i++) {
			for (int j = 0; j < Data.vowel.length; j++) {
				for (int k = 0; k < Data.fin.length; k++) {
					combos[count] = Data.consonant[i] + Data.vowel[j] + Data.fin[k];
					count++;
				}
			}
		}
	}
	
	//array contains all possible combinations of Korean characters in romanized Korean
	private static String[] combos = new String[11172];
	
	//consonant component of Korean character
	private final static String[] consonant = new String[] {
			"g", //ᄀ
			"gg", //ᄁ
			"n", //ᄂ
			"d", //ᄃ
			"dd", //ᄄ
			"l", //ᄅ
			"m", //ᄆ
			"b", //ᄇ
			"bb", //ᄈ
			"s", //ᄉ
			"ss", //ᄊ
			"", //ᄋ
			"j", //ᄌ
			"jj", //ᄍ
			"ch", //ᄎ
			"k", //ᄏ
			"t", //ᄐ
			"p", //ᄑ
			"h", //ᄒ
	};
	
	//vowel component of Korean character
	private final static String[] vowel = new String[] {
			"a", //아
			"ae", //애
			"ya", //야
			"yae", //얘
			"eo", //어
			"e", //에
			"yeo", //여
			"ye", //예
			"o", //오
			"wa", //와
			"wae", //왜
			"oe", //외
			"yo", //요
			"u", //우
			"weo", //워
			"we", //웨
			"wi", //위
			"yu", //유
			"eu", //으
			"ui", //의
			"i", //이
	};
	
	//ending component of Korean character
	private final static String[] fin = new String[] {
			"",
			"g",
			"gg",
			"gs",
			"n",
			"nj",
			"nh",
			"d",
			"l",
			"lg",
			"lm",
			"lb",
			"ls",
			"lt",
			"lp",
			"lh",
			"m",
			"b",
			"bs",
			"s",
			"ss",
			"ng",
			"j",
			"ch",
			"k",
			"t",
			"p",
			"h",
	};

	//static initialization block
	static {
		//initialize the data to be ready for usage
		initialize();
	}
	
}
