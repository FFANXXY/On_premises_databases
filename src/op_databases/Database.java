package op_databases;

import op_databases.FileThrow.DATABASE;

import java.io.*;


/**
 *
 * @version b1.0
 * @author Fanxy
 */
public class Database {
    public String spawn(File path, boolean doPrint) {
        // 获取文件的父目录
        File parentDir = path.getParentFile();

        // 如果父目录不存在，则创建所有必要的上级目录
        if (parentDir != null && !parentDir.exists()) {
            boolean result = parentDir.mkdirs();
            if (!result) {
                System.out.println("Could not create directory: " + parentDir.getAbsolutePath());
                return DATABASE.CREATE_TABLE_FAIL;
            }
        }

        // 检查文件是否已存在
        if (path.exists() && path.isFile()) {
            return DATABASE.FILE_EXISTS;
        }

        try {
            // 创建文件
            boolean fileCreated = path.createNewFile();
            if (fileCreated) {
                if (doPrint) {
                    System.out.println("Created file: " + path.getAbsolutePath());
                }
                return DATABASE.CREATE_TABLE_SUCCESS;
            } else {
                System.out.println("The File could not be created: " + path.getAbsolutePath());
                return DATABASE.CREATE_TABLE_FAIL;
            }
        } catch (IOException e) {
            e.fillInStackTrace();
            return DATABASE.CREATE_TABLE_ERROR;
        }
    }

    // 懒人版
    public String spawn(String path) {
        return spawn(new File(path), false);
    }

    public String spawnDirectory(File directory) {
        // 检查目录是否已经存在
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return DATABASE.DIRECTORY_EXISTS;
            } else {
                return DATABASE.FILE_EXISTS;
            }
        } else {
            // 尝试创建目录
            boolean result = directory.mkdirs();
            if (result) {
                return DATABASE.CREATE_TABLE_SUCCESS;
            } else {
                System.out.println("Could not create directory: " + directory.getAbsolutePath());
                return DATABASE.CREATE_TABLE_FAIL; // 目录创建失败
            }
        }
    }

    public String spawnDirectory(String directory) {
        return spawnDirectory(new File(directory));
    }

    // 初始化
    private String filePath;
    private  String dirPath;
    public String initialize() throws Exception {
        if(FilePath == null) {
            filePath = "./data/database.db";
            dirPath = "./data/database/";
            DATA_PATH = dirPath;
        } else {
            filePath = FilePath + "/data/database.db";
            dirPath = FilePath + "/data/database/";
            DATA_PATH = dirPath;
        }

        // 检查文件是否存在
        String spawnFileResult = spawn(filePath);
        if (spawnFileResult.equals(DATABASE.FILE_EXISTS)) {
            // 文件存在

            // 检查目录是否存在
            String spawnDirResult = spawnDirectory(dirPath);
            if (spawnDirResult.equals(DATABASE.DIRECTORY_EXISTS)) {
                // 目录也存在，抛出重复初始化错误
                throw new Exception("Cannot initialize database: Database already initialized");
            } else if (spawnDirResult.equals(DATABASE.CREATE_TABLE_SUCCESS)) {
                // 目录创建成功
                return DATABASE.FILE_EXISTS; // 返回文件已存在的信息
            } else {
                // 目录创建失败
                return DATABASE.CREATE_TABLE_FAIL;
            }
        } else if (spawnFileResult.equals(DATABASE.CREATE_TABLE_SUCCESS)) {
            // 文件创建成功

            // 检查目录是否存在或创建目录
            String spawnDirResult = spawnDirectory(dirPath);
            if (spawnDirResult.equals(DATABASE.DIRECTORY_EXISTS) || spawnDirResult.equals(DATABASE.CREATE_TABLE_SUCCESS)) {
                // 目录存在或创建成功
                return DATABASE.CREATE_TABLE_SUCCESS;
            } else {
                // 目录创建失败
                return DATABASE.CREATE_TABLE_FAIL;
            }
        } else {
            // 文件创建失败
            return DATABASE.CREATE_TABLE_FAIL;
        }
    }

    /**
     * 检查数据库是否已经初始化
     *
     * @return 是否初始化
     */
    public boolean isInitialized() {
        String sdbFile;
        String sdbDir;
        if(FilePath == null) {
            sdbFile = "./data/database.db";
            sdbDir = "./data/database/";
        } else {
            sdbFile = FilePath + "/data/database.db";
            sdbDir = FilePath + "/data/database/";
        }

        File dbFile = new File(sdbFile);
        File dbDir = new File(sdbDir);

        return dbFile.exists() && dbFile.isFile() && dbDir.exists() && dbDir.isDirectory();
    }

    //自动检测并初始化
    public void AutoInitialize() throws Exception {
        if(!isInitialized()) {
            initialize();
        }else {
            if(FilePath == null) {
                filePath = "./data/database.db";
                dirPath = "./data/database/";
                DATA_PATH = dirPath;
            } else {
                filePath = FilePath + "/data/database.db";
                dirPath = FilePath + "/data/database/";
                DATA_PATH = dirPath;
            }
        }
    }

    public void ResetFilePath() throws Exception {
        if(FilePath == null) {
            filePath = "./data/database.db";
            dirPath = "./data/database/";
            DATA_PATH = dirPath;
        } else {
            filePath = FilePath + "/data/database.db";
            dirPath = FilePath + "/data/database/";
            DATA_PATH = dirPath;
        }
    }

    private String FilePath;
    private String DATA_PATH;

    public Database() {
        FilePath = null;
    }

    /**
     *
     *
     * @param path 数据库文件夹路径,可以使用ePath代指"C:\\Users\\Administrator\\.JDatabase\\"
     */
    public Database(String path) {
        FilePath = path;
    }

    //存在目录
    public boolean isSubDirectoryExists(String parentFolderPath, String subDirectoryName) {
        // 创建父文件夹的File对象
        File parentDir = new File(parentFolderPath);

        // 如果父文件夹不存在或不是一个目录，则直接返回false
        if (!parentDir.exists() || !parentDir.isDirectory()) {
            return false;
        }

        // 创建要查找的子文件夹的File对象
        File subDir = new File(parentDir, subDirectoryName);

        // 检查子文件夹是否存在且是一个目录
        return subDir.exists() && subDir.isDirectory();
    }


    /*
    导入并存储变量功能
     */

    // 确保数据目录存在
    private void ensureDataDirectoryExists() {
        File dataDir = new File(DATA_PATH);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create data directory: " + DATA_PATH);
            }
        }
    }

    // 将任何可序列化的对象存储到二进制文件中
    public <T extends Serializable> void serializeObject(T object, String fileName) {
        ensureDataDirectoryExists();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_PATH + fileName))) {
            oos.writeObject(object);
        } catch (IOException e) {
            e.fillInStackTrace();
            throw new RuntimeException("Failed to serialize and save object to file: " + DATA_PATH + fileName, e);
        }
    }

    // 从二进制文件中读取并反序列化对象
    public <T extends Serializable> T deserializeObject(String fileName, Class<T> clazz) throws FileNotFoundException {
        File file = new File(DATA_PATH + fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + DATA_PATH + fileName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.fillInStackTrace();
            throw new RuntimeException("Failed to deserialize object from file: " + DATA_PATH + fileName, e);
        }
    }

    // 从二进制文件中读取并反序列化对象
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserializeObject(String fileName) throws FileNotFoundException {
        File file = new File(DATA_PATH + fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + DATA_PATH + fileName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.fillInStackTrace();
            throw new RuntimeException("Failed to deserialize object from file: " + DATA_PATH + fileName, e);
        }
    }

}
