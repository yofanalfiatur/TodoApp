package com.haphap.todoapp.Feature.Task;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haphap.todoapp.R;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        AddTaskFragment addTaskFragment = (AddTaskFragment) getSupportFragmentManager().findFragmentById(R.id.add_task_fragment_container);
        if (addTaskFragment == null) {
            addTaskFragment = AddTaskFragment.createNewInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.add_task_fragment_container, addTaskFragment);
            transaction.commit();
        }
    }
}
