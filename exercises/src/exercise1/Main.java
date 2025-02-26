package exercise1;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        Session session = new Session();
        session.populateStudents();
        session.printAverageQuizScoresOfWholeClass();
        session.printQuizScoresInAscendingOrder();
        session.printPartTimeStudentNames();
        session.printFullTimeStudentExamScores();

    }
}