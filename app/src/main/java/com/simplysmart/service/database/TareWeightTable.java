package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.simplysmart.service.model.matrix.TareWeight;

import java.util.List;

/**
 * Created by shailendrapsp on 22/12/16.
 */

@Table(name = "tare_weight")
public class TareWeightTable extends Model {

    @Column(name = "unit_id")
    public String unit_id;

    @Column(name = "name")
    public String name;

    @Column(name = "value")
    public String value;

    @Column(name = "info")
    public String info;

    public TareWeightTable() {
        super();
    }

    public TareWeightTable(TareWeight tareWeight, String unit_id) {
        super();
        this.unit_id = unit_id;
        this.name = tareWeight.getName();
        this.value = tareWeight.getValue();
        this.info = tareWeight.getInfo();
    }

    public static List<TareWeightTable> getTareWeights(String unit_id) {
        return new Select()
                .from(TareWeightTable.class)
                .where("unit_id = ?", unit_id)
                .execute();
    }
}