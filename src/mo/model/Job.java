package mo.model;

public class Job {
    public String courseName;
    public int vacancies;
    public String duties;
    public String deadline;
    public String priorities;

    public Job(String courseName, int vacancies, String duties, String deadline, String priorities) {
        this.courseName = courseName;
        this.vacancies = vacancies;
        this.duties = duties;
        this.deadline = deadline;
        this.priorities = priorities;
    }

    // 转换为存入文件的字符串（CSV格式）
    @Override
    public String toString() {
        return courseName + "," + vacancies + "," + duties + "," + deadline + "," + priorities;
    }
}