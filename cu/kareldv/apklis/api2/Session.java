package cu.kareldv.apklis.api2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;

import cu.kareldv.apklis.api2.exceptions.InternalServerException;
import cu.kareldv.apklis.api2.exceptions.ServiceUnavailableException;
import cu.kareldv.apklis.api2.internal.URLConfiguration;
import cu.kareldv.apklis.api2.model.Application;
import cu.kareldv.apklis.api2.model.Categories;
import cu.kareldv.apklis.api2.model.DashboardStats;
import cu.kareldv.apklis.api2.model.PushAppTask;
import cu.kareldv.apklis.api2.utils.Base64;
import cu.kareldv.apklis.api2.utils.Constants;
import cu.kareldv.apklis.api2.exceptions.UserNotFoundException;
import cu.kareldv.apklis.api2.internal.Internal;
import cu.kareldv.apklis.api2.model.UserInfo;
import cu.kareldv.apklis.api2.utils.Province;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Clase que controla todas las acciones como registrarse, acceder, obtener informacion, publicar app [EXPERIMENTAL], [EN_DESARROLLO]
 * @see Examples
 */
public final class Session implements Serializable {
    public static final String CLIENT_ID = "7UMqh5p9CPPPEPi0s4Ksrv2vR5tBjBJH8XlaNMMB";

    //--------------VARIABLES-------------
    private String userName, password, accesToken, refreshToken, tokenType, scope;
    private int expiration, requestedTime;
    //---------VARIABLES EN CACHE---------
    private transient UserInfo userInfo;
    private transient ArrayList<Application> apps;
    private transient Categories categories;
    private transient DashboardStats dashboardStats;
    //-----------CONFIGURACION------------
    private URLConfiguration cfgUserInfo, cfgDashboardStats;

    /**
     * Constructor por defecto, crea una Session utilizando las credenciales dadas
     * @param username      Nombre de usuario
     * @param password      Contraseña a ser usada
     * @throws Exception    Lanza una excepcion si ocurrio un error durante la comunicacion, errores internos del servidor o problemas con las credenciales, entre otras
     */
    public Session(String username, String password)throws Exception{
        this(username, password, _static_constructor(username, password));
    }

    Session(){}

    /**
     * Constructor privado, carga la sesion guardada en la cadena en formato JSON
     * @param user          Usuario (Solo para ser almacenado)
     * @param p             Contraseña (Soo para ser almacenada)
     * @param json          Datos de la sesion en formato JSON
     * @throws Exception    Lanza una excepcion si los datos en formato JSON contienen algun valor incorrecto o estan en un formato incorrecto
     * @see #Session(String, String)
     */
    Session(String user, String p, String json) throws Exception{
        this(user, p, new JSONObject(json));
    }

    /**
     * Constructor privado, carga la sesion guardada en el objeto en formato JSON
     * @param user          Usuario (Solo para ser almacenado)
     * @param p             Contraseña (Soo para ser almacenada)
     * @param obj           Datos de la sesion en formato JSON
     * @throws Exception    Lanza una excepcion si los datos en formato JSON contienen algun valor incorrecto o estan en un formato incorrecto
     * @see #Session(String, String)
     */
    Session(String user, String p, JSONObject obj) throws Exception {
        this.accesToken=obj.getString("access_token");
        this.refreshToken=obj.getString("refresh_token");
        this.scope=obj.getString("scope");
        this.tokenType=obj.getString("token_type");

        this.expiration=obj.getInt("expires_in");
        this.requestedTime=(int) System.currentTimeMillis();

        this.userName=user;
        this.password=p;

        this.cfgUserInfo =new URLConfiguration(Constants.V1.URL_API_USER_INFO(userName), "GET", "authorization");

        _resolveUserInfo();
    }

    static JSONObject _static_constructor(String user, String pass) throws Exception {
        Request.Builder b = new Request.Builder();
        b.url("https://api.apklis.cu/o/token/");
        b.post(new FormBody.Builder(Charset.forName("UTF-8"))
                .add("grant_type", "password")
                .add("client_id", Constants.AUTHORIZATION)
                .add("username", user)
                .add("password", pass)
                .build());
        Response r = Internal.req(b.build());

        if(r.isSuccessful())
            return new JSONObject(r.body().string());
        else CommonError.throwEx(r);
        return null;
    }

    /**
     * Devuelve una sesion guardada en el argumento "crypt"
     * @param crypt Datos de la sesion
     * @return      Una nueva sesion a partir de la cadena dada como argumento 
     * @see #toStoreString() 
     */
    public static Session fromStore(String crypt){
        try {
            JSONObject obj = new JSONObject(new String(Base64.getDecoder().decode(crypt)));

            Session s = new Session();
            s.userName=obj.getString("username");
            s.password=obj.getString("password");
            s.accesToken=obj.getString("access_token");
            s.refreshToken=obj.getString("refresh_token");
            s.tokenType=obj.getString("token_type");
            s.scope=obj.getString("scope");

            return s;
        } catch (Exception ex) {
            //throw new Error(ex);
            return null;
        }
    }

    /**
     * Guarda la sesion actual para ser guardada despues
     * @return  Los datos de la sesion actual
     * @see #fromStore(String)
     */
    public String toStoreString(){
        try {
            return new String(Base64.getEncoder().encode(new JSONObject()
                    .put("access_token", accesToken)
                    .put("refresh_token", refreshToken)
                    .put("scope", scope)
                    .put("token_type", tokenType)
                    .put("expires_in", expiration)
                    .put("username", userName)
                    .put("password", password)
                    .toString().getBytes()));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    //--------------GETTERS-----------------

    /**
     * Crea una nueva instancia de {@code PushAppTask} para publicar una app. EXPERIMENTAL
     * @return  {@code new PushAppTask(toAuthorization)}
     */
    public PushAppTask newUploadTask(){
        return new PushAppTask(toAuthorization());
    }

    /**
     * Devuelve los datos del desarrollador actual
     * @return  Los datos del desarrollador actual. NULLABLE
     */
    public UserInfo getUserInfo(){
        return userInfo;
    }

    /**
     *  INTERNO
     * @throws Exception    Lanza una excepcion si ocurre un error de IO o si la sesion ha sido vencida
     */
    void _resolveUserInfo() throws Exception{
        if(cfgUserInfo ==null){
            this.cfgUserInfo =new URLConfiguration(Constants.V1.URL_API_USER_INFO(userName), "GET", "authorization");
        }
        cfgUserInfo.configureIfNeeded();
        Response resp = Internal.req(new Request.Builder()
                .url(Constants.V1.URL_API_USER_INFO(userName))
                .header("content-type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Authorization", toAuthorization())
                .get()
                .build());
        if(resp.code()==200){userInfo=new UserInfo(this, resp.body().string());return;}

        CommonError.throwEx(resp);
    }

    /**
     * Devualve las aplicaciones del desarrollador actual
     * @return  Las aplicaciones propias. NULLABLE
     */
    public ArrayList<Application> getOwnApps(){
        try {
            return getOwnApps(false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devuelve las aplicaciones del desarrollador
     * @param resolveIfNot  Especifica si deberian pedirse al servidor las aplicaciones si son nulas
     * @return              Devuelve las aplicaciones del desarrollador o las pide al servidor si son nullas
     * @throws Exception    lanza una excepcion si ocurre un error de IO o si las credenciales son invalidas. NULLABLE
     */
    public ArrayList<Application> getOwnApps(boolean resolveIfNot) throws Exception{
        if(apps==null&&resolveIfNot){

            _resolveApps();
        }
        return apps;
    }

    /**
     * INTERNO
     * @throws Exception    Si ocurre un error de IO
     */
    void _resolveApps() throws Exception{
        Response resp = Internal.req(new Request.Builder()
                .url(Constants.V1.URL_API_APPS_BY_OWNER(userInfo.getId()))
                .addHeader("Authorization", toAuthorization())
                .get()
                .build());
        if(resp.code()!=200)CommonError.throwEx(resp);

        String val = resp.body().string();
        ArrayList<Application> list = new ArrayList<>();

        JSONObject obj = new JSONObject(val);

        JSONArray arr = obj.getJSONArray("results");
        for (int i = 0; i < arr.length(); i++) {
            list.add(new Application(this, arr.getJSONObject(i)));
        }

        this.apps=list;
    }

    /**
     * Devuelve las categorias disponibles para las apps
     * @return  Las categorias disponibles. NULLABLE
     */
    public Categories getCategories() {
        if(categories==null){
            categories=Categories.getInstance();
        }
        return categories;
    }

    /**
     * Nombre de usuario
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Contraseña
     */
    public String getPassword() {
        return password;
    }

    /**
     * El token necesario para realizar algunas acciones en el servidor como ublicar apps, editar tu usuario
     * @return  Token de acceso actual
     */
    public String getAccessToken() {
        return accesToken;
    }

    /**
     *
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Tipo de token (Generalmente Bearer)
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     *
     */
    public String getScope() {
        return scope;
    }

    /**
     * Devuelve el tiempo de expiracion
     */
    public int getExpiration() {
        return expiration;
    }

    /**
     *
     */
    @Deprecated
    public int getRequestedTime() {
        return requestedTime;
    }

    /**
     *
     */
    public DashboardStats getDashboardStats() {
        return dashboardStats;
    }

    /**
     * Devuelve si esta sesion ha expirado
     */
    @Deprecated
    public boolean isExpired(){
        return requestedTime>=expiration;
    }

    /**
     * Devuelve el token de autorizacion junto con su tipo de token
     * @return  {@code return getTokenType() + getAccessToken();}
     */
    public String toAuthorization() {
        return tokenType+" "+ getAccessToken();
    }

    /**
     * Actualiza todos los datos actuales
     * @throws Exception    Si ocurre algun error de IO durante las peticiones al servidor
     */
    public void updateCache() throws Exception {
        _resolveUserInfo();
        _resolveApps();
        _resolveCategories();
        _resolveDashboardStats();
    }

    private void _resolveDashboardStats() throws Exception{
        if(cfgDashboardStats==null)this.cfgDashboardStats = new URLConfiguration(Constants.V2.URL_API_GLOBALS_DASHBOARD_STATS, "GET", "authorization");
        cfgDashboardStats.configureIfNeeded();
        Request req = new Request.Builder()
                .url(Constants.V2.URL_API_GLOBALS_DASHBOARD_STATS)
                .get()
                .build();

        Response r = Internal.req(req);
        if(!r.isSuccessful()){
            CommonError.throwEx(r);
        }
        this.dashboardStats=new DashboardStats(new JSONObject(r.body().string()));
    }

    private void _resolveCategories() {
        categories=Categories.getInstance();
    }

    /**
     * Registra EXPERIMENTAL
     * @param username  Nombre de usuario
     * @param cid       Carnet de Identidad
     * @param email     Correo
     * @param province  Provincia
     * @param reg_num   Numero de registro
     * @param bussiness Nombre del negocio
     * @param desc      Descripcion del negocio
     */
    public static void Register(String username, String cid, String email, Province province, String reg_num, String bussiness, String desc){
        try{
            register.configureIfNeeded();

            Response request = Internal.req(new Request.Builder()
                    .url(Constants.V1.URL_API_REGISTER)
                    .post(RequestBody.create(MediaType.get("application/json"), new JSONObject()
                            .put("user", username)
                            .put("cid", cid)
                            .put("email", email)
                            .put("province", province.toNumber()+"")
                            .put("reg_num", reg_num)
                            .put("business", bussiness)
                            .put("description", desc)
                            .toString().getBytes()
                    ))
                    .build());

            if(request.code()==201) {
                return;
            }
            try{
                String msg = request.body().string();
                JSONObject obj = new JSONObject(msg);
                if(!obj.has("error")){
                    throw new Error(request.message());
                }
                String err = obj.getString("error");
                throw new Error(request.message()+" ( "+err+" )");
            }catch(Exception e){
                e.printStackTrace();
            }
            throw new Error(""+request.message());
        }catch(Exception e){
            throw new Error(e);
        }
    }

    private static URLConfiguration register  =new URLConfiguration(Constants.V1.URL_API_REGISTER, "POST", "content-type");

    public static final class CommonError{
        public static final void throwEx(Response r) throws Exception {
            switch(r.code()){
                case 503:
                    throw new ServiceUnavailableException(/*r.body().string()*/);
                case 500:
                    throw new InternalServerException(/*r.body().string()*/);
                case 400:
                case 403:
                case 404:
                    throw new UserNotFoundException(/*r.body().string()*/);
            }
        }
    }
}
