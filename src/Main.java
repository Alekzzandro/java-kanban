import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskTypes;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.file.Paths;

import exception.ManagerLoadFileException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = null;
        try {
            taskManager = Managers.getFileBackedTaskManager(Paths.get("tasks.csv"));
        } catch (IOException | ManagerLoadFileException e) {
            System.err.println("Ошибка при создании менеджера задач: " + e.getMessage());
            return;
        }

        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, TaskTypes.TASK);
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, TaskTypes.TASK);

        try {
            taskManager.createTask(task1);
            taskManager.createTask(task2);
        } catch (ManagerLoadFileException e) {
            System.err.println("Ошибка при создании задачи: " + e.getMessage());
        }

        Epic epic1 = new Epic(3, "Epic 1", "Description Epic 1", Status.NEW, TaskTypes.EPIC);

        try {
            taskManager.createEpic(epic1);
        } catch (ManagerLoadFileException e) {
            System.err.println("Ошибка при создании эпика: " + e.getMessage());
        }

        SubTask subTask1 = new SubTask(4, "SubTask 1", "Description SubTask 1", epic1.getId(), Status.NEW, TaskTypes.SUBTASK);

        try {
            taskManager.createSubTask(subTask1);
        } catch (ManagerLoadFileException e) {
            System.err.println("Ошибка при создании подзадачи: " + e.getMessage());
        }

        printAllTasks(taskManager);
        printHistory(taskManager);

        subTask1.setStatus(Status.DONE);

        taskManager.updateSubTask(subTask1);

        System.out.println("Epic Status after updating subtask 1: " + taskManager.getEpicById(epic1.getId()).getStatus());
        printHistory(taskManager);

        Task fetchedTask = taskManager.getTaskById(task1.getId());

        if (fetchedTask != null) {
            System.out.println("Fetched Task: " + fetchedTask);
        } else {
            System.out.println("Task not found");
        }

        printHistory(taskManager);

        SubTask fetchedSubTask = taskManager.getSubTaskById(subTask1.getId());

        if (fetchedSubTask != null) {
            System.out.println("Fetched SubTask: " + fetchedSubTask);
        } else {
            System.out.println("SubTask not found");
        }

        printHistory(taskManager);

        Epic fetchedEpic = taskManager.getEpicById(epic1.getId());

        if (fetchedEpic != null) {
            System.out.println("Fetched Epic: " + fetchedEpic);
        } else {
            System.out.println("Epic not found");
        }

        printHistory(taskManager);

        try {
            taskManager.deleteTask(task1.getId());
        } catch (ManagerLoadFileException e) {
            System.err.println("Ошибка при удалении задачи: " + e.getMessage());
        }

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