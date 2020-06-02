package com.faicess.puzzledictionary;

import android.content.Intent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import com.dd.morphingbutton.MorphingButton;
import com.github.fabtransitionactivity.SheetLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class QuickTestActivity extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener {

    CoordinatorLayout coordinatorLayout;
    SheetLayout quickTestBottomSheet;
    FloatingActionButton nextQuestionButton, submitBtnFab;
    MorphingButton submitBtnMorph;
    TextView questionTV, questionNumberTV;
    CheckBox option1, option2, option3, option4;
    HomeScreenActivity homeScreenActivityObject;
    Toolbar quickTestToolbar;
    static Boolean morphed;//check whether the button is morphed or not
    static Boolean testFinished;//check whether test is finished or not
    static Boolean evaluated;//check if the question is evaluated or not
    int correctOption;//stores the correct option
    static int questionNumber;//stores the number of questions displayed
    static String Q = "Q";
    static String nextQuestionBtnStatus = "check";//status for next question button
    static int optionSelected = 0;//stores user's selection (1-4), 0 for default, i.e no selection
    static List<Integer> questionQueue;//list of questions dislayed to user
    static int randomQuestion;//stores random question index
    static String question = "";//stores the question
    static String correctAnswer = "";//stores the correct answer
    static String wrongAnswer1 = "";//stores the wrong answer 1
    static String wrongAnswer2 = "";//stores the wrong answer 2
    static String wrongAnswer3 = "";//stores the wrong answer 3
    static int numOfCorrectQuestions = 0;//stores number of correct questions answered by user
    static int numOfWrongQuestions = 0;//stores number of wrong questions answered by user
    static float accuracy = 0f;//stores the accuracy of the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_test);


        //change statusBar color
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(getResources().getColor(R.color.pink));


        coordinatorLayout = findViewById(R.id.quickTestConstraintLayout);
        quickTestBottomSheet = findViewById(R.id.quickTestBottomSheet);
        questionTV = findViewById(R.id.quickTestQuestionTV);
        questionNumberTV = findViewById(R.id.quickTestQuestionNumberTV);
        option1 = findViewById(R.id.option1Checkbox);
        option2 = findViewById(R.id.option2Checkbox);
        option3 = findViewById(R.id.option3Checkbox);
        option4 = findViewById(R.id.option4Checkbox);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        submitBtnFab = findViewById(R.id.sheetFab);
        submitBtnMorph = findViewById(R.id.quickTestSubmitBtnMorph);
        quickTestToolbar = findViewById(R.id.quickTestTabToolbar);

        setSupportActionBar(quickTestToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        //morph the button to set white background and round corners
        MorphingButton.Params initialSetup = MorphingButton.Params.create()
                .duration(0)
                .cornerRadius(30)
                .color(getResources().getColor(R.color.pink)); // normal state color
        submitBtnMorph.morph(initialSetup);


        quickTestBottomSheet.setFab(submitBtnFab);
        quickTestBottomSheet.setFabAnimationEndListener(this);


        homeScreenActivityObject = new HomeScreenActivity();
        questionQueue = new ArrayList<>();
        morphed = false;//default: button is not morphed
        testFinished = false;//default: test is not finished
        evaluated = false;//default: question is not evaluated
        questionNumber = 0;//default: number of questions displayed is 0
        numOfCorrectQuestions = 0;
        numOfWrongQuestions = 0;
        nextQuestionBtnStatus = "check";//default: button is set to evaluate question


        //generate first question on activity startup
        GenerateNextQuestion();



        //listener for first checkbox
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!testFinished && !evaluated) {
                    optionSelected = 1;
                    option1.setChecked(true);
                    option2.setChecked(false);
                    option3.setChecked(false);
                    option4.setChecked(false);
                }
                else if (evaluated || testFinished){
                    if(optionSelected == 1){
                        option1.setChecked(true);
                    }
                    else{
                        option1.setChecked(false);
                    }
                }
            }
        });

        //listener for second checkbox
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!testFinished && !evaluated) {
                    optionSelected = 2;
                    option2.setChecked(true);
                    option1.setChecked(false);
                    option3.setChecked(false);
                    option4.setChecked(false);
                }
                else if (evaluated || testFinished){
                    if(optionSelected == 2){
                        option2.setChecked(true);
                    }
                    else{
                        option2.setChecked(false);
                    }
                }
            }
        });

        //listener for third checkbox
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!testFinished && !evaluated) {
                    optionSelected = 3;
                    option3.setChecked(true);
                    option2.setChecked(false);
                    option1.setChecked(false);
                    option4.setChecked(false);
                }
                else if (evaluated || testFinished){
                    if(optionSelected == 3){
                        option3.setChecked(true);
                    }
                    else{
                        option3.setChecked(false);
                    }
                }
            }
        });

        //listener for fourth checkbox
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!testFinished && !evaluated) {
                    optionSelected = 4;
                    option4.setChecked(true);
                    option2.setChecked(false);
                    option3.setChecked(false);
                    option1.setChecked(false);
                }
                else if (evaluated || testFinished){
                    if(optionSelected == 4){
                        option4.setChecked(true);
                    }
                    else{
                        option4.setChecked(false);
                    }
                }
            }
        });



        //listener for next question button
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if test is not finished
                if (!testFinished) {

                    //check the status of next question button
                    //if status is to check the question, i.e the question is not evaluated yet, then evaluate the question
                    if (nextQuestionBtnStatus.equals("check")) {

                        //evaluate current question
                        String answer = EvaluateQuestion();

                        //if user has selected an option
                        if (!answer.equals("")) {

                            UpdateQuestionUI();
                            nextQuestionButton.setImageResource(R.drawable.right_arrow);
                            nextQuestionBtnStatus = "next";
                        }

                        //if no option is selected by user
                        else {

                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please Select One Option.", Snackbar.LENGTH_SHORT);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(getResources().getColor(R.color.pink));
                            snackbar.show();
                        }
                    }

                    //if status is to generate the next question, i.e the question has been evaluated, then generate the next question
                    else if (nextQuestionBtnStatus.equals("next")) {

                        //generate next question
                        GenerateNextQuestion();
                        nextQuestionButton.setImageResource(R.drawable.tickmark);
                        nextQuestionBtnStatus = "check";
                    }
                }

                else {

                    //if test is finished
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Test is Finished.", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.pink));
                    snackbar.show();
                }
            }
        });



        //submit button listener to finish test and display result
        submitBtnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                testFinished = true;

                //if button is not morphed yet, morph it.
                if(!morphed) {

                    MorphingButton.Params circle = MorphingButton.Params.create()
                            .duration(500)
                            .cornerRadius(120) // 56 dp
                            .width(480) // 56 dp
                            .height(120) // 56 dp
                            .text("Show Result")
                            .color(getResources().getColor(R.color.pink)) // normal state color
                            .colorPressed(getResources().getColor(R.color.darkPink)); // pressed state color
                    submitBtnMorph.morph(circle);

                    morphed = true;
                }
                else{

                    Result();

                    //animate bottom sheet opening
                    submitBtnMorph.setVisibility(View.INVISIBLE);
                    quickTestBottomSheet.expandFab();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void GenerateNextQuestion(){

        evaluated = false;

        if(!testFinished) {

            do {
                //generate a random index from database
                randomQuestion = new Random().nextInt(HomeScreenActivity.NUMBER_OF_WORDS);
            }

            //check if the generated index has already been generated before
            while (questionQueue.contains(randomQuestion));

            //add the generated index to the queue if not generated before
            questionQueue.add(randomQuestion);

            //get the question and answers from database
            question = homeScreenActivityObject.GetMeaningAtIndex(randomQuestion);
            correctAnswer = homeScreenActivityObject.GetWordAtIndex(randomQuestion);
            wrongAnswer1 = homeScreenActivityObject.GetWordAtIndex(new Random().nextInt(HomeScreenActivity.NUMBER_OF_WORDS));
            wrongAnswer2 = homeScreenActivityObject.GetWordAtIndex(new Random().nextInt(HomeScreenActivity.NUMBER_OF_WORDS));
            wrongAnswer3 = homeScreenActivityObject.GetWordAtIndex(new Random().nextInt(HomeScreenActivity.NUMBER_OF_WORDS));

            UpdateQuestionUI();
        }
    }

    void UpdateQuestionUI(){

        if(!evaluated) {
            //set the default selection
            optionSelected = 0;
            int questionNum = questionNumber + 1;

            //reset background of all checkboxes
            option1.setBackgroundColor(getResources().getColor(R.color.white));
            option2.setBackgroundColor(getResources().getColor(R.color.white));
            option3.setBackgroundColor(getResources().getColor(R.color.white));
            option4.setBackgroundColor(getResources().getColor(R.color.white));

            //reset checkboxes when next question loads
            option1.setChecked(false);
            option2.setChecked(false);
            option3.setChecked(false);
            option4.setChecked(false);

            //set the question to textview
            questionTV.setText(question);

            //set the question number textview
            questionNumberTV.setText(Q + questionNum);

            //generate a random option number which displays the correct answer
            correctOption = new Random().nextInt(3);

            switch (correctOption) {
                case 0:
                    //option 1 has right answer
                    option1.setText(correctAnswer);
                    option2.setText(wrongAnswer1);
                    option3.setText(wrongAnswer2);
                    option4.setText(wrongAnswer3);
                    break;

                case 1:
                    //option 2 has right answer
                    option1.setText(wrongAnswer1);
                    option2.setText(correctAnswer);
                    option3.setText(wrongAnswer2);
                    option4.setText(wrongAnswer3);
                    break;

                case 2:
                    //option 3 has right answer
                    option1.setText(wrongAnswer1);
                    option2.setText(wrongAnswer2);
                    option3.setText(correctAnswer);
                    option4.setText(wrongAnswer3);
                    break;

                case 3:
                    //option 4 has right answer
                    option1.setText(wrongAnswer1);
                    option2.setText(wrongAnswer2);
                    option3.setText(wrongAnswer3);
                    option4.setText(correctAnswer);
                    break;

                default:
                    //option 1 has right answer
                    option1.setText(correctAnswer);
                    option2.setText(wrongAnswer1);
                    option3.setText(wrongAnswer2);
                    option4.setText(wrongAnswer3);
            }
        }
        else {
            switch (correctOption){
                case 0:
                    option1.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                    break;
                case 1:
                    option2.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                    break;
                case 2:
                    option3.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                    break;
                case 3:
                    option4.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                    break;
            }
        }
    }


    String EvaluateQuestion(){

        String userAnswer = "";
        switch (optionSelected){
            case 1:
                userAnswer = option1.getText().toString();
                if(!userAnswer.equals("")) {
                    if (homeScreenActivityObject.GetMeaning(userAnswer).equalsIgnoreCase(question)) {

                        option1.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                        numOfCorrectQuestions++;
                    } else {

                        option1.setBackground(getResources().getDrawable(R.drawable.wrong_answer_bg));
                        numOfWrongQuestions++;
                    }
                }
                questionNumber ++;
                evaluated = true;
                break;
            case 2:
                userAnswer = option2.getText().toString();
                if(!userAnswer.equals("")) {
                    if (homeScreenActivityObject.GetMeaning(userAnswer).equalsIgnoreCase(question)) {

                        option2.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                        numOfCorrectQuestions++;
                    } else {

                        option2.setBackground(getResources().getDrawable(R.drawable.wrong_answer_bg));
                        numOfWrongQuestions++;
                    }
                }
                questionNumber ++;
                evaluated = true;
                break;
            case 3:
                userAnswer = option3.getText().toString();
                if(!userAnswer.equals("")) {
                    if (homeScreenActivityObject.GetMeaning(userAnswer).equalsIgnoreCase(question)) {

                        option3.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                        numOfCorrectQuestions++;
                    } else {

                        option3.setBackground(getResources().getDrawable(R.drawable.wrong_answer_bg));
                        numOfWrongQuestions++;
                    }
                }
                questionNumber ++;
                evaluated = true;
                break;
            case 4:
                userAnswer = option4.getText().toString();
                if(!userAnswer.equals("")) {
                    if (homeScreenActivityObject.GetMeaning(userAnswer).equalsIgnoreCase(question)) {

                        option4.setBackground(getResources().getDrawable(R.drawable.correct_answer_bg));
                        numOfCorrectQuestions++;
                    } else {

                        option4.setBackground(getResources().getDrawable(R.drawable.wrong_answer_bg));
                        numOfWrongQuestions++;
                    }
                }
                questionNumber ++;
                evaluated = true;
                break;
            default:
                userAnswer = "";
                evaluated = false;
                break;
        }

        return userAnswer;
    }


    void Result(){

        //calculates accuracy for the current test
        if(questionNumber != 0) {
            int numOfQuestions = numOfCorrectQuestions + numOfWrongQuestions;
            accuracy = (float) (numOfCorrectQuestions * 100) / numOfQuestions;
        }
        else {
            accuracy = 0f;
        }
    }


    @Override
    public void onFabAnimationEnd() {

        //open result activity
        Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra("CorrectQuestions", numOfCorrectQuestions)
                .putExtra("WrongQuestions", numOfWrongQuestions)
                .putExtra("Accuracy", accuracy);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        this.finish();
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        startActivity(intent);
    }
}
