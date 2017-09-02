package com.reconizer.loveteller.match;

/**
 * Created by dawid on 2017-08-31.
 */

public class MatchesList {
    public String mid;
    public String listyes;
    public String listno;
    public String listmatch;

    public MatchesList(){}

    public MatchesList(String mid, String listyes, String listno, String listmatch){
        this.mid = mid;
        this.listyes = listyes;
        this.listno = listno;
        this.listmatch = listmatch;
    }
}
