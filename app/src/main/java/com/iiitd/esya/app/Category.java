package com.iiitd.esya.app;

import android.util.Log;

/**
 * Created by darkryder on 28/6/15.
 */
public enum Category {
    CSE(1, "CSE"),
    ECE(2, "ECE"),
    FLAGSHIP(4, "Flagship"),
    NON_TECH(5, "Non Tech"),
    SCHOOL(3, "School"),
    WORKSHOP(6, "Workshop"),
    TECHATHLON(7, "Techathlon"),
    ALL(0, "All");

    String naturalName;
    final int id;
    private Category(int id, String naturalName){ this.id = id; this.naturalName = naturalName; }

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

    public static Category resolveToCategory(int id){
        Category result;
        switch (id)
        {
            case 1:
                result = CSE; break;
            case 2:
                result = ECE; break;
            case 3:
                result = SCHOOL; break;
            case 4:
                result = FLAGSHIP; break;
            case 5:
                result = NON_TECH; break;
            case 6:
                result = WORKSHOP; break;
            case 7:
                result = TECHATHLON; break;
            default:
                result = CSE;
                Log.v("resolveToCategory", "No category found for " + id +". Reverting back to CSE");
        }
        return result;
    }
}
