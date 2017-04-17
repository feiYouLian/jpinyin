package com.github.stuxuhai.jpinyin;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 汉字转拼音类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 */
public final class PinyinHelper {
	private static List<String> dict = new ArrayList<String>();
	private static final Map<String, String> PINYIN_TABLE = PinyinResource.getPinyinResource();
	private static final Map<String, String> MUTIL_PINYIN_TABLE = PinyinResource.getMutilPinyinResource();
	private static final DoubleArrayTrie DOUBLE_ARRAY_TRIE = new DoubleArrayTrie();
	private static final String PINYIN_SEPARATOR = ","; // 拼音分隔符
	private static final char CHINESE_LING = '〇';
	private static final String ALL_UNMARKED_VOWEL = "aeiouv";
	private static final String ALL_MARKED_VOWEL = "āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜ"; // 所有带声调的拼音字母

	static {
		for (String word : MUTIL_PINYIN_TABLE.keySet()) {
			dict.add(word);
		}
		Collections.sort(dict);
		DOUBLE_ARRAY_TRIE.build(dict);
	}

	private PinyinHelper() {
	}

	/**
	 * 将带声调格式的拼音转换为数字代表声调格式的拼音
	 * 
	 * @param pinyinArrayString
	 *            带声调格式的拼音
	 * @return 数字代表声调格式的拼音
	 */
	private static String[] convertWithToneNumber(String pinyinArrayString) {
		String[] pinyinArray = pinyinArrayString.split(PINYIN_SEPARATOR);
		for (int i = pinyinArray.length - 1; i >= 0; i--) {
			boolean hasMarkedChar = false;
			String originalPinyin = pinyinArray[i].replace("ü", "v"); // 将拼音中的ü替换为v

			for (int j = originalPinyin.length() - 1; j >= 0; j--) {
				char originalChar = originalPinyin.charAt(j);

				// 搜索带声调的拼音字母，如果存在则替换为对应不带声调的英文字母
				if (originalChar < 'a' || originalChar > 'z') {
					int indexInAllMarked = ALL_MARKED_VOWEL.indexOf(originalChar);
					int toneNumber = indexInAllMarked % 4 + 1; // 声调数
					char replaceChar = ALL_UNMARKED_VOWEL.charAt((indexInAllMarked - indexInAllMarked % 4) / 4);
					pinyinArray[i] = originalPinyin.replace(String.valueOf(originalChar), String.valueOf(replaceChar))
							+ toneNumber;
					hasMarkedChar = true;
					break;
				}
			}
			if (!hasMarkedChar) {
				// 找不到带声调的拼音字母说明是轻声，用数字5表示
				pinyinArray[i] = originalPinyin + "5";
			}
		}

		return pinyinArray;
	}

	/**
	 * 将带声调格式的拼音转换为不带声调格式的拼音
	 * 
	 * @param pinyinArrayString
	 *            带声调格式的拼音
	 * @return 不带声调的拼音
	 */
	private static String[] convertWithoutTone(String pinyinArrayString) {
		String[] pinyinArray;
		for (int i = ALL_MARKED_VOWEL.length() - 1; i >= 0; i--) {
			char originalChar = ALL_MARKED_VOWEL.charAt(i);
			char replaceChar = ALL_UNMARKED_VOWEL.charAt((i - i % 4) / 4);
			pinyinArrayString = pinyinArrayString.replace(String.valueOf(originalChar), String.valueOf(replaceChar));
		}
		// 将拼音中的ü替换为v
		pinyinArray = pinyinArrayString.replace("ü", "v").split(PINYIN_SEPARATOR);
		return pinyinArray;
	}

	/**
	 * 将带声调的拼音格式化为相应格式的拼音
	 * 
	 * @param pinyinString
	 *            带声调的拼音
	 * @param pinyinFormat
	 *            拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，WITH_TONE_MARK--带声调
	 * @return 格式转换后的拼音
	 */
	private static String[] formatPinyin(String pinyinString, PinyinFormat pinyinFormat) {
		if (pinyinFormat == PinyinFormat.WITH_TONE_MARK) {
			return pinyinString.split(PINYIN_SEPARATOR);
		} else if (pinyinFormat == PinyinFormat.WITH_TONE_NUMBER) {
			return convertWithToneNumber(pinyinString);
		} else if (pinyinFormat == PinyinFormat.WITHOUT_TONE) {
			return convertWithoutTone(pinyinString);
		}
		return new String[0];
	}

	/**
	 * 将单个汉字转换为相应格式的拼音
	 * 
	 * @param c
	 *            需要转换成拼音的汉字
	 * @param pinyinFormat
	 *            拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，WITH_TONE_MARK--带声调
	 * @return 汉字的拼音
	 */
	public static String[] convertToPinyinArray(char c, PinyinFormat pinyinFormat) {
		String pinyin = PINYIN_TABLE.get(String.valueOf(c));
		if ((pinyin != null) && (!"null".equals(pinyin))) {
			Set<String> set = new LinkedHashSet<String>();
			for (String str : formatPinyin(pinyin, pinyinFormat)) {
				set.add(str);
			}
			return set.toArray(new String[set.size()]);
		}
		return new String[0];
	}

	/**
	 * 将单个汉字转换成带声调格式的拼音
	 * 
	 * @param c
	 *            需要转换成拼音的汉字
	 * @return 字符串的拼音
	 */
	public static String[] convertToPinyinArray(char c) {
		return convertToPinyinArray(c, PinyinFormat.WITH_TONE_MARK);
	}

	/**
	 * 将字符串转换成相应格式的拼音
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @param separator
	 *            拼音分隔符
	 * @param pinyinFormat
	 *            拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，WITH_TONE_MARK--带声调
	 * @return 字符串的拼音
	 * @throws PinyinException
	 */
	public static String convertToPinyinString(String str, String separator, PinyinFormat pinyinFormat)
			throws PinyinException {
		str = ChineseHelper.convertToSimplifiedChinese(str);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int strLen = str.length();
		while (i < strLen) {
			String substr = str.substring(i);
			List<Integer> commonPrefixList = DOUBLE_ARRAY_TRIE.commonPrefixSearch(substr);
			if (commonPrefixList.size() == 0) {
				char c = str.charAt(i);
				// 判断是否为汉字或者〇
				if (ChineseHelper.isChinese(c) || c == CHINESE_LING) {
					String[] pinyinArray = convertToPinyinArray(c, pinyinFormat);
					if (pinyinArray != null) {
						if (pinyinArray.length > 0) {
							sb.append(pinyinArray[0]);
						} else {
							throw new PinyinException("Can't convert to pinyin: " + c);
						}
					} else {
						sb.append(str.charAt(i));
					}
				} else {
					sb.append(c);
				}
				i++;
			} else {
				String words = dict.get(commonPrefixList.get(commonPrefixList.size() - 1));
				String[] pinyinArray = formatPinyin(MUTIL_PINYIN_TABLE.get(words), pinyinFormat);
				for (int j = 0, l = pinyinArray.length; j < l; j++) {
					sb.append(pinyinArray[j]);
					if (j < l - 1) {
						sb.append(separator);
					}
				}
				i += words.length();
			}

			if (i < strLen) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	/**
	 * 将字符串转换成带声调格式的拼音
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @param separator
	 *            拼音分隔符
	 * @return 转换后带声调的拼音
	 * @throws PinyinException
	 */
	public static String convertToPinyinString(String str, String separator) throws PinyinException {
		return convertToPinyinString(str, separator, PinyinFormat.WITH_TONE_MARK);
	}

	/**
	 * 判断一个汉字是否为多音字
	 * 
	 * @param c
	 *            汉字
	 * @return 判断结果，是汉字返回true，否则返回false
	 */
	public static boolean hasMultiPinyin(char c) {
		String[] pinyinArray = convertToPinyinArray(c);
		if (pinyinArray != null && pinyinArray.length > 1) {
			return true;
		}
		return false;
	}

	/**
	 * 获取字符串对应拼音的首字母
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @return 对应拼音的首字母
	 * @throws PinyinException
	 */
	public static String getShortPinyin(String str) throws PinyinException {
		String separator = "#"; // 使用#作为拼音分隔符
		StringBuilder sb = new StringBuilder();

		char[] charArray = new char[str.length()];
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);

			// 首先判断是否为汉字或者〇，不是的话直接将该字符返回
			if (!ChineseHelper.isChinese(c) && c != CHINESE_LING) {
				charArray[i] = c;
			} else {
				int j = i + 1;
				sb.append(c);

				// 搜索连续的汉字字符串
				while (j < len && (ChineseHelper.isChinese(str.charAt(j)) || str.charAt(j) == CHINESE_LING)) {
					sb.append(str.charAt(j));
					j++;
				}
				String hanziPinyin = convertToPinyinString(sb.toString(), separator, PinyinFormat.WITHOUT_TONE);
				String[] pinyinArray = hanziPinyin.split(separator);
				for (String string : pinyinArray) {
					charArray[i] = string.charAt(0);
					i++;
				}
				i--;
				sb.setLength(0);
			}
		}
		return String.valueOf(charArray);
	}

	public static void addPinyinDict(String path) throws FileNotFoundException {
		PINYIN_TABLE.putAll(PinyinResource.getResource(PinyinResource.newFileReader(path)));
	}

	public static void addMutilPinyinDict(String path) throws FileNotFoundException {
		MUTIL_PINYIN_TABLE.putAll(PinyinResource.getResource(PinyinResource.newFileReader(path)));
		dict.clear();
		DOUBLE_ARRAY_TRIE.clear();
		for (String word : MUTIL_PINYIN_TABLE.keySet()) {
			dict.add(word);
		}
		Collections.sort(dict);
		DOUBLE_ARRAY_TRIE.build(dict);
	}
	/**
	 * 获取指定字符串拼音简码数组　即支持多音字
	 *
	 * @param str
	 * @return
	 */
	public static String[] getShortMulti(String str) throws PinyinException {
		StringBuilder result = new StringBuilder();
		//多音字数量
		int multiCount = 1;
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			if (ChineseHelper.isChinese(c)) {
				String[] hanziPinyin = PinyinHelper.convertToPinyinArray(c, PinyinFormat.WITHOUT_TONE);
				if (hanziPinyin != null && hanziPinyin.length > 1) {
					multiCount *= hanziPinyin.length;
				}
			}
		}
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			int j = 0;
			while (j < multiCount) {
				// 首先判断是否为汉字或者〇，不是的话直接将该字符返回
				if (!ChineseHelper.isChinese(c) && c != CHINESE_LING) {
					result.append(c);
					j++;
				} else {
					String[] pinyinArray = convertToPinyinArray(c, PinyinFormat.WITHOUT_TONE);
					if (pinyinArray.length > 1) {
						//多音字
						for (int k = 0, len1 = pinyinArray.length; k < len1; k++) {
							//取第一个字母，为获取简拼
							result.append(pinyinArray[k].charAt(0));
							j++;
						}
					} else {
						//非多音字
						result.append(pinyinArray[0].charAt(0));
						j++;
					}
				}
				if (j == multiCount) {
					result.append(",");
				}

			}

		}
		result.substring(0, result.length() - 1);
		String[] temp = String.valueOf(result).split(",");
		//最后处理，并返回最终结果
		result.setLength(0);
		for (int i = 0, len = multiCount; i < len; i++) {
			for (int j = 0, len1 = str.length(); j < len1; j++) {
				result.append(temp[j].charAt(i));
			}
			result.append(',');
		}


		String[] resultArray = String.valueOf(result).split(",");
		//数组去重处理
		Set<String> set = new LinkedHashSet<String>();
		for (int i = 0; i < resultArray.length; i++) {
			set.add(resultArray[i]);
		}
		return set.toArray(new String[set.size()]);
	}
	/**
	 * 获取指定字符串拼音全码数组　即支持多音字
	 *
	 * @param str
	 * @return
	 */
	public static String[] getFullMulti(String str) {
		StringBuilder result = new StringBuilder();
		//多音字数量
		int multiCount = 1;
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			if (ChineseHelper.isChinese(c)) {
				String[] hanziPinyin = convertToPinyinArray(c, PinyinFormat.WITHOUT_TONE);
				if (hanziPinyin != null && hanziPinyin.length > 1) {
					multiCount *= hanziPinyin.length;
				}
			}
		}
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			int j = 0;
			while (j < multiCount) {
				// 首先判断是否为汉字或者〇，不是的话直接将该字符返回
				if (!ChineseHelper.isChinese(c) && c != CHINESE_LING) {
					result.append(c);
					j++;
				} else {
					String[] pinyinArray = convertToPinyinArray(c, PinyinFormat.WITHOUT_TONE);
					if (pinyinArray.length > 1) {
						//多音字
						for (int k = 0, len1 = pinyinArray.length; k < len1; k++) {
							//取第一个字母，为获取简拼
							result.append(pinyinArray[k]).append(",");
							j++;
						}
					} else {
						//非多音字
						result.append(pinyinArray[0]).append(",");
						j++;
					}
				}
				if (j == multiCount) {
					result.append("#");
				}

			}

		}
		result.substring(0, result.length() - 1);
		String[] temp = String.valueOf(result).split("#");
		//最后处理，并返回最终结果
		result.setLength(0);
		for (int i = 0, len = multiCount; i < len; i++) {
			for (int j = 0, len1 = str.length(); j < len1; j++) {
				String[] tempArr = temp[j].split(",");
				result.append(tempArr[i]);
			}
			result.append('#');
		}
		String[] resultArray = String.valueOf(result).split("#");
		//数组去重处理
		Set<String> set = new LinkedHashSet<String>();
		for (int i = 0; i < resultArray.length; i++) {
			set.add(resultArray[i]);
		}
		return set.toArray(new String[set.size()]);
	}
}
