import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class WinRegistry {

    public static final int HKEY_CURRENT_USER = 0x80000001,
            HKEY_LOCAL_MACHINE = 0x80000002,
            REG_SUCCESS = 0,
            REG_NOTFOUND = 2,
            REG_ACCESSDENIED = 5,
            KEY_ALL_ACCESS = 0xf003f,
            KEY_READ = 0x20019;
    private static final Preferences userRoot = Preferences.userRoot(),
            systemRoot = Preferences.systemRoot();
    private static final Class<? extends Preferences> userClass = userRoot.getClass();
    private static Method regOpenKey,
            regCloseKey,
            regQueryValueEx,
            regEnumValue,
            regQueryInfoKey,
            regEnumKeyEx,
            regCreateKeyEx,
            regSetValueEx,
            regDeleteKey,
            regDeleteValue;

    static {
        try {
            (regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", new Class[]{int.class, byte[].class, int.class})).setAccessible(true);
            (regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", new Class[]{int.class})).setAccessible(true);
            (regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[]{int.class, byte[].class})).setAccessible(true);
            (regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue", new Class[]{int.class, int.class, int.class})).setAccessible(true);
            (regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", new Class[]{int.class})).setAccessible(true);
            (regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", new Class[]{int.class, int.class, int.class})).setAccessible(true);
            (regCreateKeyEx = userClass.getDeclaredMethod("WindowsRegCreateKeyEx", new Class[]{int.class, byte[].class})).setAccessible(true);
            (regSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx", new Class[]{int.class, byte[].class, byte[].class})).setAccessible(true);
            (regDeleteValue = userClass.getDeclaredMethod("WindowsRegDeleteValue", new Class[]{int.class, byte[].class})).setAccessible(true);
            (regDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey", new Class[]{int.class, byte[].class})).setAccessible(true);
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(WinRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Read a value from key and value name
     *
     * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
     * @param key
     * @param valueName
     * @return the value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static String readString(int hkey, String key, String valueName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                return readString(systemRoot, hkey, key, valueName);
            case HKEY_CURRENT_USER:
                return readString(userRoot, hkey, key, valueName);
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    /**
     * Read value(s) and value name(s) form given key
     *
     * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
     * @param key
     * @return the value name(s) plus the value(s)
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Map<String, String> readStringValues(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                return readStringValues(systemRoot, hkey, key);
            case HKEY_CURRENT_USER:
                return readStringValues(userRoot, hkey, key);
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    /**
     * Read the value name(s) from a given key
     *
     * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
     * @param key
     * @return the value name(s)
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static List<String> readStringSubKeys(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                return readStringSubKeys(systemRoot, hkey, key);
            case HKEY_CURRENT_USER:
                return readStringSubKeys(userRoot, hkey, key);
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    /**
     * Create a key
     *
     * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
     * @param key
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int[] ret;
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                ret = createKey(systemRoot, hkey, key);
                regCloseKey.invoke(systemRoot, new Object[]{ret[0]});
                break;
            case HKEY_CURRENT_USER:
                ret = createKey(userRoot, hkey, key);
                regCloseKey.invoke(userRoot, new Object[]{ret[0]});
                break;
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }

        if (ret[1] != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + ret[1] + " key=" + key);
        }
    }

    /**
     * Write a value in a given key/value name
     *
     * @param hkey
     * @param key
     * @param valueName
     * @param value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void writeStringValue(int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                writeStringValue(systemRoot, hkey, key, valueName, value);
                break;
            case HKEY_CURRENT_USER:
                writeStringValue(userRoot, hkey, key, valueName, value);
                break;
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    /**
     * Delete a given key
     *
     * @param hkey
     * @param key
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void deleteKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int rc = -1;
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                rc = deleteKey(systemRoot, hkey, key);
                break;
            case HKEY_CURRENT_USER:
                rc = deleteKey(userRoot, hkey, key);
        }

        if (rc != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + rc + " key=" + key);
        }
    }

    /**
     * delete a value from a given key/value name
     *
     * @param hkey
     * @param key
     * @param value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void deleteValue(int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int rc = -1;
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                rc = deleteValue(systemRoot, hkey, key, value);
                break;
            case HKEY_CURRENT_USER:
                rc = deleteValue(userRoot, hkey, key, value);
        }

        if (rc != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + rc + " key=" + key + " value=" + value);
        }
    }

    private static int deleteValue(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{hkey, toCstr(key), KEY_ALL_ACCESS});
        if (handles[1] != REG_SUCCESS) {
            return handles[1];//Can be REG_NOTFOUND, REG_ACCESSDENIED
        }
        int rc = ((Integer) regDeleteValue.invoke(root, new Object[]{handles[0], toCstr(value)}));
        regCloseKey.invoke(root, new Object[]{handles[0]});
        return rc;
    }

    private static int deleteKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int rc = ((Integer) regDeleteKey.invoke(root, new Object[]{hkey, toCstr(key)}));
        return rc;  //Can be REG_NOTFOUND, REG_ACCESSDENIED, REG_SUCCESS
    }

    private static String readString(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{hkey, toCstr(key), KEY_READ});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[]{handles[0], toCstr(value)});
        regCloseKey.invoke(root, new Object[]{handles[0]});
        return (valb != null ? new String(valb).trim() : null);
    }

    private static Map<String, String> readStringValues(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        HashMap<String, String> results = new HashMap<>();
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{hkey, toCstr(key), KEY_READ});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[]{handles[0]});

        int count = info[0]; //Count  
        int maxlen = info[3]; //Max value length
        for (int index = 0; index < count; index++) {
            byte[] name = (byte[]) regEnumValue.invoke(root, new Object[]{handles[0], index, maxlen + 1});
            String value = readString(hkey, key, new String(name));
            results.put(new String(name).trim(), value);
        }
        regCloseKey.invoke(root, new Object[]{handles[0]});
        return results;
    }

    private static List<String> readStringSubKeys(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        List<String> results = new ArrayList<>();
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{hkey, toCstr(key), KEY_READ});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[]{handles[0]});

        int count = info[0];//Count
        int maxlen = info[3]; //Max value length
        for (int index = 0; index < count; index++) {
            byte[] name = (byte[]) regEnumKeyEx.invoke(root, new Object[]{handles[0], index, maxlen + 1});
            results.add(new String(name).trim());
        }
        regCloseKey.invoke(root, new Object[]{handles[0]});
        return results;
    }

    private static int[] createKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        return (int[]) regCreateKeyEx.invoke(root, new Object[]{hkey, toCstr(key)});
    }

    private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{hkey, toCstr(key), KEY_ALL_ACCESS});
        regSetValueEx.invoke(root, new Object[]{handles[0], toCstr(valueName), toCstr(value)});
        regCloseKey.invoke(root, new Object[]{handles[0]});
    }

    private static byte[] toCstr(String str) {

        byte[] result = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
    
    public static String getFriendlyName(String registryKey) {
        if (registryKey == null || registryKey.isEmpty()) {
            throw new IllegalArgumentException("'registryKey' null or empty");
        }
        try {
            int hkey = WinRegistry.HKEY_LOCAL_MACHINE;
            return WinRegistry.readString(hkey, registryKey, "FriendlyName");
        } catch (Exception ex) { // catch-all: 
            // readString() throws IllegalArg, IllegalAccess, InvocationTarget
            System.err.println(ex.getMessage());
            return null;
        }
    }
    
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
    	String keyPath = "SYSTEM\\CurrentControlSet\\Enum\\USB\\VID_2458&PID_0001\\";
    	List<String> subKeys = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, keyPath);
        ArrayList<Integer> comPorts = new ArrayList<Integer>();
        for (String subKey : subKeys) {
            String friendlyName = getFriendlyName(keyPath + subKey);
            if (friendlyName != null && friendlyName.contains("Bluegiga") && friendlyName.contains("COM")) {
                System.out.println("Find key=" + subKey + " friendlyName=" + friendlyName);
            	int beginIndex = friendlyName.indexOf("COM") + 3 /*length of 'COM'*/;
                int endIndex = friendlyName.indexOf(")");
                comPorts.add(Integer.parseInt(friendlyName.substring(beginIndex, endIndex)));
            }
        }
    }
}