package com.faicess.puzzledictionary;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

//import com.dekoservidoni.omfm.OneMoreFabMenu;

import java.text.DecimalFormat;

public class TestResultActivity extends AppCompatActivity {

    static int numOfQues;
    static int numOfCorrectQues = 0;
    static int numberOfWrongQues = 0;
    static double accuracy = 0;

    TextView numOfQuestionsTV, correctQuestionsTV, wrongQuestionsTV, accuracyTV;
    //OneMoreFabMenu resultOmfmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        //change statusBar color
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(getResources().getColor(R.color.pink));

        numOfQuestionsTV = findViewById(R.id.testResultNumOfQuesTV);
        correctQuestionsTV = findViewById(R.id.testResultNocqTV);
        wrongQuestionsTV = findViewById(R.id.testResultNowqTV);
        accuracyTV = findViewById(R.id.testResultAccTV);
        //resultOmfmBtn = findViewById(R.id.resultOmfmBtn);

        Intent intenThatStartedThisActivity = getIntent();
        if(intenThatStartedThisActivity.hasExtra("CorrectQuestions")){
            numOfCorrectQues = intenThatStartedThisActivity.getIntExtra("CorrectQuestions", 0);
        }

        if(intenThatStartedThisActivity.hasExtra("WrongQuestions")){
            numberOfWrongQues = intenThatStartedThisActivity.getIntExtra("WrongQuestions", 0);
        }

        if(intenThatStartedThisActivity.hasExtra("Accuracy")){
            accuracy = intenThatStartedThisActivity.getFloatExtra("Accuracy", 0);
        }

        numOfQues = numOfCorrectQues + numberOfWrongQues;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");


        numOfQuestionsTV.setText(String.valueOf(numOfQues));
        correctQuestionsTV.setText(String.valueOf(numOfCorrectQues));
        wrongQuestionsTV.setText(String.valueOf(numberOfWrongQues));
        accuracyTV.setText(decimalFormat.format(accuracy) + "%");

        //resultOmfmBtn.setOptionsClick(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onOptionClick(Integer integer) {

        Intent intent;
        switch (integer){
            case R.id.resultOmfmHomeBtn:
                finishAffinity();
                this.finish();
                intent = new Intent(TestResultActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                break;
            case R.id.resultOmfmExploreBtn:
                finishAffinity();
                this.finish();
                intent = new Intent(TestResultActivity.this, ExploreDictionaryActivity.class);
                startActivity(intent);
                break;
            case R.id.resultOmfmFlashcardBtn:
                finishAffinity();
                this.finish();
                intent = new Intent(TestResultActivity.this, FlashCardsActivity.class);
                startActivity(intent);
                break;
        }
    }
*/
    @Override
    public void onBackPressed() {
        finishAffinity();
        this.finish();
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        startActivity(intent);
    }
}
