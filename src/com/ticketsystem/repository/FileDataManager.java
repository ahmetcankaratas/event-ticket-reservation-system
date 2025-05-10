package com.ticketsystem.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of DataManager interface.
 */
public class FileDataManager<T> implements DataManager<T> {
    private final String filename;
    private final List<T> cache;

    public FileDataManager(String filename) {
        this.filename = filename;
        this.cache = new ArrayList<>();
        loadFromFile();
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<T> items = (List<T>) ois.readObject();
            cache.clear();
            cache.addAll(items);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(T item) {
        if (!cache.contains(item)) {
            cache.add(item);
        }
        saveToFile();
    }

    @Override
    public void saveAll(List<T> items) {
        cache.addAll(items);
        saveToFile();
    }

    @Override
    public T getById(String id) {
        // This implementation assumes T has a getId() method
        // You'll need to modify this based on your actual implementation
        return cache.stream()
                .filter(item -> {
                    try {
                        return item.getClass().getMethod("getId").invoke(item).equals(id);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public void delete(String id) {
        cache.removeIf(item -> {
            try {
                return item.getClass().getMethod("getId").invoke(item).equals(id);
            } catch (Exception e) {
                return false;
            }
        });
        saveToFile();
    }

    @Override
    public void deleteAll() {
        cache.clear();
        saveToFile();
    }
} 