AMIKO devices are BLE 4.0 compliant modules.

The solutions is based on a chip made by NORDIC Semiconductors: serie nRF51
https://www.nordicsemi.com/ (daniele.prestianni@gmail.com / drfmdrfm)

In order to communicate with AMIKO devices, we need a full Java based stack compliant with all the main 
Operative Systems: Windows, Linux and Mac.

After a technological scouting activity, we've realized that doesn't exist a Java library which is able 
to interact to the Windows Bluetooth stack. 

A solution could be composed on this stack:
1) for WINDOWS
- Windows >8.0
- BlueGiga BLED112 Dongle
- BGAPI
- JAVA App 

BlueGiga BLED112 Dongle:
The USB dongle has a virtual COM port that enables seamless host application development using a simple 
application programming interface. The BLED112 can be used for Bluetooth Smart development.
The Bluegiga BLED112 dongle is compelling because it has a self-contained BLE stack and doesn’t require 
any software support on the host computer. The adapter uses the proprietary but well-documented BGAPI 
from Bluegiga and Jeff Rowberg (@jrowberg) already implemented it in Python for us 
(fonte:http://christopherpeplin.com/2015/10/22/pygatt-python-bluetooth-low-energy-ble/)
- https://www.bluegiga.com/en-US/products/bled112-bluetooth-smart-dongle/#documentation
- http://www.amazon.co.uk/BLED112-Bluegiga-Technologies-Bluetooth-ELECTRONICS/dp/B017UUUAPM
- http://christopherpeplin.com/2015/10/22/pygatt-python-bluetooth-low-energy-ble/
- http://mgrfq63796.lithium.com/t5/Wireless/BLED112-device-drivers-for-Linux/td-p/155758


 
BGAPI: This JAVA API allow interfacing with Bluegiga Bluetooth Low Energy modules (BLE112) and the 
Blugiga Bluetooth Low Energy USB Dongle (BLED112).
With its forum, BlueGiga advices the BGAPI Java implementation: 
https://bluegiga.zendesk.com/entries/41760433-Doing-my-own-packet-sniffer-application-in-Java

- https://github.com/SINTEF-9012/bglib
- https://github.com/dukeboard/kevoree-extra/tree/master/org.kevoree.extra.osgi.rxtx
- http://mgrfq63796.lithium.com/t5/Wireless/BLED112-device-drivers-for-Linux/td-p/155758
- https://bluegiga.zendesk.com/entries/25053373--REFERENCE-BLE-master-slave-GATT-client-server-and-data-RX-TX-basics
- http://ask.programmershare.com/168_21900838/
- http://stackoverflow.com/questions/21883950/read-write-error-in-bgapi-for-ble112-modules-by-bluegiga

NOTE: after v8.0 Windows supports BLE 4.0 - https://msdn.microsoft.com/en-us/library/windows/hardware/jj159880%28v=vs.85%29.aspx
NOTE: BLED112 Windows Driver: https://www.bluegiga.com/en-US/download/?file=b2lWNsg0R1SG8qXOhOO1LA&title=BLED112%2520Windows%2520Driver&filename=BLED112_Signed_Win_Drv.zip

ATTENZIONE: 
osservando la classe BLED112 si potrà notare che questa fà uso di librerie native che ricava 
da un path del tipo "nativelib/<Platform>" (es. nativelib/Windows/win32/rxtxSerial.dll)
Queste librerie le troviamo all'interno del JAR recuperato mediante MAVEN e presente nel path:
C:\Users\Admin\.m2\repository\org\kevoree\extra\org.kevoree.extra.osgi.rxtx\2.2.0
Il repository di tale JAR è il seguente:
https://github.com/dukeboard/kevoree-extra


-------------------------------------------
Ricavare la EPOC date:
java.util
Class Date

public setTime(long time)
Sets this Date object to represent a point in time that is time milliseconds after January 1, 1970 00:00:00 GMT.

public long getTime()
Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object.
-------------------------------------------
Scrivere un file di testo
http://stackoverflow.com/questions/15754523/how-to-write-text-file-java
-------------------------------------------
Conversione da byte/Long e viceversa: http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java

public static byte[] longToBytes(long l) {
    byte[] result = new byte[8];
    for (int i = 7; i >= 0; i--) {
        result[i] = (byte)(l & 0xFF);
        l >>= 8;
    }
    return result;
}

public static long bytesToLong(byte[] b) {
    long result = 0;
    for (int i = 0; i < 8; i++) {
        result <<= 8;
        result |= (b[i] & 0xFF);
    }
    return result;
}
---------
You could use the implementation in org.apache.hadoop.hbase.util.Bytes http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/util/Bytes.html

The source code is here:

http://grepcode.com/file/repository.cloudera.com/content/repositories/releases/com.cloudera.hbase/hbase/0.89.20100924-28/org/apache/hadoop/hbase/util/Bytes.java#Bytes.toBytes%28long%29

Look for the toLong and toBytes methods.
---------
class Main
{

        public static byte[] long2byte(long l) throws IOException
        {
        ByteArrayOutputStream baos=new ByteArrayOutputStream(Long.SIZE/8);
        DataOutputStream dos=new DataOutputStream(baos);
        dos.writeLong(l);
        byte[] result=baos.toByteArray();
        dos.close();    
        return result;
        }


        public static long byte2long(byte[] b) throws IOException
        {
        ByteArrayInputStream baos=new ByteArrayInputStream(b);
        DataInputStream dos=new DataInputStream(baos);
        long result=dos.readLong();
        dos.close();
        return result;
        }


        public static void main (String[] args) throws java.lang.Exception
        {

         long l=123456L;
         byte[] b=long2byte(l);
         System.out.println(l+": "+byte2long(b));       
        }
}
---------
If you are looking for a fast unrolled version, this should do the trick, assuming a byte array called "b" with a length of 8:

long l = ((((long) b[7]) << 56) | (((long) b[6] & 0xff) << 48) | (((long) b[5] & 0xff) << 40)
            | (((long) b[4] & 0xff) << 32) | (((long) b[3] & 0xff) << 24) | (((long) b[2] & 0xff) << 16)
            | (((long) b[1] & 0xff) << 8) | (((long) b[0] & 0xff)));

---------






