package com.github.stuxuhai.jpinyin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源文件加载类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 */
public final class PinyinResource {

    private PinyinResource() {
    }

    protected static Reader newClassPathReader(String classpath) throws UnsupportedEncodingException {
        InputStream is = PinyinResource.class.getResourceAsStream(classpath);
        return new InputStreamReader(is, "UTF-8");
    }

    protected static Reader newFileReader(String path) throws UnsupportedEncodingException, FileNotFoundException {
        return new InputStreamReader(new FileInputStream(path), "UTF-8");
    }

    protected static Map<String, String> getResource(Reader reader) {
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        try {
            BufferedReader br = new BufferedReader(reader);
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
        try {
            return getResource(newClassPathReader("/data/pinyin.dict"));
        } catch (UnsupportedEncodingException e) {
            throw new PinyinException(e);
        }
    }

    protected static Map<String, String> getMutilPinyinResource() {
        try {
            return getResource(newClassPathReader("/data/mutil_pinyin.dict"));
        } catch (UnsupportedEncodingException e) {
            throw new PinyinException(e);
        }
    }

    protected static Map<String, String> getChineseResource() {
        try {
            return getResource(newClassPathReader("/data/chinese.dict"));
        } catch (UnsupportedEncodingException e) {
            throw new PinyinException(e);
        }
    }
}
