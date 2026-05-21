package tools;

import Collection.LabWork;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

public class CollectionManager {
    private static long lastId = 0;
    private LinkedList<LabWork> labCollection = new LinkedList<>();
    private final BaseConnect baseConnect;

    public CollectionManager(BaseConnect baseConnect) {
        this.baseConnect = baseConnect;
    }

    private final ZonedDateTime creationDate = ZonedDateTime.now();

    public static void setLastId(long lastId) {
        if (lastId > CollectionManager.lastId) {
            CollectionManager.lastId = lastId;
        }
    }

    public long getLastId() {
        return lastId;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setLabCollection(LinkedList<LabWork> labCollection) {
        this.labCollection = labCollection;
    }

    public boolean remove(int index){
        try {
            baseConnect.remove(index);
            Optional<LabWork> target = labCollection.stream()
                    .filter(lab -> lab.getId() == index)
                    .findFirst();
            if (target.isPresent()) {
                labCollection.remove(target.get());
                return true;
            }

            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("не удалось удалить лабу из БД и из коллекцию");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void addElement(LabWork lab){
        try {
            baseConnect.addToDB(lab);
            labCollection.addLast(lab);
        } catch (SQLException e) {
            System.out.println("не удалось добавить лабу в БД и коллекцию");
            System.out.println(e.getMessage());
        }
    }

    public LabWork getElemnt(int index){return labCollection.get(index);}

    public void clearCollection(){
        try {
            baseConnect.clear();
            labCollection.clear();
        } catch (SQLException e) {
            System.out.println("не удалось очистить лабу в БД и коллекцию");
            System.out.println(e.getMessage());
        }
    }

    public boolean update(Long id, LabWork labWork) {
        try {
            baseConnect.update(id, labWork);
            Optional<LabWork> target = labCollection.stream()
                    .filter(lab -> Objects.equals(lab.getId(), id))
                    .findFirst();
            if (target.isPresent()) {
                labWork.setId(id);
                labCollection.remove(target.get());
                labCollection.add(labWork);
                return true;
            }
            return false;
        } catch (SQLException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getCollectionType() {
        return labCollection.getClass().getName();
    }

    public int getCollectionSyze(){return labCollection.size();}

    public LinkedList<LabWork> getLabCollection() {
        return labCollection;
    }

    public Long generateId(){
        return ++lastId;
    }

    public void printCol(){
        System.out.println(labCollection);
    }

    public void changeLab(LabWork lab, int idx){
        labCollection.set(idx, lab);
    }
}


