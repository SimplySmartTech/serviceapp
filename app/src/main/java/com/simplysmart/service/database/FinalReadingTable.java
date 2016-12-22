package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by shailendrapsp on 22/12/16.
 */

@Table(name = "final_reading")
public class FinalReadingTable extends Model {

    @Column(name = "reading")
    public String reading;

    public FinalReadingTable() {
        super();
    }

    public FinalReadingTable(String reading) {
        super();
        this.reading = reading;
    }

    public static List<FinalReadingTable> getReadingsToSubmit(){
        return new Select()
                .from(FinalReadingTable.class)
                .execute();
    }

}
