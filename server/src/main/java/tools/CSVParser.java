package tools;

import Collection.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedList;

import Connecting.ConnectManager;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CSVParser {
    private static final Logger log = LogManager.getLogger(CSVParser.class);
    static String file;
    CollectionManager collectionManager;
    public CSVParser(CollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }

    public LinkedList<LabWork> parse(String file) {
        CSVParser.file = file;
        LinkedList<LabWork> labs = new LinkedList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {
            String[] header = csvReader.readNext();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                LabWork lab = new LabWork();
                Long id = Long.valueOf(line[0].trim());
                if (id > collectionManager.getLastId()) {
                    collectionManager.setLastId(id);
                }
                lab.setId(id);
                lab.setName(line[1].trim());

                Coordinates coordinates = new Coordinates();
                coordinates.setX(Long.parseLong(line[5].trim()));
                coordinates.setY(Integer.valueOf(line[6].trim()));
                lab.setCoordinates(coordinates);

                Person person = new Person();
                person.setName(line[7].trim());
                person.setWeight(Double.parseDouble(line[8].trim()));
                person.setEyeColor(Color.fromString(line[9].trim()));
                lab.setAuthor(person);

                String creationDateStr = line[2].trim();
                lab.setCreationDate(LocalDate.parse(creationDateStr));
                lab.setMinimalPoint(Double.parseDouble(line[3].trim()));
                String typeStr = line[4].trim();
                lab.setDifficulty(Difficulty.fromString(typeStr));

                labs.add(lab);
            }

        } catch (FileNotFoundException e) {
            log.warn("файл " + file + " не найден");
        } catch (NumberFormatException e) {
            log.warn("Проверь правильность введенных данных (возможно не тот тип данных)");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
        } catch (IOException e) {
            log.warn("Непредвиденная ошибка чтения файла " + file);
        } catch (Exception e) {
            log.warn("Ошибка, возможно что-то не так с форматом");
        }

        return labs;
    }
    public void convertToCSV(LinkedList<LabWork> labs) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            String[] header = {"id", "name", "creationDate", "minimalPoints", "difficulty", "x", "y", "personName", "personWeight", "eye"};
            writer.writeNext(header);
            for (LabWork lab : labs) {
                long id = lab.getId();
                String[] line = {
                        String.valueOf(id),
                        lab.getName(),
                        lab.getCreationDate().toString(),
                        String.valueOf(lab.getMinimalPoint()),
                        lab.getDifficulty().toString(),
                        String.valueOf(lab.getCoordinates().getX()),
                        String.valueOf(lab.getCoordinates().getY()),
                        lab.getAuthor().getName(),
                        String.valueOf(lab.getAuthor().getWeight()),
                        lab.getAuthor().getEyeColor().toString()
                };
                writer.writeNext(line);
            }
        } catch (Exception e) {
            log.warn("Произошла ошибка при записи в файл ");
        }
    }
}