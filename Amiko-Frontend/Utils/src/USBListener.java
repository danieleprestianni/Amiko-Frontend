import java.util.*;
import javax.usb.*;
public class USBListener {
  public static void main(String[] args) throws UsbException {
    UsbServices services = UsbHostManager.getUsbServices();
    UsbHub root = services.getRootUsbHub();
    listPeripherique(root);
  }
  public static void listPeripherique(UsbHub hub) {
    List perepheriques = hub.getAttachedUsbDevices();
    Iterator iterator = perepheriques.iterator();
    while (iterator.hasNext()) {
      UsbDevice perepherique = (UsbDevice) iterator.next();
      System.out.println(perepherique);
      if (perepherique.isUsbHub()) {
        listPeripherique((UsbHub) perepherique);
      }
    }
  }
}