package br.com.sbsistemas.minhacarteira.dao;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.sbsistemas.minhacarteira.modelo.Grupo;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GrupoTest {

    private GrupoDAO dao;

    @Before
    public void iniciaAmbiente(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        dao = new GrupoDAO(appContext);
    }

    @After
    public void limpaAmbiente(){
        dao.close();
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("br.com.sbsistemas.minhacarteira", appContext.getPackageName());
    }

    @Test
    public void testaCriacaoGruposPadrao(){
        List<Grupo> grupos = dao.recuperaTodos();
        assertEquals(7, grupos.size());
    }

    @Test
    public void testaCreacaoDosNomesDosGrupos(){
        List<Grupo> grupos = dao.recuperaTodos();
        assertEquals("Todas", grupos.get(0).getDescricao());
    }

}
