package com.simplysmart.service.database;

import com.simplysmart.service.model.matrix.ReadingData;

import io.realm.RealmObject;

/**
 * Created by shailendrapsp on 4/11/16.
 */

public class FinalReadingData extends RealmObject{

    private String jsonToSend;

    public FinalReadingData() {
        super();
    }

    public FinalReadingData(String jsonToSend){
        super();
        this.jsonToSend=jsonToSend;
    }
}
