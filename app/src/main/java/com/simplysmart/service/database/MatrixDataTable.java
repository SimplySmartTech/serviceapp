package com.simplysmart.service.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.simplysmart.service.model.matrix.MatrixData;

import java.util.List;

/**
 * Created by shailendrapsp on 2/11/16.
 */

@Table(name = "MATRIX_DATA")
public class MatrixDataTable extends Model{

    @Column(name = "type")
    public String type;

    @Column(name = "utility_id")
    public String utility_id;

    @Column(name = "icon")
    public String icon;



    public MatrixDataTable(){}

    public MatrixDataTable(MatrixData matrixData){
        this.type = matrixData.getType();
        this.utility_id = matrixData.getUtility_id();
        this.icon = matrixData.getIcon();
    }

}
