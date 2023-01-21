package ru.yandex.praktikum.biryukov.kanban.manager.memory;

import ru.yandex.praktikum.biryukov.kanban.data.Task;
import ru.yandex.praktikum.biryukov.kanban.manager.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> viewedTasks = new CustomLinkedList<>();
    @Override
    public List<Task> getHistory() {
        return viewedTasks.getTasks();
    }

    @Override
    public void add(Task task){
        if (task != null){
            viewedTasks.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (viewedTasks.memory.containsKey(id)){
            viewedTasks.removeNode(viewedTasks.memory.get(id));
        }
    }

    public class CustomLinkedList<T extends Task> {
        private final HashMap<Integer, Node<T>> memory = new HashMap<>();
        public Node<T> head;
        public Node<T> tail;

        public void linkLast(T e) {
            if (memory.containsKey(e.getId())){
                removeNode(memory.get(e.getId()));
                    memory.remove(e.getId());
            }
            final Node<T> t = tail;
            final Node<T> newNode = new Node<>(t, e, null);
            tail = newNode;
            if (t == null) {
                head = newNode;
            } else {
                t.next = newNode;
            }
            memory.put(e.getId(), newNode);
        }

        public void removeNode(Node<T> node){
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
        }

        public List<Task> getTasks(){
            List<Task> tasks = new ArrayList<>();
            for (Node<T> task = head; task != null; task = task.next) {
                tasks.add(task.data);
            }

            return tasks;
        }

        class Node <E> {

            public E data;
            public Node<E> next;
            public Node<E> prev;

            public Node(Node<E> prev ,E data, Node<E> next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }
        }
    }
}
