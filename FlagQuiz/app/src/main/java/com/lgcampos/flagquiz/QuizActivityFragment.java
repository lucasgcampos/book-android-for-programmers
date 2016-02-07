package com.lgcampos.flagquiz;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Fragment of {@link QuizActivity}
 *
 * @author Lucas Gonçalves de Campos
 */
public class QuizActivityFragment extends Fragment {

    private static final int FLAGS_IN_QUIZ = 10;
    private static final String TAG = "FlagQuiz Activity";

    private List<String> fileNameList;
    private List<String> quizCountriesList;
    private Set<String> regionsSet;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private SecureRandom random;
    private Handler handler;
    private Animation shakeAnimation;

    private LinearLayout quizLinearLayout;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;

    public QuizActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        quizLinearLayout = (LinearLayout) view.findViewById(R.id.quiz_linear_layout);
        questionNumberTextView = (TextView) view.findViewById(R.id.question_number_text_view);
        flagImageView = (ImageView) view.findViewById(R.id.flag_image_view);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = (LinearLayout) view.findViewById(R.id.row_one);
        guessLinearLayouts[1] = (LinearLayout) view.findViewById(R.id.row_two);
        guessLinearLayouts[2] = (LinearLayout) view.findViewById(R.id.row_three);
        guessLinearLayouts[3] = (LinearLayout) view.findViewById(R.id.row_four);
        answerTextView = (TextView) view.findViewById(R.id.answer_text_view);

        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(getString(R.string.question, 1, FLAGS_IN_QUIZ));

        return view;
    }

    public void updateGuessRows(SharedPreferences sharedPreferences) {
        String choices = sharedPreferences.getString(QuizActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility(View.GONE);
        }

        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }
    }

    public void updateRegions(SharedPreferences sharedPreferences) {
        regionsSet = sharedPreferences.getStringSet(QuizActivity.REGIONS, null);
    }

    public void resetQuiz() {
        restartVariables();

        loadFilesNames();
        loadAnswers();
        loadNextFlag();
    }

    private void loadAnswers() {
        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();

        while (flagCounter <= FLAGS_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfFlags);

            String filename = fileNameList.get(randomIndex);

            if (!quizCountriesList.contains(filename)) {
                quizCountriesList.add(filename);
                ++flagCounter;
            }
        }
    }

    private void restartVariables() {
        fileNameList.clear();
        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();
    }

    private void loadFilesNames() {
        AssetManager assets = getActivity().getAssets();

        try {
            for (String region : regionsSet) {
                String[] paths = assets.list(region);

                for (String path : paths) {
                    fileNameList.add(path.replace(".png", ""));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading image file names", e);
        }
    }

    private void loadNextFlag() {
        String nextImage = quizCountriesList.remove(0);
        correctAnswer = nextImage;
        answerTextView.setText("");

        questionNumberTextView.setText(getString(R.string.question, (correctAnswers + 1), FLAGS_IN_QUIZ));

        loadFlag(nextImage);

        Collections.shuffle(fileNameList);

        //Put the answer at the end of fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        loadButtonsOptions();
        loadCorrectAnswer();
    }

    private void loadFlag(String nextImage) {
        String region = nextImage.substring(0, nextImage.indexOf('-'));

        AssetManager assets = getActivity().getAssets();

        try (InputStream stream = assets.open(region + "/" + nextImage + ".png")) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);
        } catch (IOException e) {
            Log.e(TAG, "Error loading " + nextImage, e);
        }
    }

    private void loadButtonsOptions() {
        for (int row = 0; row < guessRows; row++) {
            for (int column = 0; column < guessLinearLayouts[row].getChildCount(); column++) {
                Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                String filename = fileNameList.get(row * 2 + column);
                newGuessButton.setText(getCountryName(filename));
            }
        }
    }

    private void loadCorrectAnswer() {
        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        String countryName = getCountryName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(countryName);
    }

    private String getCountryName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }
}
