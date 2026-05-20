package tools;

import Collection.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class Command {
    /**
     * класс для формирования команды и аргументов команды с их классом для отправки на сервер
     */

    private final int argCount;;
    private final Class<?> objectClass; // для OBJECT

    public Command(int argCount) {
        this(argCount, null);
    }

    public Command(int argCount, Class<?> objectClass) {
        this.argCount = argCount;
        this.objectClass = objectClass;
    }

    public int getArgCount() { return argCount; }
    public Class<?> getObjectClass() { return objectClass; }
    protected static <T> boolean input(String fieldName, Consumer<T> setter, Function<String, T> intoValue) {
        try {
            String line = Consoll.askSmt(fieldName);
            setter.accept(intoValue.apply(line));
            return true;
        } catch (NumberFormatException exep) {
            System.out.println("Проверь пральность ввода!");
            return false;
        }
        catch (IllegalArgumentException exp){
            Consoll.printSmt(exp.getMessage());
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public LabWork makeLab(){
        LabWork labWork = new LabWork();
        long id = 1;
        labWork.setCreationDate(LocalDate.now());
        while(!input("сложность: " + Arrays.toString(Difficulty.values()), labWork::setDifficulty, Difficulty::fromString));
        while(!input("имя лабы", labWork::setName, String::valueOf));
        while(!input("минимальное кол-во баллов", labWork::setMinimalPoint, Double::valueOf));
        labWork.setCoordinates(makeCoordinates());
        labWork.setAuthor(makePerson());
        labWork.setId(id);
        return labWork;
    }

    public Person makePerson(){
        Person person = new Person();
        while(!input("ваш вес", person::setWeight, Double::valueOf));
        while(!input("ваше имя", person::setName, String::valueOf));
        while(!input("цвет глаз: " + Arrays.toString(Color.values()), person::setEyeColor, Color::fromString));
        return person;
    }

    public Coordinates makeCoordinates(){
        Coordinates coordinates = new Coordinates();
        while(!input("координата по Х", coordinates::setX, Long::valueOf));
        while(!input("координата по Y", coordinates::setY, Integer::valueOf));
        return coordinates;
    }
}