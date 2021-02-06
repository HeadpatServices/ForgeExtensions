package services.headpat.forgeextensions;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;

public enum ColorCode {
	/**
	 * Represents black
	 */
	BLACK('0', 0x00),
	/**
	 * Represents dark blue
	 */
	DARK_BLUE('1', 0x1),
	/**
	 * Represents dark green
	 */
	DARK_GREEN('2', 0x2),
	/**
	 * Represents dark blue (aqua)
	 */
	DARK_AQUA('3', 0x3),
	/**
	 * Represents dark red
	 */
	DARK_RED('4', 0x4),
	/**
	 * Represents dark purple
	 */
	DARK_PURPLE('5', 0x5),
	/**
	 * Represents gold
	 */
	GOLD('6', 0x6),
	/**
	 * Represents gray
	 */
	GRAY('7', 0x7),
	/**
	 * Represents dark gray
	 */
	DARK_GRAY('8', 0x8),
	/**
	 * Represents blue
	 */
	BLUE('9', 0x9),
	/**
	 * Represents green
	 */
	GREEN('a', 0xA),
	/**
	 * Represents aqua
	 */
	AQUA('b', 0xB),
	/**
	 * Represents red
	 */
	RED('c', 0xC),
	/**
	 * Represents light purple
	 */
	LIGHT_PURPLE('d', 0xD),
	/**
	 * Represents yellow
	 */
	YELLOW('e', 0xE),
	/**
	 * Represents white
	 */
	WHITE('f', 0xF),
	/**
	 * Represents magical characters that change around randomly
	 */
	MAGIC('k', 0x10, true),
	/**
	 * Makes the text bold.
	 */
	BOLD('l', 0x11, true),
	/**
	 * Makes a line appear through the text.
	 */
	STRIKETHROUGH('m', 0x12, true),
	/**
	 * Makes the text appear underlined.
	 */
	UNDERLINE('n', 0x13, true),
	/**
	 * Makes the text italic.
	 */
	ITALIC('o', 0x14, true),
	/**
	 * Resets all previous chat colors or formats.
	 */
	RESET('r', 0x15);

	public static final char COLOR_CHAR = '§';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

	@Getter
	private final int intCode;
	@Getter
	private final char code;
	@Getter
	private final boolean isFormat;
	private final String toString;
	private static final Map<Character, ColorCode> BY_CHAR = Maps.newHashMap();

	ColorCode(char code, int intCode) {
		this(code, intCode, false);
	}

	ColorCode(char code, int intCode, boolean isFormat) {
		this.code = code;
		this.intCode = intCode;
		this.isFormat = isFormat;
		this.toString = new String(new char[]{COLOR_CHAR, code});
	}

	public boolean isColor() {
		return !isFormat && this != RESET;
	}

	public static ColorCode getByChar(char code) {
		return BY_CHAR.get(code);
	}

	public static ColorCode getByChar(String code) {
		Validate.notNull(code, "Code cannot be null");
		Validate.isTrue(code.length() > 0, "Code must have at least one char");

		return BY_CHAR.get(code.charAt(0));
	}

	/**
	 * Strips the given message of all color codes
	 *
	 * @param input String to strip of color
	 * @return A copy of the input string, without any coloring
	 */
	@Contract("!null -> !null; null -> null")
	@Nullable
	public static String stripColor(@Nullable final String input) {
		if (input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile(COLOR_CHAR + "x(?>" + COLOR_CHAR + "[0-9a-f]){6}", Pattern.CASE_INSENSITIVE); // Paper - Support hex colors in getLastColors

	/**
	 * Gets the ChatColors used at the end of the given input string.
	 *
	 * @param input Input string to retrieve the colors from.
	 * @return Any remaining ChatColors to pass onto the next line.
	 */
	@NotNull
	public static String getLastColors(@NotNull String input) {
		Validate.notNull(input, "Cannot get last colors from null text");

		StringBuilder result = new StringBuilder();
		int length = input.length();

		// Search backwards from the end as it is faster
		for (int index = length - 1; index > -1; index--) {
			char section = input.charAt(index);
			if (section == COLOR_CHAR && index < length - 1) {
				// Paper start - Support hex colors
				if (index > 11 && input.charAt(index - 12) == COLOR_CHAR && (input.charAt(index - 11) == 'x' || input.charAt(index - 11) == 'X')) {
					String color = input.substring(index - 12, index + 2);
					if (HEX_COLOR_PATTERN.matcher(color).matches()) {
						result.insert(0, color);
						break;
					}
				}
				// Paper end
				char c = input.charAt(index + 1);
				ColorCode color = getByChar(c);

				if (color != null) {
					result.insert(0, color.toString());

					// Once we find a color or reset we can stop searching
					if (color.isColor() || color.equals(RESET)) {
						break;
					}
				}
			}
		}

		return result.toString();
	}

	/**
	 * Translates a string using an alternate color code character into a
	 * string that uses the internal ChatColor.COLOR_CODE color code
	 * character. The alternate color code character will only be replaced if
	 * it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
	 *
	 * @param altColorChar    The alternate color code character to replace. Ex: {@literal &}
	 * @param textToTranslate Text containing the alternate color code character.
	 * @return Text containing the ChatColor.COLOR_CODE color code character.
	 */
	@NotNull
	public static String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
		Validate.notNull(textToTranslate, "Cannot translate null text");

		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
				b[i] = ColorCode.COLOR_CHAR;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	@Override
	public String toString() {
		return toString;
	}

	static {
		for (ColorCode color : values()) {
			BY_CHAR.put(color.code, color);
		}
	}
}
