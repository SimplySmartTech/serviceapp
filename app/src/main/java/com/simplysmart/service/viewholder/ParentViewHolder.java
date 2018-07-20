package com.simplysmart.service.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simplysmart.service.R;

/**
 * Created by shailendrapsp on 18/11/16.
 */

public class ParentViewHolder extends RecyclerView.ViewHolder{

    public TextView typeName;
    public TextView unit;
    public LinearLayout parent;

    public ParentViewHolder(View itemView) {
        super(itemView);
        this.typeName = (TextView)itemView.findViewById(R.id.typeName);
        this.unit = (TextView) itemView.findViewById(R.id.unit);
        this.parent = (LinearLayout) itemView.findViewById(R.id.parentLayout);
    }
}
