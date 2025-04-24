package com.example.taskmanagerapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import java.util.List;
import android.graphics.Color;
import android.graphics.Typeface;

public class MainActivity extends AppCompatActivity {
    Button add;
    AlertDialog dialog;
    LinearLayout layout;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        layout = findViewById(R.id.container);
        databaseHelper = new DatabaseHelper(this);

        loadTasks();
        buildDialog();
        add.setOnClickListener(v -> dialog.show());
    }

    private void loadTasks() {
        layout.removeAllViews();
        List<Task> tasks = databaseHelper.getAllTasks();
        for (Task task : tasks) {
            addCard(task.getId(), task.getName(), task.getDescription(), task.getDueDate());
        }
    }

    public void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText name = view.findViewById(R.id.nameEdit);
        final EditText description = view.findViewById(R.id.descriptionEdit);
        final EditText dueDate = view.findViewById(R.id.dueDateEdit);

        // Custom title with color and style
        TextView customTitle = new TextView(this);
        customTitle.setText("Enter your task");
        customTitle.setTextSize(20);
        customTitle.setPadding(40, 30, 40, 10);
        customTitle.setTextColor(Color.parseColor("#4CAF50")); // Green
        customTitle.setTypeface(null, Typeface.BOLD);

        builder.setCustomTitle(customTitle);
        builder.setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String taskName = name.getText().toString().trim();
                    String taskDescription = description.getText().toString().trim();
                    String taskDueDate = dueDate.getText().toString().trim();

                    if (validateInputs(taskName, taskDescription, taskDueDate)) {
                        databaseHelper.addTask(taskName, taskDescription, taskDueDate);
                        loadTasks();
                        Toast.makeText(MainActivity.this, "Task added successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid input. Fill all fields & use YYYY-MM-DD format!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        dialog = builder.create();
    }


    private void addCard(int id, String name, String description, String dueDate) {
        final View view = getLayoutInflater().inflate(R.layout.card, null);
        TextView nameView = view.findViewById(R.id.name);
        TextView descriptionView = view.findViewById(R.id.description);
        TextView dueDateView = view.findViewById(R.id.dueDate);
        Button delete = view.findViewById(R.id.delete);
        Button edit = view.findViewById(R.id.edit);

        nameView.setText(name);
        descriptionView.setText(description);
        dueDateView.setText(dueDate);

        delete.setOnClickListener(v -> {
            databaseHelper.deleteTask(id);
            loadTasks();
            Toast.makeText(MainActivity.this, "Task deleted!", Toast.LENGTH_SHORT).show();
        });

        edit.setOnClickListener(v -> showEditDialog(id, nameView, descriptionView, dueDateView));

        layout.addView(view);
    }

    private void showEditDialog(int id, TextView nameView, TextView descriptionView, TextView dueDateView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        EditText editName = view.findViewById(R.id.nameEdit);
        EditText editDescription = view.findViewById(R.id.descriptionEdit);
        EditText editDueDate = view.findViewById(R.id.dueDateEdit);

        editName.setText(nameView.getText().toString());
        editDescription.setText(descriptionView.getText().toString());
        editDueDate.setText(dueDateView.getText().toString());

        // Create custom title for the dialog
        TextView customTitle = new TextView(this);
        customTitle.setText("Edit Task");
        customTitle.setTextSize(20);
        customTitle.setPadding(40, 30, 40, 10);
        customTitle.setTextColor(Color.parseColor("#4CAF50")); // Green
        customTitle.setTypeface(null, Typeface.BOLD);

        builder.setCustomTitle(customTitle);
        builder.setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = editName.getText().toString().trim();
                    String newDescription = editDescription.getText().toString().trim();
                    String newDueDate = editDueDate.getText().toString().trim();

                    if (validateInputs(newName, newDescription, newDueDate)) {
                        databaseHelper.updateTask(id, newName, newDescription, newDueDate);
                        loadTasks();
                        Toast.makeText(MainActivity.this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid input. Fill all fields & use YYYY-MM-DD format!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});
        builder.create().show();
    }


    private boolean validateInputs(String name, String description, String dueDate) {
        Log.d("Validation", "Raw Input - Name: [" + name + "], Description: [" + description + "], Due Date: [" + dueDate + "]");

        // Trimmed input check
        name = name.trim();
        description = description.trim();
        dueDate = dueDate.trim();
        Log.d("Validation", "Trimmed Input - Name: [" + name + "], Description: [" + description + "], Due Date: [" + dueDate + "]");

        if (name.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
            Log.e("Validation", "One or more fields are empty!");
            return false;
        }

        // Correct regex pattern for YYYY-MM-DD format
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        boolean isDateValid = dueDate.matches(datePattern);
        Log.d("Validation", "Date Validation: " + isDateValid);

        if (!isDateValid) {
            Log.e("Validation", "Due date format is incorrect!");
            return false;
        }

        return true;
    }
}


