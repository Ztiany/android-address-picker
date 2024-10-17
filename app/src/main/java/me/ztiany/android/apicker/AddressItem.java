package me.ztiany.android.apicker;


import java.util.ArrayList;
import java.util.List;

class AddressItem implements IName {

    private AddressToken name;

    private List<AddressItem> children;

    public AddressToken getAddressToken() {
        return name;
    }

    public List<IName> getChildren() {
        if (children == null || children.isEmpty()) {
            return null;
        }
        return new ArrayList<>(children);
    }

    public void setChildren(List<AddressItem> children) {
        this.children = children;
    }

    public void setName(AddressToken name) {
        this.name = name;
    }

}