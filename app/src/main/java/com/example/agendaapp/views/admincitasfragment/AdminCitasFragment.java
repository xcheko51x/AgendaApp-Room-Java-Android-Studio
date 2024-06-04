package com.example.agendaapp.views.admincitasfragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agendaapp.MainActivity;
import com.example.agendaapp.R;
import com.example.agendaapp.database.AppDatabase;
import com.example.agendaapp.databinding.FragmentAdminCitasBinding;
import com.example.agendaapp.models.Cita;
import com.example.agendaapp.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class AdminCitasFragment extends Fragment implements CitasAdapter.OnItemClicked {
    FragmentAdminCitasBinding binding;
    Cita cita = new Cita();
    Boolean isValido = false;
    Boolean isEditando = false;
    List<Cita> listaCitas = new ArrayList<>();
    CitasAdapter citasAdapter = new CitasAdapter(listaCitas, this);
    AppDatabase db;

    public AdminCitasFragment() {  }

    public static AdminCitasFragment newInstance(String param1, String param2) {
        AdminCitasFragment fragment = new AdminCitasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminCitasBinding.inflate(getLayoutInflater());
        View vista = binding.getRoot();

        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Administra tus Citas");

        db = new Utils().getAppDatabase(getContext());

        setupToolbarMenu();
        obtenerCitas();

        binding.svCliente.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarCliente(newText);
                return false;
            }
        });

        return vista;
    }

    private void filtrarCliente(String texto) {
        ArrayList<Cita> listaFiltrada = new ArrayList();
        for (Cita cita : listaCitas) {
            if (cita.nomCliente.toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(cita);
            }
        }
        citasAdapter.filtrarCliente(listaFiltrada);
    }

    public void setupToolbarMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_agregar) {
                    lanzarAlertDialogCita(getActivity());
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    public void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvCitas.setLayoutManager(layoutManager);
        citasAdapter = new CitasAdapter(listaCitas, this);
        binding.rvCitas.setAdapter(citasAdapter);
    }

    public void validarCampos() {
        if (
                cita.nomCliente.isEmpty()
                || cita.telCliente.isEmpty()
                || cita.horaCita.isEmpty()
                || cita.diaCita.contains("*")
        ) {
            isValido = false;
        } else {
            isValido = true;
        }
    }

    public void obtenerHora(TextView tvHora) {
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            //Formateo el hora obtenido: antepone el 0 si son menores de 10
            String horaFormateada =  (hourOfDay < 10)? "0" + hourOfDay : String.valueOf(hourOfDay);
            //Formateo el minuto obtenido: antepone el 0 si son menores de 10
            String minutoFormateado = (minute < 10)? "0" + minute :String.valueOf(minute);
            //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
            /*String AM_PM;
            if(hourOfDay < 12) {
                AM_PM = " AM";
            } else {
                AM_PM = " PM";
            }*/
            //Muestro la hora con el formato deseado
            //etHora.setText(horaFormateada + ":" + minutoFormateado + " " + AM_PM);
            tvHora.setText(horaFormateada + ":" + minutoFormateado);
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);

        recogerHora.show();
    }

    public void lanzarAlertDialogCita(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View vista = inflater.inflate(R.layout.alert_dialog_add_update_cita, null);
        builder.setView(vista);
        builder.setCancelable(false);


        EditText etNomCliente, etTelCliente, etAsuntoCita;
        TextView tvTituloAlert, tvHora;
        ImageButton ibtnHora;
        Spinner spiDias;

        etNomCliente = vista.findViewById(R.id.etNomCliente);
        etTelCliente = vista.findViewById(R.id.etTelCliente);
        etAsuntoCita = vista.findViewById(R.id.etAsuntoCita);
        tvTituloAlert = vista.findViewById(R.id.tvTituloAlert);
        tvHora = vista.findViewById(R.id.tvHora);
        ibtnHora = vista.findViewById(R.id.ibtnHora);
        spiDias = vista.findViewById(R.id.spiDias);


        String[] listaDias = activity.getResources().getStringArray(R.array.dias_semana);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.item_spinner, listaDias);
        spiDias.setAdapter(arrayAdapter);

        if (isEditando) {
            tvTituloAlert.setText("ACTUALIZAR CITA");
            etNomCliente.setText(cita.nomCliente);
            etTelCliente.setText(cita.telCliente);
            etAsuntoCita.setText(cita.asuntoCita);
            tvHora.setText(cita.horaCita);
            spiDias.setSelection(arrayAdapter.getPosition(cita.diaCita));
        }

        ibtnHora.setOnClickListener( view -> {
            obtenerHora(tvHora);
        });

        builder.setPositiveButton("Aceptar", (dialogInterface, i) -> {
            if (!isEditando) {
                cita.idCita = String.valueOf(System.currentTimeMillis());
            }

            cita.nomCliente = etNomCliente.getText().toString().trim();
            cita.telCliente = etTelCliente.getText().toString().trim();
            cita.asuntoCita = etAsuntoCita.getText().toString().trim();
            cita.horaCita = tvHora.getText().toString().trim();
            cita.diaCita = spiDias.getSelectedItem().toString();

            validarCampos();
            if (isValido) {
                if (isEditando) {
                    actualizarCita();
                    isEditando = false;
                } else {
                    agregarCita();
                }

            } else {
                Toasty.error(getContext(), "Faltaron por llenar campos obligatorios", Toast.LENGTH_SHORT, true).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialogInterface, i) -> {
            isEditando = false;
        });

        builder.create();
        builder.show();
    }

    public void obtenerCitas() {
        AsyncTask.execute( () -> {
            //AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, DB_NAME).build();
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread(() -> setupRecyclerView());
        });
    }

    public void agregarCita() {
        AsyncTask.execute( () -> {
            //AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, DB_NAME).build();
            db.citaDao().agregarCita(cita);
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread(() -> setupRecyclerView());
        });
    }

    public void actualizarCita() {
        AsyncTask.execute( () -> {
            //AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, DB_NAME).build();
            db.citaDao().actualizarCita(cita);
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread(() -> setupRecyclerView());
        });
    }

    @Override
    public void editarCita(Cita cita) {
        isEditando = true;
        this.cita = cita;
        lanzarAlertDialogCita(getActivity());
    }

    @Override
    public void borrarCita(Cita cita) {
        AsyncTask.execute( () -> {
            //AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, DB_NAME).build();
            db.citaDao().eliminarCita(cita);
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread(() -> setupRecyclerView());
        });
    }
}

