package com.github.stuxuhai.jpinyin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 资源文件加载类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 * @version 1.0
 */
public class PinyinResource {

	private static Properties getResource(String resourceName) {
		InputStream is = PinyinResource.class.getResourceAsStream(resourceName);
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return props;
	}

	protected static Properties getPinyinTable() {
		String resourceName = "/data/pinyin.db";
		return getResource(resourceName);
	}

	protected static Properties getMutilPintinTable() {
		String resourceName = "/data/mutil_pinyin.db";
		return getResource(resourceName);
	}

	protected static Properties getChineseTable() {
		String resourceName = "/data/chinese.db";
		return getResource(resourceName);
	}
}
