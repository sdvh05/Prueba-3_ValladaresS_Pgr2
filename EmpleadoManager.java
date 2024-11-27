/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ArchivosBinarios;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Hp
 */
public class EmpleadoManager {

    private RandomAccessFile rcods, remps;

    /*
        Formato Codigo.emp
        int code
    
        Formato Empleados.emp
        int code
        String Name
        double salary
        long fecha Contratacion
        long fecha Despido
     */
    public EmpleadoManager() {
        try {
            //1- Asegurarar que el Folder Company Exista
            File mf = new File("Company");
            mf.mkdir();
            //2-Instanciar RAFs dentro de Company
            rcods = new RandomAccessFile("Company/codigos.emp", "rw");
            remps = new RandomAccessFile("Company/empleados.emp", "rw");
            //3- Inicializar el Archivo de codigo si es Nuevo
            initCodes();

        } catch (IOException e) {

        }
    }

    private void initCodes() throws IOException {
        if (rcods.length() == 0) {
            //Puntero --> 0
            rcods.writeInt(1);
            //Puntero -->  4 (Un int pesa 4 bytes)
        }
    }

    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        //Puntero --> 0
        rcods.seek(0);
        rcods.writeInt(code + 1);
        return code;
    }

    public void addEmployee(String Name, double Salary) throws IOException {
        //Asegurara que el puntero este en el final del archivo.
        remps.seek(remps.length());
        int code = getCode();
        //P -> 0 //36
        remps.writeInt(code);
        //P -> 4 //40
        remps.writeUTF(Name); //Ana (8bytes)
        //P -> 12
        remps.writeDouble(Salary);
        //P -> 20
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        //P -> 28
        remps.writeLong(0);
        //P -> 36 E0F
        //Asegurar folder y archivos indivuduales 
        createEmployeeFolders(code);
    }

    private String EmployeeFolder(int code) {
        return "company/empleado" + code;
    }

    private void createEmployeeFolders(int code) throws IOException {
        //Crear folder employe + code
        File edir = new File(EmployeeFolder(code));
        edir.mkdir();
        //crear el archivo de ventas
        createYearSalesFileFor(code);
    }

    private RandomAccessFile salesFileFor(int code) throws IOException {
        String dirPadre = EmployeeFolder(code);
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String Path = dirPadre + "/ventas" + yearActual + ".emp";
        return new RandomAccessFile(Path, "rw");
    }

    private void createYearSalesFileFor(int code) throws IOException {
        RandomAccessFile ryear = salesFileFor(code);
        if (ryear.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                ryear.writeDouble(0);
                ryear.writeBoolean(false);
            }
        }
    }

    public void employeeList() throws IOException {
        remps.seek(0); //Puntero al Inicio para leer todo desde el comienzo

        while (remps.getFilePointer() < remps.length()) {
            int code = remps.readInt();
            String name = remps.readUTF();
            double salary = remps.readDouble();
            Date dateH = new Date(remps.readLong());

            if (remps.readLong() == 0) {
                System.out.println("--------------------------------------------------------------------------------------------------------------------");
                System.out.println("Codigo: " + code + " Nombre: " + name + " Salario: Lps." + salary + " Contratado: " + dateH);
                System.out.println("--------------------------------------------------------------------------------------------------------------------\n");
            }
        }
    }

    private boolean isEmployeeActive(int code) throws IOException { //Funcion que me deja el puntero despues del codigo de los empleados activos
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            long pos = remps.getFilePointer(); // guarda la poscicion donde esta el codigo (Para leer todo lo que va despues del codigo)
            remps.readUTF();
            remps.skipBytes(16); //nos saltamos 16 bytes, lo cual equivale al double salay y long contratacion

            if (remps.readLong() == 0 && codigo == code) {
                remps.seek(pos);
                return true;
            }
        }
        return false;
    }

    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) { // el puntero ya queda despues del codigo con el empleado activo
            String name = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiento a: " + name);
            return true;
        }
        return false;
    }

    public void addSaleToEmployee(int code, double monto) throws IOException {
        if (isEmployeeActive(code)) {
            RandomAccessFile salesFile = salesFileFor(code);
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);

            salesFile.seek(month * 9);
            double currentSales = salesFile.readDouble();
            salesFile.seek(month * 9);
            salesFile.writeDouble(currentSales + monto);

            System.out.println("Venta agregada al empleado " + code + " por Lps." + monto);
            salesFile.close();
        } else {
            System.out.println("Empleado no activo o inexistente.");
        }
    }

    public void payEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            if (isEmployeeActive(code)) {
                RandomAccessFile salesFile = salesFileFor(code);
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);

                salesFile.seek(month * 9 + 8);
                if (salesFile.readBoolean()) {
                    System.out.println("El empleado ya ha sido pagado este mes.");
                    return;
                }

                salesFile.seek(month * 9);
                double totalSales = salesFile.readDouble();
                salesFile.seek(month * 9 + 8);
                salesFile.writeBoolean(true);

                remps.seek(0);
                while (remps.getFilePointer() < remps.length()) {
                    int codigo = remps.readInt();
                    String name = remps.readUTF();
                    double salary = remps.readDouble();
                    long dateH = remps.readLong();
                    long dateD = remps.readLong();

                    if (codigo == code && dateD == 0) {
                        double commission = totalSales * 0.1;
                        double baseSalary = salary + commission;
                        double deduction = baseSalary * 0.035;
                        double netSalary = baseSalary - deduction;

                        File receiptFile = new File(EmployeeFolder(code) + "/recibos.emp");
                        try (RandomAccessFile receipt = new RandomAccessFile(receiptFile, "rw")) {
                            receipt.seek(receipt.length());
                            receipt.writeLong(System.currentTimeMillis());
                            receipt.writeDouble(commission);
                            receipt.writeDouble(baseSalary);
                            receipt.writeDouble(deduction);
                            receipt.writeDouble(netSalary);
                            receipt.writeInt(calendar.get(Calendar.YEAR));
                            receipt.writeInt(month);
                        }

                        System.out.println("Pago realizado a " + name + ". Sueldo neto: Lps." + netSalary);
                        return;
                    }
                }
            } else {
                System.out.println("Empleado no activo o inexistente.");
            }
        }
    }

    public void printEmployee(int code) throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            String name = remps.readUTF();
            double salary = remps.readDouble();
            long dateH = remps.readLong();
            long dateD = remps.readLong();

            if (codigo == code) {
                System.out.println("DATOS del Usuario");
                System.out.println("\n------------------------------------------------");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + name);
                System.out.println("Salario: Lps." + salary);
                System.out.println("Fecha de contratacion: " + new Date(dateH));
                if (dateD > 0) {
                    System.out.println("Fecha de despido: " + new Date(dateD));
                    System.out.println("------------------------------------------------\n");
                } else {
                    System.out.println("Empleado activo");
                    System.out.println("------------------------------------------------\n");
                }

                RandomAccessFile salesFile = salesFileFor(code);
                double totalSales = 0;
                
                
                System.out.println("Ventas anuales:");
                System.out.println("------------------------------------------------");
                for (int i = 0; i < 12; i++) {
                    salesFile.seek(i * 9);
                    double monthlySales = salesFile.readDouble();
                    totalSales += monthlySales;
                    System.out.println("Mes " + (i + 1) + ": Lps." + monthlySales);
                }
                System.out.println("---------------------------------------");
                System.out.println("Total ventas anuales: Lps." + totalSales);
                System.out.println("------------------------------------------------");

                File receiptFile = new File(EmployeeFolder(code) + "/recibos.emp");
                if (receiptFile.exists()) {
                    try (RandomAccessFile receipt = new RandomAccessFile(receiptFile, "r")) {
                        System.out.println("\nRecibos historicos:");
                        while (receipt.getFilePointer() < receipt.length()) {
                            long date = receipt.readLong();
                            double commission = receipt.readDouble();
                            double baseSalary = receipt.readDouble();
                            double deduction = receipt.readDouble();
                            double netSalary = receipt.readDouble();

                            System.out.println("-----------------------------------");
                            System.out.println("Fecha de pago: " + new Date(date)   );
                            System.out.println("Comisión: Lps." + commission        );
                            System.out.println("Sueldo base: Lps." + baseSalary     );
                            System.out.println("Deducción: Lps." + deduction        );
                            System.out.println("Sueldo neto: Lps." + netSalary      );
                            System.out.println("-----------------------------------\n");
                        }
                    } catch (IOException e) {
                        System.out.println(".");
                    }
                } else {
                    System.out.println("No hay recibos historicos para este empleado.");
                }
                return;
            }
        }
        System.out.println("Empleado con el código " + code + " no encontrado.");
    }
}
