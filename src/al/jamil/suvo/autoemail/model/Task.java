package al.jamil.suvo.autoemail.model;

public class Task {
    String project;
    String task;
    long time;

    public Task(String project, String task, long time) {
        this.project = project;
        this.task = task;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Task{" +
                "project='" + project + '\'' +
                ", task='" + task + '\'' +
                '}';
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
