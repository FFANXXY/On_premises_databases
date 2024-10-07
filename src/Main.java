import op_databases.Database;

import static op_databases.FilePaths.CDesk.ePath;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("is Running");

        Database database = new Database();
        /*

        if(database.isInitialized()) {
            database.initialize();
        } else {
        database.ResetFilePath();
        }
         */
        database.AutoInitialize();

        //全过程实例
        String a = "111";

        // 序列化并保存对象
        database.serializeObject(a, "a.dat");

        // 反序列化并读取对象
        try {
            String deserializedA = database.deserializeObject("a.dat", String.class);
            System.out.println("Deserialized Integer: " + deserializedA);
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        //简单化存储对象

        // 示例对象
        String str = "Hello, World!";
        int b = 1;
        double d = 3.14;

        System.out.println();
        // 序列化并保存对象
        database.serializeObject(str, "string.dat");
        database.serializeObject(b, "int.dat");
        database.serializeObject(d, "double.dat");

        // 反序列化并读取对象
        try {
            String deserializedStr = database.deserializeObject("string.dat");
            Integer deserializedA = database.deserializeObject("int.dat");
            Double deserializedD = database.deserializeObject("double.dat");

            System.out.println("Deserialized String: " + deserializedStr);
            System.out.println("Deserialized Integer: " + deserializedA);
            System.out.println("Deserialized Double: " + deserializedD);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}