package br.edu.es.belmiro.internetbtp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Lançador de uso exclusivo dos Quest da EEEFM Belmiro Teixeira Pimenta.
 * Não incorpora navegador nem coleta dados de usuários.
 * Apenas solicita ao sistema Meta Horizon OS a abertura de seu navegador nativo.
 *
 * Documentação Meta: https://developers.meta.com/vr/documentation/android-apps/ps-system-deep-linking/
 * Deep link: systemux://browser
 * ATENÇÃO: A compatibilidade de chamadas entre apps NO MODO PARTILHADO ainda deve
 * ser confirmada empiricamente em um Quest gerenciado antes de expandir para 30.
 */
public class MainActivity extends Activity {
    private TextView status;
    private static final String HOME_URL = "https://www.google.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createScreen();
        // O próprio toque no ícone é suficiente: tente abrir o Browser automaticamente.
        getWindow().getDecorView().post(this::openQuestBrowser);
    }

    private void createScreen() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(40, 28, 40, 28);
        box.setBackgroundColor(Color.rgb(14, 29, 65));

        TextView title = new TextView(this);
        title.setText("INTERNET – BTP");
        title.setTextSize(34);
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        box.addView(title);

        TextView info = new TextView(this);
        info.setText("Abrindo o navegador nativo do Meta Quest...");
        info.setTextSize(19);
        info.setTextColor(Color.rgb(189, 231, 241));
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 22, 0, 16);
        box.addView(info);

        Button retry = new Button(this);
        retry.setText("ABRIR NAVEGADOR");
        retry.setOnClickListener(v -> openQuestBrowser());
        box.addView(retry);

        status = new TextView(this);
        status.setText("Se a tela não mudar, pressione ABRIR NAVEGADOR.");
        status.setTextSize(15);
        status.setTextColor(Color.LTGRAY);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 22, 0, 0);
        box.addView(status);

        setContentView(box);
    }

    private void openQuestBrowser() {
        try {
            PackageManager pm = getPackageManager();
            // Método recomendado na documentação oficial Meta (System Deep Linking).
            Intent intent = pm.getLaunchIntentForPackage("com.oculus.vrshell");
            if (intent == null) {
                // Alternativa explícita para versões sem activity LAUNCHER consultável.
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setClassName("com.oculus.vrshell", "com.oculus.vrshell.MainActivity");
                intent.setData(android.net.Uri.parse("systemux://browser"));
            }
            intent.putExtra("intent_data", "systemux://browser");
            intent.putExtra("uri", HOME_URL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            status.setText("Comando enviado ao sistema. Se o navegador não abrir, "
                    + "o Modo Partilhado pode estar restringindo o atalho.");
        } catch (Exception ex) {
            status.setText("Não foi possível abrir o navegador: "
                    + ex.getClass().getSimpleName()
                    + ". Use o atalho por imagem enquanto testamos.");
        }
    }
}
