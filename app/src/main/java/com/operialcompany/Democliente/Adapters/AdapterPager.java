package com.operialcompany.Democliente.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.res.Resources;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.operialcompany.Democliente.ActivitiesAndFragments.RestaurantsFragment;
import com.operialcompany.Democliente.ActivitiesAndFragments.UserAccountFragment;
import com.operialcompany.Democliente.ActivitiesAndFragments.CartFragment;
import com.operialcompany.Democliente.ActivitiesAndFragments.OrdersFragment;

/**
 * Created by qboxus on 10/18/2019.
 */
public class AdapterPager extends FragmentPagerAdapter {


    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


    public AdapterPager(final Resources resources, FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment result = null;
        switch (position) {

            case 0:
                result = new RestaurantsFragment();
                break;
            case 1:
                result = new OrdersFragment();
                break;

            case 2:
                result = new CartFragment();
                break;
            case 3:
                result = new UserAccountFragment();
                break;

        }
        return result;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(final int position) {

        return null;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }


    /**
     * Get the Fragment by position
     *
     * @param position tab position of the fragment
     * @return
     */
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}

