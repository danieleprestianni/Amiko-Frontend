package io.amiko.app.utils.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Vector;

public class DataTable<T> {

	private ArrayList<String> header = null;
	private ArrayList<Method> dataTypeMethods = null;
	private Vector<T> data = new Vector<>();

	public void addRow(T rowValue) {
		if (this.header == null) {
			this.getHeader(rowValue);
		}
		this.data.add(rowValue);
	}

	private void getHeader(T rowValue) {
		this.header = new ArrayList<>();
		this.dataTypeMethods = new ArrayList<>();

		Class<?> cls = rowValue.getClass();

		Method methods[] = cls.getMethods();
		for (Method m : methods) {
			String name = m.getName();
			if (isGetter(m)) {
				this.header.add(name.substring(3));
				this.dataTypeMethods.add(m);
			}
		}
	}

	public static boolean isGetter(Method method) {
		if (!method.getName().startsWith("get"))
			return false;
		if (method.getName().equalsIgnoreCase("getClass"))
			return false;
		if (method.getParameterTypes().length != 0)
			return false;
		if (void.class.equals(method.getReturnType()))
			return false;
		return true;
	}

	public static boolean isSetter(Method method) {
		if (!method.getName().startsWith("set"))
			return false;
		if (method.getParameterTypes().length != 1)
			return false;
		return true;
	}

	public static void main(String args[]) {
		DataTable<DataModel> dataTable = new DataTable<DataModel>();

		DataModel rowValue = new DataModel();
		rowValue.setName("Daniele");
		rowValue.setValue(1);
		rowValue.setSurname("Prestianni");
		dataTable.addRow(rowValue);

		dataTable.exportAsCSV(new File("test.csv"));
	}

	public void exportAsCSV(File destFile) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(destFile));

			// write the header
			String header = "";
			for (String h : this.header) {
				header += h + ",";
			}
			header = header.substring(0, header.length() - 1);
			writer.write(header + "\n");

			// write all Data
			for (T rowData : this.data) {
				String dataRow = "";
				for (Method m : this.dataTypeMethods) {
					try {
						dataRow += m.invoke(rowData) + ",";
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				dataRow = dataRow.substring(0, dataRow.length() - 1);
				writer.write(dataRow + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}
	
	public void exportAsCSV(File destFile, ArrayList<String> order) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(destFile));

			// write the header
			String header = "";
			for(String h: order){
				header += h + ",";
			}			
			header = header.substring(0, header.length() - 1);
			writer.write(header + "\n");
			
			// write all Data
			for (T rowData : this.data) {
				String dataRow = "";
				for(String field:order){
					int i = this.header.indexOf(field);					
					Method m = this.dataTypeMethods.get(i);
					try {
						dataRow += m.invoke(rowData) + ",";
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				dataRow = dataRow.substring(0, dataRow.length() - 1);
				writer.write(dataRow + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}

}
