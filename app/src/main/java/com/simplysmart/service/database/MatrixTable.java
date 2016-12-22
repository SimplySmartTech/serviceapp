package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.simplysmart.service.model.matrix.MatrixData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shailendrapsp on 22/12/16.
 */

@Table(name = "matrix")
public class MatrixTable extends Model {

    @Column(name = "unit_id")
    public String unit_id;

    @Column(name = "type")
    public String type;

    @Column(name = "utility_id")
    public String utility_id;

    @Column(name = "icon")
    public String icon;

    @Column(name = "orderby")
    public int order;

    @Column(name = "mandatory")
    public boolean mandatory;


    public MatrixTable() {
        super();
    }

    public MatrixTable(MatrixData data,String unit_id) {
        super();
        this.unit_id = unit_id;
        this.type = data.getType();
        this.utility_id = data.getUtility_id();
        this.icon = data.getIcon();
        this.order = data.getOrder();
        this.mandatory = data.isMandatory();
    }

    public static MatrixTable getMatrixInfo(MatrixData matrixData) {
        return new Select()
                .from(MatrixTable.class)
                .where("utility_id = ?", matrixData.getUtility_id())
                .executeSingle();
    }

    public static List<MatrixTable> getMatrixList(String unit_id){
        return new Select()
                .from(MatrixTable.class)
                .where("unit_id = ?",unit_id)
                .execute();
    }
}