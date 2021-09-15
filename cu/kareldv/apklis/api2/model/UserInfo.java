package cu.kareldv.apklis.api2.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import cu.kareldv.apklis.api2.Session;
import cu.kareldv.apklis.api2.utils.Province;

/**
 * Clase que contiene la informacion del usuario que accedio a la {@link Session}
 * @see Session
 */
public final class UserInfo implements Serializable{
    private Session session;
    private String user, name, phone, email, id, avatar, last_name, sha1, fullName;
    private boolean isDeveloper, isDeveloperActive, salesAdmin, betaTester;
    private Province province;

    /**Interno*/
    public UserInfo(Session s, String json) throws JSONException {
        this(s, new JSONObject(json));
    }

    /**Interno*/
    protected UserInfo(Session s, JSONObject obj) throws JSONException{
        this.session=s;
        this.id=obj.getString("id");
        this.user=obj.getString("username");
        this.name=obj.getString("first_name");
        this.last_name=obj.getString("last_name");
        this.avatar=obj.getString("avatar");
        this.phone=obj.getString("phone_number");
        this.email=obj.getString("email");
        this.sha1=obj.getString("sha1");
        this.isDeveloper=obj.getBoolean("is_developer");
        this.isDeveloperActive=obj.getBoolean("is_developer_active");
        this.salesAdmin=obj.getBoolean("sales_admin");
        this.betaTester=obj.getBoolean("beta_tester");
        this.province=Province.fromNumber(obj.getInt("province"));
        this.fullName=name+" "+last_name;
    }

    /**
     * Devuelve el nombre de usuario
     */
    public String getUser() {
        return user;
    }

    /**
     * Devuelve el nombre del usuario
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve el numero telefonico del usuario
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Correo del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * INTERNO
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve la url del icono del usuario
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Devuelve los Apellidos del usuario
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * Devuelve el SHA1 del usuario
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * Devuelve el nombre completo del usuario
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Devuelve si puede acceder a la consola de ventas
     */
    public boolean isSalesAdmin() {
        return salesAdmin;
    }

    /**
     * Devuelve si es un beta tester
     */
    public boolean isBetaTester() {
        return betaTester;
    }

    /**
     * Provincia del usuario
     */
    public Province getProvince() {
        return province;
    }

    /**
     * Devuelve si has accedido a apklis admin
     */
    public boolean isDeveloper() {
        return isDeveloper;
    }

    /**
     * Devuelve si tu cuenta esta activa
     */
    public boolean isDeveloperActive() {
        return isDeveloperActive;
    }

    /**
     * Devuelve un asistente para realizar cambios en tu informacion
     */
    public UserPatchWizard patchWizard(){
        return new UserPatchWizard(session);
    }
    
    public String toString() {
        return "UserInfo[user:"+user+", name:"+name+", phone:"+phone+", email:"+email+", id:"+id+", isDev: "+isDeveloper+", isDevActive:"+isDeveloperActive+"]";
    }
}
