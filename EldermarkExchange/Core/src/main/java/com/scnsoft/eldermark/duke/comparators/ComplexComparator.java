//package com.scnsoft.eldermark.duke.comparators;
//
//import no.priv.garshol.duke.Comparator;
//import no.priv.garshol.duke.comparators.ExactComparator;
//import no.priv.garshol.duke.comparators.Levenshtein;
//import no.priv.garshol.duke.comparators.SoundexComparator;
//
///**
// * Created by knetkachou on 1/20/2017.
// */
//public class ComplexComparator implements Comparator {
//
//    private Comparator subcomp1;
//    private Comparator subcomp2;
//
//    public ComplexComparator() {
//        this.subcomp1 = new Levenshtein();
//        this.subcomp2 = new SoundexComparator();
//    }
//
//    public void setComparator1(Comparator comp) {
//        this.subcomp1 = comp;
//    }
//
//    @Override
//    public boolean isTokenized() {
//        return false;
//    }
//
//    @Override
//    public double compare(String v1, String v2) {
//        return 0;
//    }
//}
