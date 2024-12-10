import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();

        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2); //

        Epic epic1 = new Epic(3, "Epic 1", "Description Epic 1");
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask(4, "SubTask 1", "Description SubTask 1", epic1.getId(), Status.NEW);
        taskManager.createSubTask(subTask1);

        printAllTasks(taskManager);
        printHistory(taskManager);

        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        System.out.println("Epic Status after updating subtask 1: " + taskManager.getEpicById(epic1.getId()).getStatus());

        printHistory(taskManager);//25+

        Task fetchedTask = taskManager.getTaskById(task1.getId());
        System.out.println("Fetched Task: " + fetchedTask);
        printHistory(taskManager);

        SubTask fetchedSubTask = taskManager.getSubTaskById(subTask1.getId());
        System.out.println("Fetched SubTask: " + fetchedSubTask);
        printHistory(taskManager);

        Epic fetchedEpic = taskManager.getEpicById(epic1.getId());
        System.out.println("Fetched Epic: " + fetchedEpic);
        printHistory(taskManager);

        taskManager.deleteTask(task1.getId());
        System.out.println("After deleting Task 1:");
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.deleteAllSubTasks();
        System.out.println("After deleting all SubTasks:");
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.deleteAllEpics();
        System.out.println("After deleting all Epics:");
        printAllTasks(taskManager);
        printHistory(taskManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
            for (SubTask subTask : taskManager.getSubTasksByEpic(epic.getId())) {
                System.out.println("--> " + subTask);
            }
        }
        System.out.println("Подзадачи:");
        for (SubTask subTask : taskManager.getSubTasks()) {
            System.out.println(subTask);
        }
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}