package me.ztiany.android.apicker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okio.BufferedSource;
import okio.Okio;

final class AddressLoader {

    private static final String CHINA_ADDRESS_PATH = "address.json";

    private final AddressQueryCallback mAddressQueryCallback;

    private final Context mContext;

    public AddressLoader(@NonNull Context context, AddressQueryCallback addressQueryCallback) {
        mContext = context;
        mAddressQueryCallback = addressQueryCallback;
    }

    public void start() {
        if (mAddressQueryCallback != null) {
            AppExecutor.runOnIO(() -> {
                List<IName> names = new ArrayList<>(formJson(getAddressJson()));
                AppExecutor.runOnMain(() -> mAddressQueryCallback.onGetAddress(names));
            });
        }
    }

    interface AddressQueryCallback {
        void onGetAddress(List<IName> names);
    }

    private String getAddressJson() {
        try {
            return convertToString(mContext.getAssets().open(CHINA_ADDRESS_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private List<AddressItem> formJson(String content) {
        final Type type = new TypeToken<List<AddressItem>>() {
        }.getType();
        return new Gson().fromJson(content, type);
    }

    void destroy() {
        //TODO: cancel the loading.
    }

    @WorkerThread
    private String convertToString(InputStream stream) {
        try (BufferedSource source = Okio.buffer(Okio.source(stream))) {
            return source.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}