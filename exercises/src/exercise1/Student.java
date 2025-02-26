package exercise1;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Student {
    private String name;
    private List<Integer> quizzesScores;

    public Student(String name) {
        this.name = name;
        this.quizzesScores = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void inputQuizzesScores(int score) {

        quizzesScores.add(score);
    }

    public List<Integer> getQuizzesScores() {
        return quizzesScores;
    }

    //Create public methods to calculate average quiz scores per student for the whole class
    public int calculateAverageQuizScores() {
        if (quizzesScores.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (int score : quizzesScores) {
            sum += score;
        }
        return sum / quizzesScores.size();
    }
}
