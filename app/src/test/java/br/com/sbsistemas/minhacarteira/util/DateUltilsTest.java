package br.com.sbsistemas.minhacarteira.util;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 13/08/2017.
 */

public class DateUltilsTest {

    @Test
    public void testaCriacaoDeDataAtual(){
        LocalDateUtils dataUtil = new LocalDateUtils(null);
        LocalDate hoje = dataUtil.getHoje();

        LocalDate dataEsperada = new LocalDate();
        Assert.assertEquals(dataEsperada.getDayOfMonth(), hoje.getDayOfMonth());
        Assert.assertEquals(dataEsperada.getMonthOfYear(), hoje.getMonthOfYear());
        Assert.assertEquals(dataEsperada.getYear(), hoje.getYear());

        dataUtil = new LocalDateUtils(new LocalDate(2017, 1, 1), null);
        hoje = dataUtil.getHoje();
        Assert.assertEquals(1, hoje.getDayOfMonth());
        Assert.assertEquals(1, hoje.getMonthOfYear());
        Assert.assertEquals(2017, hoje.getYear());
    }

}
