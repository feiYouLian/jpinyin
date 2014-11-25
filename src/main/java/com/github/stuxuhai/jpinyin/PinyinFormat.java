package com.github.stuxuhai.jpinyin;

/**
 * 拼音格式类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 * @version 1.0
 */
public class PinyinFormat {
	private String name;
	public static final PinyinFormat WITH_TONE_MARK = new PinyinFormat("WITH_TONE_MARK");
	public static final PinyinFormat WITHOUT_TONE = new PinyinFormat("WITHOUT_TONE");
	public static final PinyinFormat WITH_TONE_NUMBER = new PinyinFormat("WITH_TONE_NUMBER");

	protected PinyinFormat(String name) {
		this.name = name;
	}

	protected String getName() {
		return this.name;
	}
}
