package TurnosEnfermeria.modelo;

/**
 * Clase base abstracta que representa a una persona en el sistema.
 * Todos los atributos son privados con sus correspondientes getters y setters (SIA-3).
 * Implementa la validacion de RUT chileno mediante el algoritmo Modulo 11.
 */
public abstract class Persona {

    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private String rut;
    private int edad;

    /**
     * Constructor principal. Lanza RutInvalidoException si el RUT no es valido.
     */
    public Persona(String nombre, String apellidoP, String apellidoM,
                   String rut, int edad) throws RutInvalidoException {
        this.nombre    = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        setRut(rut); // Valida y asigna el RUT
        this.edad = edad;
    }

    // ========== METODOS DE NEGOCIO ==========
    /**
    * Normaliza el RUT quitando puntos y espacios exteriores,
    * y convirtiendo el digito verificador a mayuscula.
    */
    public static String normalizarRut(String rut) {
        if (rut == null) {
            return "";
        }
        return rut.trim().toUpperCase(java.util.Locale.ROOT).replace(".", "");
    }

    /**
     * Valida un RUT chileno mediante el algoritmo Modulo 11.
     * Acepta formato: 12345678-5 o 12345678-K (sin puntos).
     * @param rut RUT a validar
     * @return true si el RUT es valido segun Modulo 11
     */
    public static boolean validarRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) return false;
        String r = normalizarRut(rut);
        // Validar formato: digitos seguidos de guion y digito verificador
        if (!r.matches("\\d{7,8}-[\\dK]")) return false;
        String[] partes = r.split("-");
        String cuerpo       = partes[0];
        char   dvIngresado  = partes[1].charAt(0);

        // Calcular digito verificador con Modulo 11
        int suma   = 0;
        int factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma  += Character.getNumericValue(cuerpo.charAt(i)) * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }
        int  resto       = suma % 11;
        char dvCalculado = (resto == 0) ? '0'
                         : (resto == 1) ? 'K'
                         : (char)('0' + (11 - resto));

        return dvIngresado == dvCalculado;
    }

    /**
     * Retorna el nombre completo: Nombre ApellidoPaterno ApellidoMaterno.
     */
    public String getNombreCompleto() {
        return nombre + " " + apellidoP + " " + apellidoM;
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    public String getNombre()              { return nombre; }
    public void   setNombre(String n)      { this.nombre = n; }

    public String getApellidoP()           { return apellidoP; }
    public void   setApellidoP(String ap)  { this.apellidoP = ap; }

    public String getApellidoM()           { return apellidoM; }
    public void   setApellidoM(String am)  { this.apellidoM = am; }

    public String getRut()                 { return rut; }

    /**
     * Asigna el RUT validando Modulo 11. Lanza RutInvalidoException si invalido.
     */
    public void setRut(String rut) throws RutInvalidoException {
        if (!validarRut(rut)) {
            throw new RutInvalidoException(rut);
        }
        this.rut = normalizarRut(rut);
    }

    public int  getEdad()        { return edad; }
    public void setEdad(int e)   { this.edad = e; }

    @Override
    public String toString() {
        return "[" + rut + "] " + getNombreCompleto() + " (Edad: " + edad + ")";
    }
}
