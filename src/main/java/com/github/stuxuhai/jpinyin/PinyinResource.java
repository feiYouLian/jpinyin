package com.github.stuxuhai.jpinyin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源文件加载类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 */
public final class PinyinResource {

    private PinyinResource() {}

    private static Map<String, String> getResource(String resourceName) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            InputStream is = PinyinResource.class.getResourceAsStream(resourceName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("=");
                map.put(tokens[0], tokens[1]);
            }
            br.close();
        } catch (IOException e) {
            throw new PinyinException(e);
        }

        return map;
    }

    protected static Map<String, String> getPinyinResource() {
        return getResource("/data/pinyin.db");
    }

    protected static Map<String, String> getMutilPinyinResource() {
        return getResource("/data/mutil_pinyin.db");
    }

    protected static Map<String, String> getChineseResource() {
        return getResource("/data/chinese.db");
    }
}
