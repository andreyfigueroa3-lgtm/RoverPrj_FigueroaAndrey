package MarsRovers;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que modela un Rover de exploración en Marte.
 * Puede moverse, detectar fugas de calor y gestionar su potencia.
 *
 * @author Andrey Figueroa Calderón
 * @version 1.0
 */
public class Rover {

  // atributos de cada Rover (uno por objeto)

  private String nombre;
  private String codigo;
  private double potencia;
  private double potenciaInicial;
  private int cantRecargas;
  private int cantDetecciones;
  private int x;
  private int y;
  private int xInicial;
  private int yInicial;
  private ArrayList<ArrayList<String>> mandatosExitosos;
  private ArrayList<ArrayList<String>> mandatosNoPosibles;

  // estos dos son compartidos por todos los Rovers (estáticos)

  private static int cantRovers = 0;
  private static ArrayList<String> listaRovers = new ArrayList<>();

  // valores fijos del negocio, nunca cambian

  private static final double POTENCIA_OMISION = 100;
  private static final double COSTO_MOVIMIENTO = 0.5;
  private static final double COSTO_DETECCION = 0.25;
  private static final int RECARGAS_MAXIMAS = 5;

  // ── Constructores ─────────────────────────────────────────────────────────

  // crea el Rover con 100 unidades de potencia si no se indica otra cosa
  // nombre: cómo se va a llamar el Rover, ej: "Curiosity"
  public Rover(String nombre) {
    this(nombre, POTENCIA_OMISION);
  }

  // crea el Rover con el nombre y la potencia que se le indique
  // nombre: cómo se va a llamar el Rover
  // potencia: cuántas unidades de la aleación tendrá al inicio
  public Rover(String nombre, double potencia) {
    cantRovers++;
    this.nombre = nombre;
    this.codigo = "rover-" + cantRovers; // se genera automático con el contador
    this.potencia = potencia;
    this.potenciaInicial = potencia; // guardamos el valor original para mostrarlo en el estado
    this.cantRecargas = RECARGAS_MAXIMAS;
    this.cantDetecciones = 0;
    this.x = 0;
    this.y = 0;
    this.xInicial = 0;
    this.yInicial = 0;
    this.mandatosExitosos = new ArrayList<>();
    this.mandatosNoPosibles = new ArrayList<>();
    listaRovers.add(getEstado());
  }

  // ── Métodos privados ──────────────────────────────────────────────────────

  // lanza el dado para ver si hay fuga de calor en la dirección a moverse
  // gasta 0.25 de potencia siempre, detecte o no detecte
  // devuelve true si hay fuga (peligro), false si está libre para moverse
  private boolean detectarFugaCalor() {
    potencia -= COSTO_DETECCION;
    cantDetecciones++;
    return Math.random() >= 0.5; // >= 0.5 significa fuga detectada
  }

  // arma el registro del mandato y lo mete en la lista que corresponde
  // guarda tres cosas: fecha/hora actual, el tipo de mandato y si salió bien o no
  // tipoMandato: nombre del movimiento que se intentó
  // exitoso: true si se pudo hacer, false si no se pudo
  private void registrarMandato(String tipoMandato, boolean exitoso) {
    ArrayList<String> mandato = new ArrayList<>();
    mandato.add(LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    mandato.add(tipoMandato);
    mandato.add(exitoso ? "exitoso" : "no posible");
   mandatosExitosos =  [ ["dd/MM/yyyy HH:mm:ss","exitoso"] ]
    if (exitoso) {
      mandatosExitosos.add(mandato);
    } else {
      mandatosNoPosibles.add(mandato);
    }
  }

  // lógica central que usan los 4 métodos de movimiento
  // así no repetimos el mismo código 4 veces
  // orden: 1) revisa si hay potencia, 2) detecta fuga, 3) mueve si todo está bien
  // tipo: nombre del mandato para registrarlo
  // ejeX: cuánto cambia x (-1, 0 o 1)
  // ejeY: cuánto cambia y (-1, 0 o 1)
  private void ejecutarDesplazamiento(String tipo, int ejeX, int ejeY) {
    // si no alcanza ni para detectar + moverse, ni lo intentamos
    if (potencia < COSTO_DETECCION + COSTO_MOVIMIENTO) {
      registrarMandato(tipo, false);
      return;
    }

    // detectamos fuga (aquí ya se gasta 0.25 de potencia)
    boolean fugaDetectada = detectarFugaCalor();

    if (fugaDetectada) {
      registrarMandato(tipo, false);
      return;
    }

    // todo bien, nos movemos y gastamos los 0.50 del movimiento
    x += ejeX;
    y += ejeY;
    potencia -= COSTO_MOVIMIENTO;
    registrarMandato(tipo, true);
  }

  // ── Movimientos ───────────────────────────────────────────────────────────

  // mueve el Rover hacia adelante, suma 1 a y
  public void desplazarAdelante() {
    ejecutarDesplazamiento("desplazarAdelante", 0, 1);
  }

  // mueve el Rover hacia atrás, resta 1 a y
  public void desplazarAtras() {
    ejecutarDesplazamiento("desplazarAtras", 0, -1);
  }

  // mueve el Rover hacia la derecha, suma 1 a x
  public void desplazarDerecha() {
    ejecutarDesplazamiento("desplazarDerecha", 1, 0);
  }

  // mueve el Rover hacia la izquierda, resta 1 a x
  public void desplazarIzquierda() {
    ejecutarDesplazamiento("desplazarIzquierda", -1, 0);
  }

  // ── Consultas ─────────────────────────────────────────────────────────────

  // devuelve en qué coordenadas está el Rover ahora mismo, formato (x, y)
  public String getPosicion() {
    return "(" + x + ", " + y + ")";
  }

  // devuelve cuántas unidades de potencia le quedan al Rover
  public double getPotencia() {
    return potencia;
  }

  // devuelve toda la información del Rover: datos generales, posición y lista de mandatos
  public String getEstado() {
    StringBuilder sb = new StringBuilder();
    sb.append("═══════════════════════════════════\n");
    sb.append("  ROVER: ").append(nombre).append("\n");
    sb.append("═══════════════════════════════════\n");
    sb.append("  Código           : ").append(codigo).append("\n");
    sb.append("  Potencia inicial : ").append(potenciaInicial).append("\n");
    sb.append("  Potencia actual  : ").append(potencia).append("\n");
    sb.append("  Recargas dispon. : ").append(cantRecargas).append("\n");
    sb.append("  Detecciones      : ").append(cantDetecciones).append("\n");
    sb.append("  Posición inicial : (").append(xInicial).append(", ")
        .append(yInicial).append(")\n");
    sb.append("  Posición actual  : ").append(getPosicion()).append("\n");

    sb.append("\n  ── Mandatos exitosos (").append(mandatosExitosos.size())
        .append(") ──\n");
    for (ArrayList<String> m : mandatosExitosos) {
      sb.append("  [").append(m.get(0)).append("] ")
          .append(m.get(1)).append(" → ").append(m.get(2)).append("\n");
    }

    sb.append("\n  ── Mandatos no posibles (").append(mandatosNoPosibles.size())
        .append(") ──\n");
    for (ArrayList<String> m : mandatosNoPosibles) {
      sb.append("  [").append(m.get(0)).append("] ")
          .append(m.get(1)).append(" → ").append(m.get(2)).append("\n");
    }

    sb.append("═══════════════════════════════════\n");
    return sb.toString();
  }

  // ── Recarga ───────────────────────────────────────────────────────────────

  // agrega potencia al Rover, solo si todavía le quedan recargas disponibles
  // máximo 5 recargas en toda la vida del Rover
  // unidades: cuántas unidades de la aleación se quieren agregar
  public void recargar(double unidades) {
    if (cantRecargas > 0) {
      potencia += unidades;
      cantRecargas--;
    }
  }

  // ── Estáticos: info de todos los Rovers ───────────────────────────────────

  // devuelve cuántos Rovers se han creado en total
  public static int getCantRovers() {
    return cantRovers;
  }

  // devuelve la información de todos los Rovers que se han creado
  public static String getListaRovers() {
    StringBuilder sb = new StringBuilder();
    sb.append("Total de Rovers creados: ").append(cantRovers).append("\n");
    for (String estado : listaRovers) {
      sb.append(estado).append("\n");
    }
    return sb.toString();
  }
}
