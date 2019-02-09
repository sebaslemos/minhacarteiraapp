package br.com.sbsistemas.minhacarteira;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;

import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.exception.MinhaCarteiraInputException;
import br.com.sbsistemas.minhacarteira.helpers.FormularioReceitaHelper;
import br.com.sbsistemas.minhacarteira.modelo.Receita;

public class FormularioReceiraActivity extends AppCompatActivity {

    private FormularioReceitaHelper helper;
    private TextView dataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_receira);

        helper = new FormularioReceitaHelper(this);
        dataText = (TextView) findViewById(R.id.formulario_receita_data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_receita, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            ControladorReceitas controladorReceitas = new ControladorReceitas(this);
            Receita receita = helper.pegaReceitaDoFormulario();
            controladorReceitas.adicionarReceita(receita);
            Toast.makeText(this, "Receita adicionada com sucesso", Toast.LENGTH_LONG).show();
            finish();
        } catch (MinhaCarteiraInputException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void selecionaData(View v){
        final LocalDate data = LocalDate.now();

        DatePickerDialog pickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String mesFormatado = month < 10 ? "0" + month : month + "";
                dataText.setText(mesFormatado + "/" + year);
            }
        }, data.getYear(), data.getMonthOfYear() - 1, data.getDayOfMonth());

        pickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        pickerDialog.show();
    }


}
