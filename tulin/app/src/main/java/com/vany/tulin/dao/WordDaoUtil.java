package com.vany.tulin.dao;

import com.vany.tulin.dto.Word;
import com.vany.tulin.dto.WordDao;

import java.util.List;

/**
 * Created by van元 on 2017/2/17.
 */

public class WordDaoUtil {
    private WordDao mWordDao;
    private static WordDaoUtil wordDaoUtil;

    private WordDaoUtil() {}

    public static WordDaoUtil getSingleTon() {
        if (wordDaoUtil == null) {
            wordDaoUtil = new WordDaoUtil();
        }
        return wordDaoUtil;
    }
    public WordDao getWordDao() {
        return mWordDao;
    }
    public void setWordDao(WordDao wordDao) {
        this.mWordDao = wordDao;
    }

    /**
     * 添加单词
     * @param word
     */
    public void insertWord(Word word) {
        mWordDao.insert(word);
    }

    /**
     * 根据单词名称来查找单词
     * @param word
     * @return
     */
    public Word getWordByWordName(String word) {
        return mWordDao.queryBuilder()
                .where(WordDao.Properties.Word.eq(word)).unique();
    }

    /**
     * 获取20条分页数据
     * @param offset
     * @return
     */
    public List<Word> getTwentyDatas(int offset) {
       return mWordDao.queryBuilder().offset(offset * 20).limit(20).list();
    }

    /**
     * 获取所有数据
     * @return
     */
    public List<Word> loadAll() {
        return mWordDao.loadAll();
    }

    /**
     * 根据实体来删除
     * @param word
     */
    public void deleteByEntity(Word word) {
        mWordDao.delete(word);
    }

    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(Long id) {
        mWordDao.deleteByKey(id);
    }

    /**
     * 删除所有数据
     */
    public void deleteAll() {
        mWordDao.deleteAll();
    }
}
