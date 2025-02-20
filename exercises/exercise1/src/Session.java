import java.util.*;

/**
 * @author Hedy Huang
 * @version 1.0
 */
public class Session {
    private List<Student> students;

    public Session() {
        this.students = new ArrayList<>();
    }
    
    //Populate 20 students (full-time or part-time)
    public void populateStudents(){
        Random random = new Random();
        for (int i = 1; i < 21; i++) {
            if (random.nextBoolean()){
                FullTimeStudent fullTimeStudent = new FullTimeStudent("FullTimeStudent" + i);
                fullTimeStudent.setExamScore1(random.nextInt(101));
                fullTimeStudent.setExamScore2(random.nextInt(101));
                populateQuizScores(fullTimeStudent);
                students.add(fullTimeStudent);
            }else{
                PartTimeStudent partTimeStudent = new PartTimeStudent("PartTimeStudent" + i);
                populateQuizScores(partTimeStudent);
                students.add(partTimeStudent);
            }
        }
    }

    //Create dummy scores
    public void populateQuizScores(Student student) {
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            student.inputQuizzesScores(random.nextInt(101));
        }
    }

    //Create public methods to calculate average quiz scores per student for the whole class
    public void printAverageQuizScoresOfWholeClass() {
        System.out.println("Average quiz scores per student of the whole class:");
        for (Student student : students) {
            System.out.println("name:" + student.getName() + "\tAverage quiz score:" + student.calculateAverageQuizScores());
        }
    }

    //Create public method to print the list of quiz scores in ascending order for one session
    public void printQuizScoresInAscendingOrder () {
        List<Integer> allQuizScores = new ArrayList<>();
        for (Student student : students) {
            allQuizScores.addAll(student.getQuizzesScores());
        }
        Collections.sort(allQuizScores);
        System.out.println("Quiz scores in ascending order: " + allQuizScores);
    }

    //Create public method to print names of part-time students
    public void printPartTimeStudentNames () {
        System.out.println("Names of part-time students:");
        for (Student student : students) {
            if (student instanceof PartTimeStudent){
                System.out.println(student.getName());
            }
        }
    }

    //Create public method to print exam scores of full-time students
    public void printFullTimeStudentExamScores() {
        System.out.println("Exam scores of full-time students:");
        for (Student student : students) {
            if (student instanceof FullTimeStudent){
                System.out.println("name:" + student.getName() + "\texamScore1:" + ((FullTimeStudent) student).getExamScore1()
                                    + "\texamScore2:" + ((FullTimeStudent) student).getExamScore2());
            }
        }
    }
}
