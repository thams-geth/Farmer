package com.tts.farmer;

/**
 * Created by THAMS on 31-Mar-18.
 */

public class pushid {
    public String docid,category;
    public <T extends pushid> T withid(final String docid,final String category){
        this.docid=docid;
        this.category=category;
        return (T) this;
    }
    public <T extends pushid> T withid(final String docid){
        this.docid=docid;
        return (T) this;
    }


}
