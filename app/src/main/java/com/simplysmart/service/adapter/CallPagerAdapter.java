package com.simplysmart.service.adapter;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class CallPagerAdapter extends FragmentStateAdapter {
	private List<Fragment> fragments;

	public CallPagerAdapter(FragmentActivity fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}



	@NonNull
	@Override
	public Fragment createFragment(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemCount() {
		return this.fragments.size();
	}
}