/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ArchivosBinarios;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Hp
 */


public class EmpleadoMain {
    public static void main(String[] args) {
        EmpleadoManager manager = new EmpleadoManager();
        Scanner lea = new Scanner(System.in);
        int menu=0;


        do {
            System.out.println("\n\n*****MENU*****");
            System.out.println("|-------------------------------------|");
            System.out.println("| 1- Agregar Empleado                 |");
            System.out.println("| 2- Listar Empleados no Despedidos   |");
            System.out.println("| 3- Agregar Venta al Empleado        |");
            System.out.println("| 4- Pagar Empleado                   |");
            System.out.println("| 5- Despedir Empleado                |");
            System.out.println("| 6- Imprimir Informacion del Empleado|");
            System.out.println("| 7- Salir                            |");
            System.out.println("|-------------------------------------|");

            System.out.print("Ingrese su opcion: ");

            try {
                menu = lea.nextInt();
                lea.nextLine(); 

                switch (menu) {
                    case 1:
                        try {
                            System.out.print("Ingrese el nombre del empleado: ");
                            String nombre = lea.nextLine();
                            System.out.print("Ingrese el salario del empleado: ");
                            double salario = lea.nextDouble();
                            manager.addEmployee(nombre, salario);
                            System.out.println("Empleado agregado correctamente.");
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Entrada invalida. El salario debe ser un numero.");
                            lea.nextLine();
                        }
                        break;

                    case 2:
                        System.out.println("\nLista de Empleados Activos:");
                        manager.employeeList();
                        break;

                    case 3:
                        try {
                            System.out.print("Ingrese el codigo del empleado: ");
                            int codigoVenta = lea.nextInt();
                            System.out.print("Ingrese el monto de la venta: ");
                            double monto = lea.nextDouble();
                            manager.addSaleToEmployee(codigoVenta, monto);
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Entrada invalida. Asegúrese de ingresar numeros validos.");
                            lea.nextLine();
                        }
                        break;

                    case 4:
                        try {
                            System.out.print("Ingrese el codigo del empleado: ");
                            int codigoPago = lea.nextInt();
                            manager.payEmployee(codigoPago);
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Codigo de empleado invalido.");
                            lea.nextLine();
                        }
                        break;

                    case 5:
                        try {
                            System.out.print("Ingrese el codigo del empleado a despedir: ");
                            int codigoDespedir = lea.nextInt();
                            if (manager.fireEmployee(codigoDespedir)) {
                                System.out.println("Empleado despedido correctamente.");
                            } else {
                                System.out.println("No se pudo despedir al empleado. Verifique el codigo.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Entrada invalida. El código debe ser un numero entero.");
                            lea.nextLine();
                        }
                        break;

                    case 6:
                        try {
                            System.out.print("Ingrese el código del empleado: ");
                            int codigoImprimir = lea.nextInt();
                            manager.printEmployee(codigoImprimir);
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Codigo de empleado invalido.");
                            lea.nextLine();
                        }
                        break;

                    case 7:
                        System.out.println("Saliendo del sistema...");
                        lea.close();
                        break;

                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
                
            } catch (InputMismatchException e) {
                System.out.println("Error: Entrada inválida. Por favor, ingrese un número del menú.");
                lea.nextLine();
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }while(menu!=7);
    }
}
