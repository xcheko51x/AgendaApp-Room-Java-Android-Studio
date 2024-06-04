package com.example.agendaapp.views.agendafragment;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.agendaapp.MainActivity;
import com.example.agendaapp.R;
import com.example.agendaapp.database.AppDatabase;
import com.example.agendaapp.databinding.FragmentAgendaBinding;
import com.example.agendaapp.models.Cita;
import com.example.agendaapp.utils.Utils;
import com.example.agendaapp.views.admincitasfragment.CitasAdapter;

import java.util.ArrayList;
import java.util.List;

public class AgendaFragment extends Fragment {

    FragmentAgendaBinding binding;
    List<Cita> listaCitas = new ArrayList<>();
    List<Cita> listaCitasLunes = new ArrayList<>();
    List<Cita> listaCitasMartes = new ArrayList<>();
    List<Cita> listaCitasMiercoles = new ArrayList<>();
    List<Cita> listaCitasJueves = new ArrayList<>();
    List<Cita> listaCitasViernes = new ArrayList<>();
    List<Cita> listaCitasSabado = new ArrayList<>();
    List<Cita> listaCitasDomingo = new ArrayList<>();
    CitasDiaAdapter citasDiaAdapter;
    AppDatabase db;

    public AgendaFragment() { }

    public static AgendaFragment newInstance(String param1, String param2) {
        AgendaFragment fragment = new AgendaFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAgendaBinding.inflate(getLayoutInflater());
        View vista = binding.getRoot();

        ((MainActivity) getActivity()).getSupportActionBar().hide();

        db = new Utils().getAppDatabase(getContext());

        obtenerTodasCitas();

        return vista;
    }

    public void obtenerCitasDia() {
        for(Cita cita : listaCitas) {
            switch (cita.diaCita) {
                case "Lunes":
                    listaCitasLunes.add(cita);
                    setupRecyclerView(binding.rvCitasLunes, listaCitasLunes);
                    break;
                case "Martes":
                    listaCitasMartes.add(cita);
                    setupRecyclerView(binding.rvCitasMartes, listaCitasMartes);
                    break;
                case "Miercoles":
                    listaCitasMiercoles.add(cita);
                    setupRecyclerView(binding.rvCitasMiercoles, listaCitasMiercoles);
                    break;
                case "Jueves":
                    listaCitasJueves.add(cita);
                    setupRecyclerView(binding.rvCitasJueves, listaCitasJueves);
                    break;
                case "Viernes":
                    listaCitasViernes.add(cita);
                    setupRecyclerView(binding.rvCitasViernes, listaCitasViernes);
                    break;
                case "Sabado":
                    listaCitasSabado.add(cita);
                    setupRecyclerView(binding.rvCitasSabado, listaCitasSabado);
                    break;
                case "Domingo":
                    listaCitasDomingo.add(cita);
                    setupRecyclerView(binding.rvCitasDomingo, listaCitasDomingo);
                    break;
                default:
                    break;
            }
        }
    }

    public void setupRecyclerView(RecyclerView rvDia, List<Cita> listaDia) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvDia.setLayoutManager(layoutManager);
        citasDiaAdapter = new CitasDiaAdapter(listaDia);
        rvDia.setAdapter(citasDiaAdapter);
    }

    public void obtenerTodasCitas() {
        AsyncTask.execute( () -> {
            //AppDatabase db = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, DB_NAME).build();
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread( () -> {
                obtenerCitasDia();
            });
        });
    }
}