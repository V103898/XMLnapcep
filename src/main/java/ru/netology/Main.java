package ru.netology;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
        import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Конвертация CSV в JSON (из предыдущей задачи)
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        List<Employee> csvList = parseCSV(columnMapping, csvFileName);
        if (csvList != null) {
            String csvJson = listToJson(csvList);
            writeString(csvJson, "data.json");
        }

        // Новая часть: конвертация XML в JSON
        String xmlFileName = "data.xml";
        List<Employee> xmlList = parseXML(xmlFileName);
        if (xmlList != null) {
            String xmlJson = listToJson(xmlList);
            writeString(xmlJson, "data2.json");
            System.out.println("XML to JSON конвертация завершена успешно");
        }
    }

    // Объявляем класс Employee как статический
    public static class Employee {
        public long id;
        public String firstName;
        public String lastName;
        public String country;
        public int age;

        public Employee() {
            // Пустой конструктор

        }
        public  Employee(long id, String firstName, String lastName, String country, int age) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.country = country;
            this.age = age;
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Employee> parseXML(String fileName) {
        try {
            // Создаем парсер XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            // Получаем корневой элемент
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            List<Employee> employees = new ArrayList<>();

            // Проходим по всем элементам employee
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Извлекаем данные из XML
                    long id = Long.parseLong(getTagValue("id", element));
                    String firstName = getTagValue("firstName", element);
                    String lastName = getTagValue("lastName", element);
                    String country = getTagValue("country", element);
                    int age = Integer.parseInt(getTagValue("age", element));

                    // Создаем объект Employee и добавляем в список
                    employees.add(new Employee(id, firstName, lastName, country, age));
                }
            }
            return employees;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    private static String listToJson(List<Employee> employees) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(employees);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}