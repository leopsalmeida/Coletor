package br.halley.pauer.coletorandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor; // para manipular os dados
import android.database.sqlite.SQLiteDatabase; // banco dados do android
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends Activity
{
    String x, pesquisa_codigo, pesquisa, pesquisaquant, localiza, sql, sql2, format, contents, NomeBanco = "BancoColetor";
    SQLiteDatabase BancoDados = null;
    EditText CodigoBarra, NomeProduto, QuantidadeProduto;
    Button Salvar, SalvarQuant, Scan, Cadastrar, Lista, Voltar, Pesquisar;
    Cursor cursor, c, c2;
    ListView MostraDados;
    SimpleCursorAdapter AdapterLista;

    static int id = -1;

    public static final String KEY_CODIGOBARRA = "codigobarra";
    public static final String KEY_NOMEPRODUTO = "nomeproduto";
    public static final String KEY_QUANTIDADEPRODUTO = "quantidade";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_principal);

        Cadastrar = (Button) findViewById(R.id.btnCadastrar);
        Lista = (Button) findViewById(R.id.btnLista);

        Cadastrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chamaCadastro();
            }
        });

        Lista.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chamaLista();
            }
        });
    }

    public void chamaLista()
    {
        setContentView(R.layout.tela_contagem);

        Voltar = (Button) findViewById(R.id.btnVoltar);
        Voltar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MenuPrincipal();
            }
        });

        CarregaDado();

    }

    public void MenuPrincipal()
    {
        setContentView(R.layout.tela_principal);

        Cadastrar = (Button) findViewById(R.id.btnCadastrar);
        Lista = (Button) findViewById(R.id.btnLista);

        Cadastrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chamaCadastro();
            }
        });

        Lista.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chamaLista();
            }
        });
    }

    public void chamaCadastro()
    {
        setContentView(R.layout.activity_main);

        CodigoBarra = (EditText) findViewById(R.id.etCodProd);
        Scan = (Button) findViewById(R.id.btnScan);
        NomeProduto = (EditText) findViewById(R.id.etNomeProd);
        Salvar = (Button) findViewById(R.id.btnSalvar);
        QuantidadeProduto = (EditText) findViewById(R.id.etQuantidade);
        Pesquisar = (Button) findViewById(R.id.btnPesquisar);
        Voltar = (Button) findViewById(R.id.btnVoltar);

        Voltar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MenuPrincipal();
            }
        });

        Pesquisar.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    pesquisa_codigo = CodigoBarra.getText().toString();
                    BancoDados = openOrCreateDatabase(NomeBanco, MODE_WORLD_READABLE, null);
                    c2 = BancoDados.query("tabcadastroproduto2", new String[]{"_id", "codigobarra", "nomeproduto", "quantidade"}, "codigobarra = '" + pesquisa_codigo + "'", null, null, null, null);
                    c2.moveToFirst();
                    pesquisa = c2.getString(2);
                    NomeProduto.setText(pesquisa);
                    pesquisaquant = c2.getString(3);
                    QuantidadeProduto.setText(pesquisaquant);
                }catch(Exception erro)
                    {
                        MensagemAlerta("Alerta", "Informe um código de barras cadastrado!");
                        NomeProduto.setText("");
                        CodigoBarra.setText("");
                        QuantidadeProduto.setText("");
                    }
            }
        });

        CarregaDado();
        CriaBanco();
        btnScanner();
        btnSalvarDados();

        MostraDados.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int posicao, long arg3)
            {
                id = (posicao+1);
                SetarDados(id);
            }
        });
    }

    public void SetarDados(int posicao)
    {
        BancoDados = openOrCreateDatabase(NomeBanco, MODE_WORLD_READABLE, null);
        c = BancoDados.rawQuery("SELECT * FROM tabcadastroproduto2 WHERE _id = '"+posicao+"'", null);

        while(c.moveToNext())
        {
            NomeProduto.setText(c.getString(c.getColumnIndex("nomeproduto")));
            CodigoBarra.setText(c.getString(c.getColumnIndex("codigobarra")));
            QuantidadeProduto.setText(c.getString(c.getColumnIndex("quantidade")));
        }
    }

    public void CriaBanco()
    {
        try
        {
            BancoDados = openOrCreateDatabase(NomeBanco, MODE_WORLD_READABLE, null);
            String SQL = "CREATE TABLE IF NOT EXISTS tabcadastroproduto2 (_id INTEGER PRIMARY KEY, codigobarra TEXT, nomeproduto TEXT, quantidade TEXT)";

            BancoDados.execSQL(SQL);
            if(!VerificaRegistro())
            {
                //MensagemAlerta("Banco de Dados", "Banco criado com sucesso!");
            }
        }catch(Exception erro)
            {
                //MensagemAlerta("Erro Banco de Dados", "Nao foi possivel criar o banco!" + erro);
            }
        finally
        {
            BancoDados.close();
        }
    }

    public boolean AlterarRegistro(int id)
    {
        BancoDados = openOrCreateDatabase(NomeBanco, MODE_WORLD_WRITEABLE, null);
        //sql = "UPDATE tabcadastroproduto2 SET nomeproduto = '"+NomeProduto.getText().toString()+"'," + "codigobarra = '"+CodigoBarra.getText().toString()+ "' WHERE _id = '"+id+"'";
        sql = "UPDATE tabcadastroproduto2 SET nomeproduto = '"+NomeProduto.getText().toString()+"'," + "codigobarra = '"+CodigoBarra.getText().toString()+ "'," + "quantidade = '"+QuantidadeProduto.getText().toString()+ "' WHERE _id = '"+id+"'";
        BancoDados.execSQL(sql);
        return true;
    }

    public void GravaBanco(int posicao)
    {
        if(posicao == -1)
        {
            try
            {
                BancoDados = openOrCreateDatabase(NomeBanco, MODE_WORLD_READABLE, null);
                String SQL = "INSERT INTO tabcadastroproduto2 (codigobarra, nomeproduto, quantidade) VALUES ('"+CodigoBarra.getText().toString()+"','"+NomeProduto.getText().toString()+"','"+QuantidadeProduto.getText().toString()+"')";
                BancoDados.execSQL(SQL);
                MensagemAlerta("Banco de Dados", "Registro gravado com sucesso!");
            }catch (Exception erro)
                {
                    MensagemAlerta("Erro Banco de Dados", "Não foi possível gravar o registro!");
                }
            finally
            {
                BancoDados.close();
            }
        }
        else
        {
            AlterarRegistro(posicao);
        }
    }

    private boolean VerificaRegistro()
    {
        try
        {
            BancoDados = openOrCreateDatabase(NomeBanco, MODE_WORLD_READABLE, null);
            cursor = BancoDados.rawQuery("Select * from tabcadastroproduto2", null);

            if(cursor.getCount() != 0)
            {
                cursor.moveToFirst();
                return true;
            }
            else
            {
                return false;
            }
        }catch (Exception erro)
            {
                //MensagemAlerta("Erro Banco de Dados", "Não foi possivél verificar dados!" +erro);
                return false;
            }
        finally
        {
            BancoDados.close();
        }
    }

    public void CarregaDado()
    {
        MostraDados = (ListView) findViewById(R.id.lvMostraDados);

        if(VerificaRegistro())
        {
            String [] Coluna = new String[] {KEY_NOMEPRODUTO};
            AdapterLista = new SimpleCursorAdapter(this, R.layout.mostrabanco, cursor, Coluna, new int[] {R.id.tvCarregaDado});
            MostraDados.setAdapter(AdapterLista);
        }
        else
        {
            //MensagemAlerta("Erro Banco de Dados", "Não foi possivél verificar dados!");
        }
    }

    public void btnScanner()
    {
        Scan.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intent, 0);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == 0)
        {
            if(resultCode == RESULT_OK)
            {
                contents = intent.getStringExtra("SCAN_RESULT");
                format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                CodigoBarra.setText(contents);
            }
        }
    }

    public void btnSalvarDados()
    {
        Salvar.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                if (CodigoBarra.getText().toString().equals("") || NomeProduto.getText().toString().equals(""))
                {
                    //MensagemAlerta("Erro", "Campo obrigatorio vazio!");
                    return;
                }
                GravaBanco(id);
                CarregaDado();
                id = -1;
                NomeProduto.setText("");
                CodigoBarra.setText("");
                QuantidadeProduto.setText("");
            }
        });
    }

    public void MensagemAlerta(String TituloAlerta, String MensagemAlerta)
    {
        AlertDialog.Builder Mensagem = new AlertDialog.Builder(MainActivity.this);
        Mensagem.setTitle(TituloAlerta);
        Mensagem.setMessage(MensagemAlerta);
        Mensagem.setNeutralButton("OK", null);
        Mensagem.show();
    }

}
