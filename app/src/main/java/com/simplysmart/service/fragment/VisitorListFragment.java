package com.simplysmart.service.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplysmart.service.R;
import com.simplysmart.service.adapter.VisitorListAdapter;
import com.simplysmart.service.database.VisitorTable;
import com.simplysmart.service.interfaces.PageSelectedListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shekhar on 30/12/16.
 */

public class VisitorListFragment extends BaseFragment implements PageSelectedListener {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_visitor_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showList();
    }

    private void showList() {
        List<VisitorTable> visitorTables = VisitorTable.getAllVisitorTables();
        if (visitorTables != null && visitorTables.size() > 0) {
            Collections.sort(visitorTables, new Comparator<VisitorTable>() {
                @Override
                public int compare(VisitorTable lhs, VisitorTable rhs) {
                    return (int) (rhs.timestamp - lhs.timestamp);
                }
            });

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.visitor_list);
            VisitorListAdapter visitorListAdapter2 = new VisitorListAdapter(getContext(), visitorTables);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(visitorListAdapter2);
            showNoDataFound(false);
        } else {
            showNoDataFound(true);
        }
    }

    private void showNoDataFound(boolean show) {
        TextView no_data_found = (TextView) rootView.findViewById(R.id.no_data_found);

        if (show) {
            no_data_found.setVisibility(View.VISIBLE);
        } else {
            no_data_found.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected() {
        showList();
    }
}
