package tools;

import Collection.LabWork;
import java.io.Serializable;

public class UpdateArgs implements Serializable {
    private Long id;
    private LabWork labWork;

    public UpdateArgs(Long id, LabWork labWork) {
        this.id = id;
        this.labWork = labWork;
    }

    public Long getId() { return id; }
    public LabWork getLabWork() { return labWork; }
}