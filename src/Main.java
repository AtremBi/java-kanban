public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("title", "description", "NEW");
        Task task2 = new Task("title", "description", "NEW");
        SubTask subTask1 = new SubTask("title", "description", "NEW");
        SubTask subTask2 = new SubTask("title123", "description123", "DONE");
        SubTask subTask3 = new SubTask("title123", "description123", "NEW");
        Epic epic1 = new Epic("title", "description", "IN_PROGRESS");
        Epic epic2 = new Epic("title", "description", "IN_PROGRESS");
        Manager manager = new Manager();

        manager.saveEpic(epic1);
        manager.saveEpic(epic2);

        subTask1.setEpicId(epic1.getId());
        subTask2.setEpicId(epic1.getId());
        subTask3.setEpicId(epic2.getId());

        manager.saveTask(task1);
        manager.saveTask(task2);

        manager.saveSubTask(subTask1);
        manager.saveSubTask(subTask2);
        manager.saveSubTask(subTask3);

        epic1.addNewTask(subTask1.getId());
        epic1.addNewTask(subTask2.getId());
        epic2.addNewTask(subTask3.getId());
        manager.syncEpic(epic1);
        manager.syncEpic(epic2);

        System.out.println("Все эпики - " + manager.getEpicMap());
        System.out.println("Все таски - " + manager.getTaskMap());
        System.out.println("Все сабтаски - " + manager.getSubTaskMap());

        subTask1.setStatus("DONE");
        manager.updateSubTask(subTask1);
        manager.syncEpic(epic1);

        System.out.println("Проверили смену статуса - " + manager.getEpicMap());
        System.out.println("Получение всех сабтасков из эпика - " + manager.getAllSubTaskByEpicId(epic1.getId()));

        manager.removeEpicById(epic1.getId());
        System.out.println("Удаление конеретного эпика - " + manager.getEpicMap());

        manager.removeTaskById(task1.getId());
        System.out.println("Удаление таска - " + manager.getTaskMap());

    }
}
