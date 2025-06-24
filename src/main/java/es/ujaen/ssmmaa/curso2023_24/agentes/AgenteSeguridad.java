package es.ujaen.ssmmaa.curso2023_24.agentes;

import es.ujaen.ssmmaa.OntoJuegoEspias;
import es.ujaen.ssmmaa.Vocabulario;
import es.ujaen.ssmmaa.Vocabulario.*;
import es.ujaen.ssmmaa.elementos.*;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.proto.ProposeResponder;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.util.Vector;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author pedroj
 * Esqueleto de agente para la estructura general que deben tener todos los
 * agentes
 */
public class AgenteSeguridad extends Agent {
    //Variables del agente
    private Ontology ontologia;
    private AgenteJugador agenteEnJuego; //Representacion del agente en la partida.
    private Codec codec = new SLCodec();
    private HashMap<String, Juego> juegosApuntados;
    private HashMap<String, Partida> partidasIniciadas;
    private HashMap<String, Integer> puntosAccionActuales;
    private HashMap<String, boolean[]>  zonasExploradas;
    private HashMap<String, boolean[]>  zonasExploradasEnemigo;
    private HashMap<String, Vocabulario.Objetivo[]>  tipoZonas;
    private final double porcentajeExploracion = 0.3;

    private ContentManager manager;
    
    @Override
    protected void setup() {
        
        //Inicialización de las variables del agente
        this.agenteEnJuego = new AgenteJugador(getLocalName(), this.getAID(), TipoAgenteJugador.SEGURIDAD);
        this.juegosApuntados = new HashMap<>();
        this.partidasIniciadas = new HashMap<>();
        this.puntosAccionActuales = new HashMap<>();
        this.zonasExploradas = new HashMap<>();
        this.zonasExploradasEnemigo = new HashMap<>();
        this.tipoZonas = new HashMap<>();
        //Registro de la Ontología y del lenguaje
        try {
            ontologia = OntoJuegoEspias.getInstance();
            manager = getContentManager();
            manager.registerLanguage(codec);
            manager.registerOntology(ontologia);
        } catch (BeanOntologyException ex) {
            //Logger.getLogger(AgenteDemo.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(getLocalName() + " no pudo recuperar la ontologia");
            takeDown();
        }

        
        //Registro del agente en las Páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(TipoAgenteJugador.SEGURIDAD.toString());
        sd.setName(Vocabulario.NOMBRE_SERVICIO);
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            System.out.println(getLocalName() + " no pudo registrarse");
            takeDown();
        }
        
        System.out.println("Se inicia la ejecución del agente: " + this.getName());
        //Añadir las tareas principales
        MessageTemplate mtProponer = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE),
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        addBehaviour(new TareaResponderPropose(this, mtProponer));
        
        MessageTemplate mtContract = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        addBehaviour(new TareaResponderContractNet(this, mtContract));

        addBehaviour(new TareaSolicitudFinalizacion());
    }

    @Override
    protected void takeDown() {
        //Eliminar registro del agente en las Páginas Amarillas
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            System.out.println(this.getName()+" no pudo eliminarse de las páginas amarillas.");
        }

        //Despedida
        System.out.println("Finaliza la ejecución del agente: " + this.getLocalName());
    }

    //Métodos de trabajo del agente
    private ArrayList clasica_y_aburrida(String idPartida){
        ArrayList estrategias = new ArrayList();
        Vector<Integer> resultado = cuantasZonas(idPartida, Objetivo.MAXIMA_PRIORIDAD);
        boolean [] zonasAsignadas = new boolean [Vocabulario.NUM_ZONAS];
        boolean centinela = true;
        for (int i = 0; i < resultado.size() && centinela; i++) {
            double prob = Vocabulario.aleatorio.nextDouble();
            EstrategiaSeguridad cual = null;
            if(prob < 0.7 && this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.NE.getPuntosAccion()){
                cual = EstrategiaSeguridad.NE;
            } else if(this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.DI.getPuntosAccion()) {
                cual = EstrategiaSeguridad.DI;
            } else {
                centinela = false;
            }
            if(centinela){
                Estrategia estrategia = new Estrategia(cual.toString(),this.agenteEnJuego.getTipoJugador());
                Asignacion nueva = new Asignacion(resultado.get(i),estrategia);
                estrategias.add(nueva);
                zonasAsignadas[resultado.get(i)] = true;
                int actualizaPuntos = puntosAccionActuales.get(idPartida);
                actualizaPuntos -= cual.getPuntosAccion();
                puntosAccionActuales.put(idPartida, actualizaPuntos);
            }
        }
        resultado = cuantasZonas(idPartida, Objetivo.ALTA_PRIORIDAD);
        for (int i = 0; i < resultado.size()&&centinela; i++) {
            double prob = Vocabulario.aleatorio.nextDouble();
            EstrategiaSeguridad cual = null;
            if(prob < 0.6 && this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.NE.getPuntosAccion()){
                cual = EstrategiaSeguridad.NE;
            } else if(this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.DI.getPuntosAccion()) {
                cual = EstrategiaSeguridad.DI;
            } else {
                centinela = false;
            }
            if(centinela){
                Estrategia estrategia = new Estrategia(cual.toString(),this.agenteEnJuego.getTipoJugador());
                Asignacion nueva = new Asignacion(resultado.get(i),estrategia);
                estrategias.add(nueva);
                zonasAsignadas[resultado.get(i)] = true;
                int actualizaPuntos = puntosAccionActuales.get(idPartida);
                actualizaPuntos -= cual.getPuntosAccion();
                puntosAccionActuales.put(idPartida, actualizaPuntos);
            }
        }
        resultado = cuantasZonas(idPartida, Objetivo.PRIORIDAD_NORMAL);
        for (int i = 0; i < resultado.size() && centinela; i++) {
            double prob = Vocabulario.aleatorio.nextDouble();
            EstrategiaSeguridad cual = null;
            if(prob > 0.7 && this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.NE.getPuntosAccion()){
                cual = EstrategiaSeguridad.NE;
            } else if(this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.DI.getPuntosAccion()) {
                cual = EstrategiaSeguridad.DI;
            } else {
                centinela = false;
            }
            if(centinela){
                Estrategia estrategia = new Estrategia(cual.toString(),this.agenteEnJuego.getTipoJugador());
                Asignacion nueva = new Asignacion(resultado.get(i),estrategia);
                estrategias.add(nueva);
                zonasAsignadas[resultado.get(i)] = true;
                int actualizaPuntos = puntosAccionActuales.get(idPartida);
                actualizaPuntos -= cual.getPuntosAccion();
                puntosAccionActuales.put(idPartida, actualizaPuntos);
            }
        }

        for (int i = 0; i < zonasAsignadas.length && centinela; i++) {
            double prob = Vocabulario.aleatorio.nextDouble();
            if(!zonasAsignadas[i]){
                EstrategiaSeguridad cual = null;
                if(prob > 0.7 && this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.NE.getPuntosAccion()){
                    cual = EstrategiaSeguridad.NE;
                } else if(this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.DI.getPuntosAccion()) {
                    cual = EstrategiaSeguridad.DI;
                } else {
                    centinela = false;
                }
                if(centinela){
                    Estrategia estrategia = new Estrategia(cual.toString(),this.agenteEnJuego.getTipoJugador());
                    Asignacion nueva = new Asignacion(i,estrategia);
                    estrategias.add(nueva);
                    zonasAsignadas[i] = true;
                    int actualizaPuntos = puntosAccionActuales.get(idPartida);
                    actualizaPuntos -= cual.getPuntosAccion();
                    puntosAccionActuales.put(idPartida, actualizaPuntos);
                }
            }
        }
        return estrategias;
    }
    
    private ArrayList abarcarMucho_apretarPoco(String idPartida){
        ArrayList estrategias = new ArrayList();
        boolean [] zonasAsignadas = new boolean [Vocabulario.NUM_ZONAS];
        for (int i = 0; i < Vocabulario.NUM_ZONAS; i++) {
            zonasAsignadas[i] = false;
        }
        Vector<Integer> resultado = cuantasZonas(idPartida, Objetivo.MAXIMA_PRIORIDAD);
        if(!resultado.isEmpty()){ // Si se conocen zonas de máxima prioridad
            boolean centinela = true;
            //Buscar una zona maxima prioridad que el rival haya explorado.
            for (int i = 0; i < resultado.size() && centinela; i++) {
                if(this.zonasExploradasEnemigo.get(idPartida)[resultado.get(i)]){
                    Estrategia estrategia = new Estrategia(EstrategiaSeguridad.NE.toString(),this.agenteEnJuego.getTipoJugador());
                    Asignacion nueva = new Asignacion(resultado.get(i),estrategia);
                    estrategias.add(nueva);
                    zonasAsignadas[resultado.get(i)] = true;
                    int actualizaPuntos = puntosAccionActuales.get(idPartida);
                    actualizaPuntos -= EstrategiaSeguridad.NE.getPuntosAccion();
                    puntosAccionActuales.put(idPartida, actualizaPuntos);
                    centinela = false;
                }
            }
            if(centinela){ // si no aplicar la mejor estrategia a una de las zona con maxima prioridad
                Estrategia estrategia = new Estrategia(EstrategiaSeguridad.NE.toString(),this.agenteEnJuego.getTipoJugador());
                Asignacion nueva = new Asignacion(resultado.get(0),estrategia);
                estrategias.add(nueva);
                zonasAsignadas[resultado.get(0)] = true;
                int actualizaPuntos = puntosAccionActuales.get(idPartida);
                actualizaPuntos -= EstrategiaSeguridad.NE.getPuntosAccion();
                puntosAccionActuales.put(idPartida, actualizaPuntos);
                centinela = false;
            }
        } else { //Si no, buscar zonas de alta prioridad
            resultado = cuantasZonas(idPartida, Objetivo.ALTA_PRIORIDAD);
            if(!resultado.isEmpty()){ //si hay zonas de alta prioridad
                boolean centinela = true;
                //Buscar una zona alta prioridad que el rival haya explorado.
                for (int i = 0; i < resultado.size() && centinela; i++) {
                    if(this.zonasExploradasEnemigo.get(idPartida)[resultado.get(i)]){
                        Estrategia estrategia = new Estrategia(Vocabulario.EstrategiaSeguridad.NE.toString(),this.agenteEnJuego.getTipoJugador());
                        Asignacion nueva = new Asignacion(resultado.get(i),estrategia);
                        estrategias.add(nueva);
                        zonasAsignadas[resultado.get(i)] = true;
                        int actualizaPuntos = puntosAccionActuales.get(idPartida);
                        actualizaPuntos -= EstrategiaSeguridad.NE.getPuntosAccion();
                        puntosAccionActuales.put(idPartida, actualizaPuntos);
                        centinela = false;
                    }
                }
                if(centinela){ // si no aplicar la mejor estrategia a una de las zona con alta prioridad
                    Estrategia estrategia = new Estrategia(Vocabulario.EstrategiaSeguridad.NE.toString(),this.agenteEnJuego.getTipoJugador());
                    Asignacion nueva = new Asignacion(resultado.get(0),estrategia);
                    estrategias.add(nueva);
                    zonasAsignadas[resultado.get(0)] = true;
                    int actualizaPuntos = puntosAccionActuales.get(idPartida);
                    actualizaPuntos -= EstrategiaSeguridad.NE.getPuntosAccion();
                    puntosAccionActuales.put(idPartida, actualizaPuntos);
                    centinela = false;
                }
            } else { //Si no, buscar zonas de prioridad normal
                resultado = cuantasZonas(idPartida, Objetivo.PRIORIDAD_NORMAL);
                if(!resultado.isEmpty()){
                    boolean centinela = true;
                    //Buscar una zona normal prioridad que el rival haya explorado.
                    for (int i = 0; i < resultado.size() && centinela; i++) {
                        if(this.zonasExploradasEnemigo.get(idPartida)[resultado.get(i)]){
                            Estrategia estrategia = new Estrategia(Vocabulario.EstrategiaSeguridad.NE.toString(),this.agenteEnJuego.getTipoJugador());
                            Asignacion nueva = new Asignacion(resultado.get(i),estrategia);
                            estrategias.add(nueva);
                            zonasAsignadas[resultado.get(i)] = true;
                            int actualizaPuntos = puntosAccionActuales.get(idPartida);
                            actualizaPuntos -= EstrategiaSeguridad.NE.getPuntosAccion();
                            puntosAccionActuales.put(idPartida, actualizaPuntos);
                            centinela = false;
                        }
                    }
                    if(centinela){ // si no aplicar la mejor estrategia a una de las zona con normal prioridad
                        Estrategia estrategia = new Estrategia(Vocabulario.EstrategiaSeguridad.NE.toString(),this.agenteEnJuego.getTipoJugador());
                        Asignacion nueva = new Asignacion(resultado.get(0),estrategia);
                        estrategias.add(nueva);
                        zonasAsignadas[resultado.get(0)] = true;
                        int actualizaPuntos = puntosAccionActuales.get(idPartida);
                        actualizaPuntos -= EstrategiaSeguridad.NE.getPuntosAccion();
                        puntosAccionActuales.put(idPartida, actualizaPuntos);
                        centinela = false;
                    }
                }
            }
        }
        //Rellenar las zonas restantes
        boolean centinela = true;
        for (int i = 0; i < Vocabulario.NUM_ZONAS && centinela; i++) {
            double prob = Vocabulario.aleatorio.nextDouble();
            if(!this.zonasExploradasEnemigo.get(idPartida)[i] && !zonasAsignadas[i]){
                EstrategiaSeguridad cual = null;
                if(prob > 0.7 && this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.NE.getPuntosAccion()){
                    cual = EstrategiaSeguridad.NE;
                } else {
                    cual = EstrategiaSeguridad.DI;
                }
                Estrategia estrategia = new Estrategia(cual.toString(),this.agenteEnJuego.getTipoJugador());
                Asignacion nueva = new Asignacion(i,estrategia);
                estrategias.add(nueva);
                zonasAsignadas[i] = true;
                int actualizaPuntos = puntosAccionActuales.get(idPartida);
                actualizaPuntos -= cual.getPuntosAccion();
                puntosAccionActuales.put(idPartida, actualizaPuntos);
                if(this.puntosAccionActuales.get(idPartida)<EstrategiaSeguridad.DI.getPuntosAccion()){
                     centinela = false;
                }
            }
        }
        for (int i = 0; i < Vocabulario.NUM_ZONAS && centinela; i++) {
            double prob = Vocabulario.aleatorio.nextDouble();
            if(this.zonasExploradasEnemigo.get(idPartida)[i] && !zonasAsignadas[i]){
                EstrategiaSeguridad cual = null;
                if(prob > 0.7 && this.puntosAccionActuales.get(idPartida)>=EstrategiaSeguridad.NE.getPuntosAccion()){
                    cual = EstrategiaSeguridad.NE;
                } else {
                    cual = EstrategiaSeguridad.DI;
                }
                Estrategia estrategia = new Estrategia(cual.toString(),this.agenteEnJuego.getTipoJugador());
                Asignacion nueva = new Asignacion(i,estrategia);
                estrategias.add(nueva);
                zonasAsignadas[i] = true;
                int actualizaPuntos = puntosAccionActuales.get(idPartida);
                actualizaPuntos -= cual.getPuntosAccion();
                puntosAccionActuales.put(idPartida, actualizaPuntos);
                if(this.puntosAccionActuales.get(idPartida)<EstrategiaSeguridad.DI.getPuntosAccion()){
                     centinela = false;
                }
            }
        }
        return estrategias;
    }
    
    private Vector<Integer> cuantasZonas(String idPartida, Objetivo tipoZona){
        Vector<Integer> zonas = new Vector();
        for (int i = 0; i < Vocabulario.NUM_ZONAS; i++) {
            if(this.tipoZonas.get(idPartida)[i].equals(tipoZona)){ //TODO: puede que esto no funciones
                zonas.add(i);
            }
        }
        return zonas;
    }
    
    //Clases internas que representan las tareas del agente
    public class TareaResponderPropose extends ProposeResponder{
        
        public TareaResponderPropose(Agent a, MessageTemplate msg){
            super(a, msg);
        }
        
        @Override
        protected ACLMessage prepareResponse (ACLMessage propose) throws NotUnderstoodException{
            ACLMessage respuesta = propose.createReply();
            try {
                Action accion = (Action)getContentManager().extractContent(propose);
                //TODO: una vez funcione elimnicar los logger.
                if (accion.getAction() instanceof ProponerJuego){
                    ProponerJuego propuesta = (ProponerJuego)accion.getAction();
                    if(propuesta.getJuego().getModo() != Vocabulario.ModoJuego.INDIVIDUAL){ // Por ahora solo individual
                        respuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        Justificacion excusa = new Justificacion(propuesta.getJuego().getIdJuego(), Motivacion.TIPO_JUEGO_DESCONOCIDO);
                        getContentManager().fillContent(respuesta, excusa);

                    } else { //Supuestamente se va jugar al juego
                        respuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        Registro datos = new Registro(propuesta.getJuego().getIdJuego(), agenteEnJuego);
                        getContentManager().fillContent(respuesta, datos);
                        juegosApuntados.put(propuesta.getJuego().getIdJuego(), propuesta.getJuego());
                    }
                } else if(accion.getAction() instanceof IniciarPartida){
                    IniciarPartida propuesta = (IniciarPartida)accion.getAction();
                    AccionPartida accionRespuesta = null;
                    if(juegosApuntados.containsKey(propuesta.getPartida().getIdJuego())){
                        if(partidasIniciadas.size() < 5){ // Si hay menos de 5 partidas aceptadas, iniciar partida
                            respuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            accionRespuesta = new AccionPartida(propuesta.getPartida().getIdPartida(), EstadoPartida.INICIO);
                            getContentManager().fillContent(respuesta, accionRespuesta);
                            //Añadir la partida a la lista de partidas iniciadas
                            partidasIniciadas.put(propuesta.getPartida().getIdPartida(), propuesta.getPartida());
                            //Inicializar los puntos de acción
                            puntosAccionActuales.put(propuesta.getPartida().getIdPartida(), Vocabulario.PUNTOS_ACCION);
                            //Inicializar las estructuras de datos necesarias para la partida.
                            zonasExploradas.put(propuesta.getPartida().getIdPartida(), new boolean [Vocabulario.NUM_ZONAS]);
                            zonasExploradasEnemigo.put(propuesta.getPartida().getIdPartida(), new boolean [Vocabulario.NUM_ZONAS]);
                            tipoZonas.put(propuesta.getPartida().getIdPartida(), new Vocabulario.Objetivo [Vocabulario.NUM_ZONAS]);
                            for (int i = 0; i < Vocabulario.NUM_ZONAS; i++) {
                                zonasExploradas.get(propuesta.getPartida().getIdPartida())[i] = false;
                                zonasExploradasEnemigo.get(propuesta.getPartida().getIdPartida())[i] = false;
                                tipoZonas.get(propuesta.getPartida().getIdPartida())[i] = Vocabulario.Objetivo.DESCONOCIDO;
                            }
                        } else { // si hay más partidas, se abandona la partida
                            respuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            accionRespuesta = new AccionPartida(propuesta.getPartida().getIdPartida(), EstadoPartida.ABANDONAR);
                            getContentManager().fillContent(respuesta, accionRespuesta);
                        }
                    }
                }
            } catch (Codec.CodecException | OntologyException ex) {
                System.out.println(this.getAgent().getName()+": Excepcion en TareaResponderPropose-prepareResponse");
            }
            //System.out.println(this.getAgent().getName()+": "+respuesta);
            return respuesta;
        }
    }
    
    public class TareaResponderContractNet extends ContractNetResponder{
        
        public TareaResponderContractNet (Agent a, MessageTemplate msg){
            super(a, msg);
        }
        
        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException{
            ACLMessage respuesta = cfp.createReply();
            
            try {
                Action accion = (Action)getContentManager().extractContent(cfp);
                if(accion.getAction() instanceof SolicitarExploracion){ //TODO: igual hay que comprobar el id de la partida.
                    System.out.println(this.myAgent.getLocalName()+"--> Se recibe CFP solicitud exploracion");
                    SolicitarExploracion solicitud = (SolicitarExploracion) accion.getAction();
                    //Si aun no se ha llegado al limite de puntos de accion gastados
                    int limite = Vocabulario.PUNTOS_ACCION-(int)(Vocabulario.PUNTOS_ACCION*porcentajeExploracion);
                    //System.out.println(this.myAgent.getLocalName()+"-->HOLA 1");
                    if(puntosAccionActuales.get(solicitud.getIdPartida())>limite){
                        respuesta.setPerformative(ACLMessage.PROPOSE);
                        int zonaExplorar = Vocabulario.aleatorio.nextInt(Vocabulario.NUM_ZONAS);
                        //Si la zona elegida ya ha sido explorada con éxito, no repetir.
                        //System.out.println(this.myAgent.getLocalName()+"-->HOLA 2");
                        while(zonasExploradas.get(solicitud.getIdPartida())[zonaExplorar]){
                            //System.out.println(this.myAgent.getLocalName()+"-->HOLA 3");
                            zonaExplorar = Vocabulario.aleatorio.nextInt(Vocabulario.NUM_ZONAS);
                        }
                        //System.out.println(this.myAgent.getLocalName()+"-->HOLA 4");
                        int puntosGastados = Vocabulario.aleatorio.nextInt(3)+1; //TODO: parametrizar
                        //System.out.println(this.myAgent.getLocalName()+"-->puntos gastados: "+puntosGastados);
                        Exploracion movimientoExploracion = new Exploracion(solicitud.getIdPartida(),puntosGastados,zonaExplorar);
                        int actualizaPuntos = puntosAccionActuales.get(solicitud.getIdPartida());
                        actualizaPuntos -=puntosGastados;
                        puntosAccionActuales.put(solicitud.getIdPartida(), actualizaPuntos);
                        getContentManager().fillContent(respuesta, movimientoExploracion);
                        System.out.println(this.myAgent.getLocalName()+"-->Se manda exploracion");
                        //System.out.println(this.myAgent.getLocalName()+respuesta);
                    } else { // Si se ha llegado al limite notificar el fin de la exploracion
                        respuesta.setPerformative(ACLMessage.REFUSE);
                        AccionPartida finExploracion = new AccionPartida(solicitud.getIdPartida(), EstadoPartida.FIN_EXPLORACION);
                        getContentManager().fillContent(respuesta, finExploracion);
                        System.out.println(this.myAgent.getLocalName()+"-->REFUSE: no quiero seguir explorando.");
                    }
                } else if(accion.getAction() instanceof AsignarEstrategias){
                    System.out.println(this.myAgent.getLocalName()+"--> Se recibe CFP solicitud estrategias");
                    // TODO: probar estrategias de una en una
                    AsignarEstrategias solicitud = (AsignarEstrategias) accion.getAction();
                    //Hay suficientes puntos de acción restantes. En teoría no haría falta comprobar esto.
                    if(puntosAccionActuales.get(solicitud.getIdPartida())>=Vocabulario.EstrategiaSeguridad.DI.getPuntosAccion()){
                        respuesta.setPerformative(ACLMessage.PROPOSE);
                        ArrayList asignaciones = new ArrayList();
                        if(Vocabulario.aleatorio.nextBoolean()){
                            asignaciones = abarcarMucho_apretarPoco(solicitud.getIdPartida());
                        } else {
                            asignaciones = clasica_y_aburrida(solicitud.getIdPartida());
                        }
                        AsignacionEstrategias estrategia = new AsignacionEstrategias(solicitud.getIdPartida(), asignaciones);
                        getContentManager().fillContent(respuesta, estrategia);
                        System.out.println(this.myAgent.getLocalName()+"--> Se manda propose con las estrategias:");
                        System.out.println(this.myAgent.getLocalName()+respuesta);
                    } else {
                        respuesta.setPerformative(ACLMessage.REFUSE);
                        AccionPartida abandono = new AccionPartida(solicitud.getIdPartida(), EstadoPartida.ABANDONAR);
                        getContentManager().fillContent(respuesta, abandono);
                    }
                }
            } catch (Codec.CodecException | OntologyException ex) {
                System.out.println(this.getAgent().getName()+": Excepcion en TareaResponderContractNet-handleCfp");
            }
            //System.out.println(this.myAgent.getLocalName()+"-->HOLA 6");
            return respuesta;
        }
        
        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
            ACLMessage respuesta = accept.createReply();
            //System.out.println(this.myAgent.getLocalName()+accept);
            
            try {
                ContentElement accion = getContentManager().extractContent(accept);
                if(accion instanceof ResultadoExploracion){
                    System.out.println(this.myAgent.getLocalName()+"--> Se recibe ACCEPT_PROPOSAL con la exploracion.");
                    //System.out.println(this.myAgent.getLocalName()+"-->ADIOS 1");
                    respuesta.setPerformative(ACLMessage.INFORM);
                    ResultadoExploracion resultado = (ResultadoExploracion) accion;
                    List movimientos = resultado.getOperacionExploracion();
                    OperacionExploracion movimientoJugador = (OperacionExploracion)movimientos.get(0);
                    OperacionExploracion movimientoRival = null;
                    // Si el rival ha explorado almacenar movimiento.
                    if(movimientos.size()>1){
                        movimientoRival = (OperacionExploracion)movimientos.get(1);
                        if(movimientoRival.getNumZona()<Vocabulario.NUM_ZONAS)
                            zonasExploradasEnemigo.get(resultado.getIdPartida())[movimientoRival.getNumZona()] = true;
                    }
                    // Almacenar movimientos propios
                    if(movimientoJugador.getObjetivo() != Objetivo.DESCONOCIDO){
                        tipoZonas.get(resultado.getIdPartida())[movimientoJugador.getNumZona()] = movimientoJugador.getObjetivo();
                        zonasExploradas.get(resultado.getIdPartida())[movimientoJugador.getNumZona()] = true;
                    }
                    AccionPartida continuacion = null;
                    int limite = Vocabulario.PUNTOS_ACCION-(int)(Vocabulario.PUNTOS_ACCION*porcentajeExploracion);
                    // Si no se ha llegado al minimo, seguir explorando
                    if(puntosAccionActuales.get(resultado.getIdPartida())>limite){
                        System.out.println(this.myAgent.getLocalName()+"-->SEGUIR JUGANDO");
                        continuacion = new AccionPartida(resultado.getIdPartida(), EstadoPartida.SEGUIR_JUGANDO);
                    } else { // Si se ha llegado al limite, finalizar exploración
                        System.out.println(this.myAgent.getLocalName() + "-->FIN EXPLORACION");

                        continuacion = new AccionPartida(resultado.getIdPartida(), EstadoPartida.FIN_EXPLORACION);
                    }
                    getContentManager().fillContent(respuesta, continuacion);
                } else if(accion instanceof AsignacionEstrategias){
                    System.out.println(this.myAgent.getLocalName()+"--> Se recibe ACCEPT_PROPOSAL con las estrategias del rival.");
                    //TODO: Eliminar la partida de partidas en juego. Actualizar todos los valores correspondientes.
                    respuesta.setPerformative(ACLMessage.INFORM);
                    AsignacionEstrategias resultado = (AsignacionEstrategias) accion;
                    AccionPartida abandono = new AccionPartida(resultado.getIdPartida(),EstadoPartida.ABANDONAR);
                    //Eliminar la información de la partida.
                    partidasIniciadas.remove(resultado.getIdPartida());
                    puntosAccionActuales.remove(resultado.getIdPartida());
                    zonasExploradas.remove(resultado.getIdPartida());
                    zonasExploradasEnemigo.remove(resultado.getIdPartida());
                    tipoZonas.remove(resultado.getIdPartida());
                    getContentManager().fillContent(respuesta, abandono);
                    System.out.println(this.myAgent.getLocalName()+"--> Estrategias asignadas. Me voy.");
                }
            } catch (Codec.CodecException | OntologyException ex) {
                System.out.println(this.getAgent().getName()+": Excepcion en TareaResponderContractNet-handleAcceptProposal");
            }
            //System.out.println(this.myAgent.getLocalName()+"-->ADIOS 2");
            return respuesta;
        }
    }

    public class TareaSolicitudFinalizacion extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate template = MessageTemplate.MatchConversationId("TERMINAR EJECUCION");
            ACLMessage respuesta = receive(template);

            if (respuesta != null) {
                myAgent.doDelete();
            } else {
                block();
            }
        }
    }
}
