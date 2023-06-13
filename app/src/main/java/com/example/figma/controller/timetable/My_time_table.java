package com.example.figma.controller.timetable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.figma.databinding.MyTimeTableBinding;
import com.example.figma.model.Board;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.figma.model.Board;
import com.example.figma.controller.MainHome;


public class My_time_table extends AppCompatActivity {

    private MyTimeTableBinding mBinding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // 스피너
    private ArrayList<String> dp_sp;
    private ArrayList<String> grade_sp;
    private ArrayList<String> class_sp;
    private String dp_time;
    private String grade_time;
    private String class_time;
    private LinearLayoutManager layoutManager_mj, layoutManager2_ge, layoutManager_pic;

    private List<Board> mjfieldList = new ArrayList<>();
    private List<Board> gefieldList = new ArrayList<>();




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액티비티 바인딩 객체에 할당 및 뷰 설정
        mBinding = MyTimeTableBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);


        layoutManager_mj = new LinearLayoutManager(this);
        layoutManager2_ge = new LinearLayoutManager(this);
        layoutManager_pic = new LinearLayoutManager(this);

        mBinding.MajorRecycler.setLayoutManager(layoutManager_mj);
        mBinding.GeRecycler.setLayoutManager(layoutManager2_ge);
        mBinding.PicRecycler.setLayoutManager(layoutManager_pic);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();


        // ----선택한 과목 리사이클러뷰에 표시 ---- //
        List<Board> item_list = new ArrayList<>();
        Select_item_adapter adapter = new Select_item_adapter(item_list);
        mBinding.PicRecycler.setAdapter(adapter);

        db.collection("Time_table").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (snapshots != null && snapshots.exists()) {
                    Map<String, Object> data = snapshots.getData();

                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        String fieldName = entry.getKey();
                        Object fieldValue = entry.getValue();
                        Board itemfield = new Board(fieldName, fieldValue);
                        item_list.add(itemfield);
                    }

                    adapter.notifyDataSetChanged();

                }
            }
        });



        // 스피너
        dp_sp = new ArrayList<>();
        grade_sp = new ArrayList<>();
        class_sp = new ArrayList<>();

        dp_sp.add("정보통신공학");
        dp_sp.add("산업경영공학");

        grade_sp.add("1학년");
        grade_sp.add("2학년");
        grade_sp.add("3학년");
        grade_sp.add("전공심화");

        class_sp.add("A반");
        class_sp.add("B반");

        ArrayAdapter<String> adapter_dp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dp_sp);
        adapter_dp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.dpSpinner.setAdapter(adapter_dp);

        ArrayAdapter<String> adapter_grade = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grade_sp);
        adapter_grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.gradeSpinner.setAdapter(adapter_grade);

        ArrayAdapter<String> adapter_class = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, class_sp);
        adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.classSpinner.setAdapter(adapter_class);

        // 학과 스피너
        mBinding.dpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dp_time = (String) parent.getItemAtPosition(position);
                // 선택된 항목을 사용합니다
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무 항목도 선택되지 않았을 때의 동작을 정의합니다
            }
        });

        mBinding.gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                grade_time = (String) parent.getItemAtPosition(position);
                // 선택된 항목을 사용합니다
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무 항목도 선택되지 않았을 때의 동작을 정의합니다
            }
        });

        mBinding.classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                class_time = (String) parent.getItemAtPosition(position);
                // 선택된 항목을 사용합니다
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무 항목도 선택되지 않았을 때의 동작을 정의합니다
            }
        });

        // 전공 리사이클러뷰
        mBinding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((dp_time != null) && (grade_time != null) && (class_time != null)) {

                    db.collection("Major_Time").document(dp_time).collection(grade_time).document(class_time)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            mjHandleData(data);
                                        }
                                    } else {
                                        Log.i("log", "실패");
                                    }

                                }
                            });
                } else {
                    Toast.makeText(My_time_table.this, "정보를 선택하세요", Toast.LENGTH_SHORT).show();
                }
            }

        });


        // ---- 교양 리사이클러뷰에 표시 ---- //
        db.collection("GE class").document("필수").collection("온라인").document("비대면수업")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Map<String, Object> GeData = document.getData();
                                geHandleData(GeData);
                            }
                        } else {
                            Log.i("log", "실패");
                        }

                    }
                });







        // 뒤로가기
        mBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainHome.class);
                startActivity(intent);
            }
        });
    }


    private void mjHandleData(Map<String, Object> data) {

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            // Create a Field object and add it to the list
            Board field = new Board(fieldName, fieldValue);
            mjfieldList.add(field);
            Major_adapter adapter = new Major_adapter(mjfieldList);
            mBinding.MajorRecycler.setAdapter(adapter);

        }


    }

    private void geHandleData(Map<String, Object> data) {

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            // Create a Field object and add it to the list
            Board field = new Board(fieldName, fieldValue);
            gefieldList.add(field);
            Major_adapter adapter = new Major_adapter(gefieldList);
            mBinding.GeRecycler.setAdapter(adapter);

        }


    }


}