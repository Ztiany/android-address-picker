package me.ztiany.android.apicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddressPicker extends DialogFragment implements AddressLoader.AddressQueryCallback {

    private TabLayout mTabLayout;

    private AddressListener mAddressListener;

    /**
     * 国家->省->市->区
     */
    private static final int FIX_ADDRESS_ITEM_COUNT = 4;

    private AddressLoader mAddressInquirers;
    private AddressAdapter mAddressAdapter;

    private List<IName> mSource;
    private List<IName> mCurrentSource;

    private final IName.AddressToken[] mAddressItems = new IName.AddressToken[FIX_ADDRESS_ITEM_COUNT];
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Common_Floating);
        mAddressInquirers = new AddressLoader(requireContext(), this);
        mAddressAdapter = new AddressAdapter(requireContext());
        mAddressAdapter.setItemClickListener(mItemClickListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = View.inflate(requireContext(), R.layout.dialog_address_picker, null);
        mRecyclerView = layoutView.findViewById(R.id.rv_content);
        mTabLayout = layoutView.findViewById(R.id.tbl_tab);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAddressAdapter);
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListenerAdapter);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window dialogWindow = requireDialog().getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = (int) (getScreenHeight() * 0.6F);
            dialogWindow.setAttributes(lp);
        }
        if (mSource == null || mSource.isEmpty()) {
            mAddressInquirers.start();
        }
    }

    AddressPicker setAddressListener(AddressListener addressListener) {
        mAddressListener = addressListener;
        return this;
    }

    @Override
    public void onGetAddress(List<IName> names) {
        mSource = names;
        startShowAddress();
    }

    private void startShowAddress() {
        mCurrentSource = mSource;
        showNext(true);
    }

    private void addTab() {
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setText(R.string.please_select);
        mTabLayout.addTab(tab);
        tab.setTag(mCurrentSource);
        tab.select();
    }

    private void showNext(boolean addTab) {
        if (addTab) {
            addTab();
        }
        mAddressAdapter.replaceAll(mCurrentSource);
        mRecyclerView.scrollToPosition(0);
    }

    private final TabLayout.OnTabSelectedListener mOnTabSelectedListenerAdapter = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (mTabLayout.getTabAt(mTabLayout.getTabCount() - 1) == tab) {//最后一个
                return;
            }

            @SuppressWarnings("unchecked")
            List<IName> iNames = (List<IName>) tab.getTag();

            //找到 tab 位置
            int index = 0;
            int tabCount = mTabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                if (tab == mTabLayout.getTabAt(i)) {
                    index = i;
                    break;
                }
            }

            //去掉被点击 tab 后面的 tab
            for (int i = tabCount - 1; i > index; i--) {
                mTabLayout.removeTabAt(i);
            }

            //重置
            mCurrentSource = iNames;
            for (int i = FIX_ADDRESS_ITEM_COUNT - 1; i >= index; i--) {
                mAddressItems[i] = null;
            }
            tab.setText(R.string.please_select);
            showNext(false);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    private void onSelectCompleted() {
        if (mAddressListener != null) {
            mAddressListener.onSelectedAddress(
                    getAddressNameByIndex(1),
                    getAddressNameByIndex(2),
                    getAddressNameByIndex(3)
            );
        }
        dismiss();
    }

    private final View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IName iName = (IName) v.getTag();
            if (iName == null) {
                return;
            }

            List<IName> children = iName.getChildren();
            IName.AddressToken name = iName.getAddressToken();
            TabLayout.Tab tabAt = mTabLayout.getTabAt(mTabLayout.getTabCount() - 1);
            setSelectedItem(name);
            assert tabAt != null;
            tabAt.setText(name.getName());

            if (children != null && !children.isEmpty()) {
                mCurrentSource = children;
                showNext(true);
            } else {
                onSelectCompleted();
            }
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setWindowAnimations(R.style.AppAnimation_BottomIn);
            dialogWindow.setAttributes(lp);
        }
        return dialog;
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mAddressInquirers != null) {
            mAddressInquirers.destroy();
        }
    }

    private void setSelectedItem(IName.AddressToken token) {
        int index = -1;
        if (token.getIdentifying() == IName.AddressToken.COUNTRY) {
            index = 0;
        } else if (token.getIdentifying() == IName.AddressToken.PROVINCE) {
            index = 1;
        } else if (token.getIdentifying() == IName.AddressToken.CITY) {
            index = 2;
        } else if (token.getIdentifying() == IName.AddressToken.AREA) {
            index = 3;
        }
        if (index != -1) {
            mAddressItems[index] = token;
        }
    }

    private String getAddressNameByIndex(int index) {
        IName.AddressToken token = mAddressItems[index];
        return token == null ? "" : token.getName();
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

}