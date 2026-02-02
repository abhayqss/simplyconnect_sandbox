package com.scnsoft.eldermark.merger.duke.comparator;

import no.priv.garshol.duke.Comparator;
import org.apache.commons.lang3.StringUtils;

public class SsnComparator implements Comparator {

    @Override
    public boolean isTokenized() {
        return false;
    }

    @Override
    public double compare(String s1, String s2) {
        if (StringUtils.isBlank(s1) || StringUtils.isBlank(s2) ||  s1.length()!=s2.length() || s1.length()!=9){
            return 0.0;
        }
        if (s1.equals(s2)){
            return 1.0;
        }
        char[] s1Array = s1.toCharArray();
        char[] s2Array = s2.toCharArray();

        int diff=0;
        boolean nghbr= false;
        for (int i=0;i<9;i++){
            if(s1Array[i]!=s2Array[i]) {
                diff++;
            }
            if (diff==2){
                if (s1Array[i-1]==s2Array[i] && s2Array[i-1]==s1Array[i]){
                    nghbr= true;
                }
                break;
            }
        }
        switch (diff){
            case 0: return 1.0;
            case 1: return 0.54;   //53 //56
            case 2: if (nghbr) return 0.5;
            default:return 0.0;
        }
    }
}
