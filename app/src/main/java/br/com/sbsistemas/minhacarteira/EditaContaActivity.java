package br.com.sbsistemas.minhacarteira;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.helpers.EditaContaHelper;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

public class EditaContaActivity extends AppCompatActivity {

    private EditaContaHelper helper;
    private TextView categoriaText;
    private TextView grupoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_conta);

        Conta conta = (Conta) getIntent().getExtras().get("conta");

        helper = new EditaContaHelper(this, conta);
        inicializaComponentes();
    }

    private void inicializaComponentes() {
        categoriaText = (TextView) findViewById(R.id.edita_conta_categoria);
        grupoText = (TextView) findViewById(R.id.edita_conta_grupo);
    }

    public void selecionaCategoria(View v){
        Intent intent = new Intent(this, FormularioCategoriaActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_conta, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Categoria categoria = (Categoria) data.getExtras().getSerializable("CategoriaClicada");
        categoriaText.setText(categoria.getDescricao());

        ControladorGrupo controladorGrupo = new ControladorGrupo(this);
        Grupo grupo = controladorGrupo.getGrupo(categoria.getIdGrupo());
        grupoText.setText(grupo.getDescricao());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //só item de confirmar por enquanto
        Conta conta = helper.recuperaConta();
        ControladorConta controladorConta = new ControladorConta(this);
        try{
            controladorConta.atualizaConta(conta);
            finish();
            Toast.makeText(this, "Conta " + conta.getDescricao() + " atualizada com sucesso.", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
