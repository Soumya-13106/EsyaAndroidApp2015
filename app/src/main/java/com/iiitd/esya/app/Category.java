package com.iiitd.esya.app;

/**
 * Created by darkryder on 28/6/15.
 */
public enum Category {
    CSE("CSE"),
    ECE("ECE"),
    FLAGSHIP("Flagship"),
    NON_TECH("Non Tech"),
    SCHOOL("School"),
    WORKSHOP("Workshop"),
    ALL("All");

    String naturalName;
    private Category(String naturalName){
        this.naturalName = naturalName;
    }

    @Override
    public String toString() {
        return this.naturalName;
    }

    public static Category resolveToCategory(String what){
        for(Category c: values()){
            if (what.equals(c.toString())){
                return c;
            }
        }
        return null;
    }

}
