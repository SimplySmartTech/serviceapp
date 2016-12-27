package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by shailendrapsp on 27/12/16.
 */

@Table(name = "visitor")
public class VisitorTable extends Model {
    @Column(name = "num_of_person")
    public int num_of_person;

    @Column(name = "details")
    public String details;

    @Column(name = "image_urls")
    public String image_urls;

    @Column(name = "local_image_urls")
    public String local_image_urls;

    @Column(name = "timestamp")
    public long timestamp;

    public VisitorTable() {
        super();
    }

    public static List<VisitorTable> getVisitorTables() {
        return new Select()
                .from(VisitorTable.class)
                .execute();
    }

    public static VisitorTable getVisitorTable(){
        return new Select()
                .from(VisitorTable.class)
                .executeSingle();
    }
}
