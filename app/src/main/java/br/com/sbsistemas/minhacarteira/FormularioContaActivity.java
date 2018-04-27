package br.com.sbsistemas.minhacarteira;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;

import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.helpers.FormularioContaHelper;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

public class FormularioContaActivity extends AppCompatActivity {


    private FormularioContaHelper helper;
    private TextView categoriaText;
    private TextView grupoText;
    private TextView dataText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_conta);

        helper = new FormularioContaHelper(this);

        inicializaComponentes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_conta, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //só item de confirmar por enquanto
        Conta conta = helper.pegaConta();
        ControladorConta controladorConta = new ControladorConta(this);
        try{
            controladorConta.criarConta(conta, helper.getPago(), helper.getData(), helper.getAtiva());
            finish();
            Toast.makeText(this, "Conta " + conta.getDescricao() + " adicionada com sucesso.", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializaComponentes() {
        categoriaText = (TextView) findViewById(R.id.formulario_conta_categoria);
        grupoText = (TextView) findViewById(R.id.formulario_conta_grupo);
        dataText = (TextView) findViewById(R.id.formulario_conta_data);
    }

    public void selecionaCategoria(View v){
        Intent intent = new Intent(FormularioContaActivity.this, FormularioCategoriaActivity.class);
        startActivityForResult(intent, 1);
    }

    public void selecionaData(View v){
        final LocalDate data = LocalDate.now();

        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String diaFormatado = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
                String mesFormatado = month < 10 ? "0" + month : month + "";
                dataText.setText(diaFormatado + "/" + mesFormatado + "/" + year);
            }
        }, data.getYear(), data.getMonthOfYear() - 1, data.getDayOfMonth());
        pickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Categoria categoria = (Categoria) data.getExtras().getSerializable("CategoriaClicada");
        categoriaText.setText(categoria.getDescricao());

        ControladorGrupo controladorGrupo = new ControladorGrupo(this);
        Grupo grupo = controladorGrupo.getGrupo(categoria.getIdGrupo());
        grupoText.setText(grupo.getDescricao());
    }
}
