import model.Status;
import model.SubTask;
import model.Task;
import service.HttpTaskServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

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

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        System.out.println("HTTP-сервер запущен на порту 8080.");
        System.out.println("Для завершения работы программы остановите сервер вручную.");

        demonstrateTaskManagerUsage(taskManager);
    }

    private static void demonstrateTaskManagerUsage(TaskManager taskManager) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(Status.NEW);
        task1.setDuration(java.time.Duration.ofMinutes(30));
        task1.setStartTime(now);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(Status.NEW);
        task2.setDuration(java.time.Duration.ofMinutes(45));
        task2.setStartTime(now.plusMinutes(35));

        try {
            taskManager.createTask(task1);
            taskManager.createTask(task2);
        } catch (ManagerLoadFileException e) {
            System.err.println("Ошибка при создании задачи: " + e.getMessage());
        }

        model.Epic epic1 = new model.Epic();
        epic1.setTitle("Epic 1");
        epic1.setDescription("Description Epic 1");
        epic1.setStatus(Status.NEW);

        try {
            taskManager.createEpic(epic1);
        } catch (ManagerLoadFileException e) {
            System.err.println("Ошибка при создании эпика: " + e.getMessage());
        }

        model.SubTask subTask1 = new model.SubTask();
        subTask1.setTitle("SubTask 1");
        subTask1.setDescription("Description SubTask 1");
        subTask1.setStatus(Status.NEW);
        subTask1.setDuration(java.time.Duration.ofMinutes(30));
        subTask1.setStartTime(now.plusHours(2));

        if (taskManager.getEpicById(epic1.getId()) != null) {
            try {
                model.SubTask newSubTask = new model.SubTask(
                        subTask1.getId(),
                        subTask1.getTitle(),
                        subTask1.getDescription(),
                        subTask1.getStatus(),
                        epic1.getId(),
                        subTask1.getDuration(),
                        subTask1.getStartTime()
                );
                taskManager.createSubTask(newSubTask);
            } catch (ManagerLoadFileException e) {
                System.err.println("Ошибка при создании подзадачи: " + e.getMessage());
            }
        } else {
            System.out.println("Эпик с ID " + epic1.getId() + " не найден. Подзадача не создана.");
        }

        printAllTasks(taskManager);

        printHistory(taskManager);

        List<SubTask> subTasks = taskManager.getSubTasks();
        if (!subTasks.isEmpty()) {
            model.SubTask firstSubTask = subTasks.get(0);
            firstSubTask.setStatus(Status.DONE);
            try {
                taskManager.updateSubTask(firstSubTask);
            } catch (ManagerLoadFileException e) {
                System.err.println("Ошибка при обновлении подзадачи: " + e.getMessage());
            }

            model.Epic fetchedEpic = taskManager.getEpicById(epic1.getId());
            if (fetchedEpic != null) {
                System.out.println("Статус эпика после обновления подзадачи 1: " + fetchedEpic.getStatus());
            } else {
                System.out.println("Эпик не найден");
            }
        } else {
            System.out.println("Подзадача с ID " + subTask1.getId() + " не найдена");
        }


        printHistory(taskManager);

        try {
            taskManager.deleteTask(task1.getId());
        } catch (ManagerLoadFileException e) {
            System.err.println("Ошибка при удалении задачи: " + e.getMessage());
        }

        System.out.println("После удаления задачи 1:");
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.deleteAllSubTasks();
        System.out.println("После удаления всех подзадач:");
        printAllTasks(taskManager);
        printHistory(taskManager);

        taskManager.deleteAllEpics();
        System.out.println("После удаления всех эпиков:");
        printAllTasks(taskManager);
        printHistory(taskManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (model.Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
            for (model.SubTask subTask : taskManager.getSubTasksByEpic(epic.getId())) {
                System.out.println("--> " + subTask);
            }
        }

        System.out.println("Подзадачи:");
        for (model.SubTask subTask : taskManager.getSubTasks()) {
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