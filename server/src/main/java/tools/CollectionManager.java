package tools;

import Collection.LabWork;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.LinkedList;

public class CollectionManager {
    private long lastId = 0;
    private LinkedList<LabWork> labCollection = new LinkedList<>();
    private BaseConnect baseConnect;

    public CollectionManager(BaseConnect baseConnect) {
        this.baseConnect = baseConnect;
    }

    private final ZonedDateTime creationDate = ZonedDateTime.now();

    public void setLastId(long lastId) {
        this.lastId = lastId;
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

    public void removeElement(int index){
        labCollection.remove(index);
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
        labCollection.clear();
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


