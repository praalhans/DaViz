package com.aexiz.daviz.simulation;

import java.util.ArrayList;

class OrderedSetList<T> extends ArrayList<T> {

    private static final long serialVersionUID = -7310476084643000609L;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (T c : this) {
            if (first) first = false;
            else sb.append(',');
            sb.append(c);
        }
        sb.append('}');
        return sb.toString();
    }

}
