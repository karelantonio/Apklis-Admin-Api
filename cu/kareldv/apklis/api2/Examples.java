package cu.kareldv.apklis.api2;

import java.util.Scanner;

import cu.kareldv.apklis.api2.model.Application;

/**
 * Clase que muestra ejemplos de uso de esta api, recuerda que casi toda esta api es experimental asi que algunas partes pueden funcionar de forma inesperada, Disfrutalo!
 */
public class Examples {
    /**
     * Ejemplo de uso del login
     */
    public static final void simpleLogin() throws Exception{
        Scanner sc = new Scanner(System.in);
        String user = null,
                pass = null;

        System.out.print("Introduzca su nombre de usuario: ");
        user=sc.nextLine();
        System.out.print("Introduzca su contraseña: ");
        pass=sc.nextLine();

        try{
            Session s = new Session(user, pass);
            //ALERTA: en android necesitaras llamar este metodo en un hilo aparte, porque
            //realiza tareas de red y obtendrias un: NetworkOnMainThreadException
            s.updateCache();
            System.out.println("Hola "+s.getUserInfo().getName()+"!");
        }catch(Throwable e){
            System.err.println("Error accediendo: "+e);
        }
    }

    /**
     * Ejemplo de uso de obtener tus apps
     */
    public static final void getApps(){
        Scanner sc = new Scanner(System.in);
        String user = null,
                pass = null;

        System.out.print("Introduzca su nombre de usuario: ");
        user=sc.nextLine();
        System.out.print("Introduzca su contraseña: ");
        pass=sc.nextLine();

        try{
            Session s = new Session(user, pass);
            //ALERTA: en android necesitaras llamar este metodo en un hilo aparte, porque
            //realiza tareas de red y obtendrias un: NetworkOnMainThreadException
            s.updateCache();
            System.out.println("Buscando tus apps, "+s.getUserInfo().getName()+"...");
            int pos = 0;
            for (Application app:
                s.getOwnApps()){
                System.out.println("App #"+(++pos)+": "+app.getName()+", con un promedio de "+app.getRating()+" estrellas");
            }
        }catch(Throwable e){
            System.err.println("Error accediendo: "+e);
        }
    }
}
