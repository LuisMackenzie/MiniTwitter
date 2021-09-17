package com.example.minitwitter.Activities.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.data.TweetViewModel;

public class NuevoTweetDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageView ivClose, ivAvatar;
    private Button btnTwittear;
    private EditText etMensaje;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        // dialogNuevoTweet = getDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.nuevo_tweet, container, false);
        ivClose = view.findViewById(R.id.iv_close);
        ivAvatar = view.findViewById(R.id.iv_avatar_new_tweet);
        btnTwittear = view.findViewById(R.id.btn_tweetear);
        etMensaje = view.findViewById(R.id.et_mensaje_new_tweet);

        // Eventos
        btnTwittear.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        // Seteamos la imagen de perfil de usuario
        String photoUrl = SharedPreferencesManager.getStringValue(Constantes.PREF_PHOTOURL);
        if (!photoUrl.isEmpty()) {
            Glide.with(getActivity())
                    .load(Constantes.API_MINITWITTER_FILES + photoUrl)
                    .into(ivAvatar);
        }

        return view;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String mensaje = etMensaje.getText().toString();

        switch (id) {
            case R.id.iv_close:
                if (!mensaje.isEmpty()) {
                    showDialogConfirm();
                } else {
                    getDialog().dismiss();
                }

                break;
            case R.id.btn_tweetear:
                if (!mensaje.isEmpty()) {
                    TweetViewModel model = ViewModelProviders.of(getActivity()).get(TweetViewModel.class);
                    model.insertTweet(mensaje);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(MyApp.getContext(), "No se puede Crear un tweet vacio!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void showDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cancelar Tweet").setMessage("Desea realmente eliminar el tweet?");

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getDialog().dismiss();
                Toast.makeText(MyApp.getContext(), "Eliminaste el tweet con exito", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MyApp.getContext(), "Puedes seguir escribiendo", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
