package com.iiitd.esya.app;

import android.util.Log;

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
        // Creating a map might not be justifiable for such a small input. Array linear search
        // is efficient enough.
        for(Category c: values()){
            if (what.equals(c.toString())){
                return c;
            }
        }
        Log.v("resolveToCategory", "No category found for " + what +". Reverting back to CSE");
        return CSE;
    }
}
