package br.com.sbsistemas.minhacarteira.utils.graficos;


import com.github.mikephil.charting.data.Entry;

public class EntryYComparator<entry> implements java.util.Comparator<com.github.mikephil.charting.data.Entry> {

    @Override
    public int compare(Entry o1, Entry o2) {
        if(o1.getX() < o2.getX()) return -1;
        if(o1.getX() > o2.getX()) return 1;
        return 0;
    }
}