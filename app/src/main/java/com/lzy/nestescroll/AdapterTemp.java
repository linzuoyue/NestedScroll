package com.lzy.nestescroll;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO <br/>
 * 2020/11/11 4:38 PM <br/>
 *
 * @author 林佐跃 <br/>
 * @since V TODO <br/>
 */
public class AdapterTemp extends RecyclerView.Adapter<AdapterTemp.BaseViewHolder> {
    private NestedViewPagerActivityTest2 nestedViewPagerActivityTest2;

    public AdapterTemp(NestedViewPagerActivityTest2 nestedViewPagerActivityTest2) {

        this.nestedViewPagerActivityTest2 = nestedViewPagerActivityTest2;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType != 4) {
            return new BaseViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycler1, parent, false));
        }
        return new BaseViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (position == 4) {
            ViewPager2 viewpagerView = holder.itemView.findViewById(R.id.viewpager_view);
            if (viewpagerView.getAdapter() != null) {
                return;
            }
            ViewPager2Adapter pagerAdapter = new ViewPager2Adapter(nestedViewPagerActivityTest2, getPageFragments());
            viewpagerView.setAdapter(pagerAdapter);
            viewpagerView.setNestedScrollingEnabled(false);
            final String[] labels = new String[]{"linear", "scroll", "recycler"};
            TabLayout tabLayout = holder.itemView.findViewById(R.id.tablayout);
            new TabLayoutMediator(tabLayout, viewpagerView, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    tab.setText(labels[position]);
                }
            }).attach();
        } else {
            ((TextView)holder.itemView).setText("head" + position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    private List<Fragment> getPageFragments() {
        List<Fragment> data = new ArrayList<>();
        data.add(new RecyclerViewFragment());
        data.add(new RecyclerViewFragment());
        data.add(new RecyclerViewFragment());
        return data;
    }

}
