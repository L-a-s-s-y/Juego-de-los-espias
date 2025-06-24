package es.ujaen.ssmmaa.curso2023_24.agentes;
//Jade
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
//Java
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//Ontologia
import es.ujaen.ssmmaa.OntoJuegoEspias;
import es.ujaen.ssmmaa.Vocabulario;
import es.ujaen.ssmmaa.Vocabulario.*;
import es.ujaen.ssmmaa.elementos.*;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgenteEspia extends Agent {

    final int COOLDOWN_RECEPCION = 200; //Cooldown del servicio de recepcion de mensajes (ms).
    int MAX_PARTIDAS = 5; //Numero maximo de partidas en las que se participa
    private ContentManager MANAGER; //Encargado de extraer y codificar contenido para mensajes
    private final Codec codec = new SLCodec();
    Ontology ONTOLOGIA; //Instancia de la ontologia
    int contadorPartidas = 0; //Numero de partidas en las que se participa
    Map<String, List<Objetivo>> mapaZonas = new HashMap<>(); //Almacena los puntos disponibles por partida

    @Override
    protected void setup() {
        if (MAX_PARTIDAS < Vocabulario.MIN_PARTIDAS) MAX_PARTIDAS = Vocabulario.MIN_PARTIDAS;
        // Instancia de la ontologia

        try {
            ONTOLOGIA = OntoJuegoEspias.getInstance();
            MANAGER = getContentManager();
            MANAGER.registerOntology(ONTOLOGIA);
            MANAGER.registerLanguage(codec);
        } catch (BeanOntologyException e) {
            System.out.println(getLocalName() + " no pudo recuperar la ontologia");
            takeDown();
        }
        // Paginas amarillas
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType(Vocabulario.TipoAgenteJugador.ESPIA.name());
            sd.setName(Vocabulario.NOMBRE_SERVICIO);
            dfd.addServices(sd);
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.out.println(getLocalName() + " no pudo registrarse");
            takeDown();
        }
        System.out.println("Se inicia la ejecución del agente: " + this.getLocalName());
        // Servicio de recepcion
        addBehaviour(new ServicioRecepcion(this));
        addBehaviour(new TareaSolicitudFinalizacion());
    }

    @Override
    protected void takeDown() {
        System.out.println("Finaliza la ejecución del agente: " + this.getLocalName());
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            System.out.println(getLocalName() + " No pudo desregistrarse");
        }
    }

    class ServicioRecepcion extends TickerBehaviour{

        public ServicioRecepcion(Agent a) { super(a, COOLDOWN_RECEPCION);}

        @Override
        protected void onTick() {
            boolean mensajeRecibido;
            do{
                mensajeRecibido = false;
                ACLMessage msg = receive();
                if (msg != null){
                    mensajeRecibido = true;

                    //Lobby
                    if (msg.getPerformative() == ACLMessage.PROPOSE){
                        try {
                            Action content = (Action) MANAGER.extractContent(msg);
                            if (content == null) notUnderstood(msg);
                            else if (content.getAction() instanceof ProponerJuego){
                                if (contadorPartidas < 5) acceptProposal(msg);
                                else rejectProposal(msg);
                            }
                            else if (content.getAction() instanceof IniciarPartida){
                                if (contadorPartidas < 5){
                                    acceptProposal(msg);
                                    contadorPartidas++;
                                }
                                else rejectProposal(msg);
                            }
                            else notUnderstood(msg);
                        } catch (CodecException | OntologyException | ClassCastException ignored) {}
                    }

                    //Juego
                    else if (msg.getPerformative() == ACLMessage.CFP || msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                        try {
                            Action action;
                            action = (Action)MANAGER.extractContent(msg);
                            if (action.getAction() instanceof SolicitarExploracion){
                                explorar(msg);
                            }
                            else if (action.getAction() instanceof AsignarEstrategias) {
                                AsignarEstrategias(msg);
                            }
                        } catch (CodecException | OntologyException | ClassCastException ignored) {}
                        try{
                            ContentElement content;
                            content = MANAGER.extractContent(msg);
                            if (content instanceof ResultadoExploracion) {
                                explorar(msg);
                            }
                        } catch (CodecException | OntologyException | ClassCastException ignored) {}
                    }
                }
            } while (mensajeRecibido);
        }
        
    }

    /* En el caso de la exploracion, ya que se permite el uso de estrategias en zonas no exploradas, empleo el minimo de puntos posibles,
     * asignando un 20% de posibilidad a cada zona, de esta forma, hago que sea un comportamiento mas impredecible que por ejemplo gastar puntos
     * en una zona hasta lograr explorarla, y haciendo imposible que un rival opere en base a lo que yo haga aqui.*/
    void explorar(ACLMessage msg){

        try {
            //System.out.println(this.getLocalName()+"-->HOLA 1");
            Action action;
            action = (Action)MANAGER.extractContent(msg);
            //Explorar (Gastar un punto por zona para tratar de obtener informacion de todas ellas)
            if (action.getAction() instanceof SolicitarExploracion solicitud) {
                //System.out.println(this.getLocalName()+"-->HOLA 2");
                if (!mapaZonas.containsKey(solicitud.getIdPartida())){
                    //System.out.println(this.getLocalName()+"-->HOLA 3");
                    mapaZonas.put(solicitud.getIdPartida(), new ArrayList<>());
                }
                int zonaActual = mapaZonas.get(solicitud.getIdPartida()).size();
                Exploracion exploracion = new Exploracion(solicitud.getIdPartida(), 1, zonaActual%9);
                ACLMessage reply = msg.createReply(ACLMessage.PROPOSE);
                try {
                    //System.out.println(this.getLocalName()+"-->HOLA 4");
                    MANAGER.fillContent(reply, exploracion);
                    send(reply);
                } catch (CodecException | OntologyException e) {notUnderstood(msg);}
            }
        } catch (CodecException | OntologyException | ClassCastException ignored) {}

        try {
            ContentElement content;
            content = MANAGER.extractContent(msg);
            //System.out.println(this.getLocalName()+"-->ADIOS 1");
            //Continuar explorando hasta llegar a 21 puntos restantes
            if (content instanceof ResultadoExploracion resultado) {
                ACLMessage reply = msg.createReply(ACLMessage.INFORM);
                AccionPartida accion;
                //System.out.println(this.getLocalName()+"-->ADIOS 1");

                //Tengo aun zonas que no he probado a explorar, asi que sigo explorando
                if (mapaZonas.get(resultado.getIdPartida()).size() < 9) {
                    OperacionExploracion operacion;
                    operacion = (OperacionExploracion) resultado.getOperacionExploracion().get(0);
                    mapaZonas.get(resultado.getIdPartida()).add(operacion.getNumZona(), operacion.getObjetivo());
                    accion = new AccionPartida(resultado.getIdPartida(), EstadoPartida.SEGUIR_JUGANDO);
                    //System.out.println(this.getLocalName()+"-->SEGUIR JUGANDO");

                }
                //Quedan 21 puntos, asi que los reservo para la asignacion de estrategias
                else {
                    accion = new AccionPartida(resultado.getIdPartida(), EstadoPartida.FIN_EXPLORACION);
                    //System.out.println(this.getLocalName()+"-->FIN EXPLORACION");
                }

                try {
                    MANAGER.fillContent(reply, accion);
                    send(reply);
                    //System.out.println(this.getLocalName()+"-->ADIOS 2");
                } catch (CodecException | OntologyException e) {
                    notUnderstood(msg);
                }
            }
        }catch (CodecException | OntologyException | ClassCastException ignored) {}
    }

    /* Con diferencia, la mejor estrategia del espia es Evaluar Objetivos, que, aun siendo la mas barata, es la que aporta mas beneficio
     * ya que a menos que se neutralize con NE, (que cuesta el doble que EO), siempre genera beneficios para el espia, independientemente de la zona
     * Sin embargo, la mejor estrategia para un contraespia es NE, que precisamente es la que neutraliza a EO. Por ello, se puede suponer
     * que un contraespia tratara de gastar sus puntos en esa estrategia en zonas mas valiosas. En caso de que se encuentre una zona de alto valor,
     * supondre que cualquiera de prioridad superior a PrioridadNormal, tratara de contrarrestar la estrategia NE mediante la estrategia DE.*/
    void AsignarEstrategias(ACLMessage msg){
        //ALERTA: es asignar estrategias, no asignacion estrategias
        //ALERTA: no vemos donde mandas el mensaje y el tablero no lo recibe.
        AsignarEstrategias asignacion = null;
        if (msg.getPerformative() == ACLMessage.CFP){
            try {
                Action accion = (Action)MANAGER.extractContent(msg);
                //System.out.println(this.getLocalName() + msg);
                asignacion = (AsignarEstrategias) accion.getAction();
                //System.out.println(this.getLocalName() + "--> Se recibe el cfp con la solicitud de estrategias");

            } catch (CodecException | OntologyException ignored) {notUnderstood(msg);}
            if (asignacion == null) notUnderstood(msg);
            else{
                jade.util.leap.ArrayList asignaciones = new jade.util.leap.ArrayList();
                int puntosRestantes = 21;
                for (int i = 0; i < 9; i++){
                    Estrategia estrategia = new Estrategia();
                    estrategia.setJugador(TipoAgenteJugador.ESPIA);
                    if (mapaZonas.get(asignacion.getIdPartida()).get(i).equals(Objetivo.DESCONOCIDO)){
                        estrategia.setEstrategia(EstrategiaEspia.EO.toString());
                        puntosRestantes -= 2;
                    }
                    else if (mapaZonas.get(asignacion.getIdPartida()).get(i).equals(Objetivo.BAJA_PRIORIDAD)){
                        estrategia.setEstrategia(EstrategiaEspia.EO.toString());
                        puntosRestantes -= 2;
                    }
                    else if (mapaZonas.get(asignacion.getIdPartida()).get(i).equals(Objetivo.PRIORIDAD_NORMAL)){
                        estrategia.setEstrategia(EstrategiaEspia.EO.toString());
                        puntosRestantes -= 2;
                    }
                    else if (mapaZonas.get(asignacion.getIdPartida()).get(i).equals(Objetivo.ALTA_PRIORIDAD)){
                        if (puntosRestantes > (9-i)*2){
                            estrategia.setEstrategia(EstrategiaEspia.DE.toString());
                            puntosRestantes -= 3;
                        }
                        else{
                            estrategia.setEstrategia(EstrategiaEspia.EO.toString());
                            puntosRestantes -= 2;
                        }
                    }
                    else if (mapaZonas.get(asignacion.getIdPartida()).get(i).equals(Objetivo.MAXIMA_PRIORIDAD)){
                        if (puntosRestantes > (9-i)*2){
                            estrategia.setEstrategia(EstrategiaEspia.DE.toString());
                            puntosRestantes -= 3;
                        }
                        else{
                            estrategia.setEstrategia(EstrategiaEspia.EO.toString());
                            puntosRestantes -= 2;
                        }
                    }
                    else {
                        puntosRestantes -= 2;
                        estrategia.setEstrategia(EstrategiaEspia.EO.toString());
                    }
                    asignaciones.add(i, new Asignacion(i, estrategia));
                }
                AsignacionEstrategias misAsignaciones = new AsignacionEstrategias();
                misAsignaciones.setIdPartida(asignacion.getIdPartida());
                misAsignaciones.setListaAsignacion(asignaciones);
                ACLMessage respuesta = msg.createReply();
                respuesta.setPerformative(ACLMessage.PROPOSE);
                try {
                    MANAGER.fillContent(respuesta, misAsignaciones);
                    System.out.println(this.getLocalName()+respuesta);
                    send(respuesta);
                } catch (CodecException | OntologyException ex) {
                    Logger.getLogger(AgenteEspia.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        //Finalizacion de la partida
        else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
            ACLMessage reply = msg.createReply(ACLMessage.INFORM);
            ContentElement content = null;
            try {
                content = MANAGER.extractContent(msg);
            } catch (CodecException | OntologyException ignored) {notUnderstood(msg);}
            assert content != null;
            AccionPartida accionPartida = new AccionPartida(((AsignacionEstrategias) content).getIdPartida(), EstadoPartida.SEGUIR_JUGANDO);
            try {
                MANAGER.fillContent(reply, accionPartida);
            } catch (CodecException | OntologyException e) {notUnderstood(msg);}
            send(reply);
            contadorPartidas--;
            mapaZonas.remove(accionPartida.getIdPartida());
        }

        else notUnderstood(msg);
    }

    //Comportamiento predeterminado al recibir mensajes inesperados
    void notUnderstood(ACLMessage msg){
        ACLMessage reply = msg.createReply(ACLMessage.NOT_UNDERSTOOD);
        send(reply);
    }

    void rejectProposal(ACLMessage msg){
        ACLMessage reply = msg.createReply(ACLMessage.REJECT_PROPOSAL);
        Action action = null;

        try {
            action = (Action) MANAGER.extractContent(msg);
        } catch (CodecException | OntologyException ignored) {}
        if (action == null) {msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);}
        else if (action.getAction() instanceof ProponerJuego){
            var idJuego = ((ProponerJuego) action.getAction()).getJuego().getIdJuego();
            Motivacion motivacion = Motivacion.JUEGOS_ACTIVOS_SUPERADOS;
            Justificacion justificacion = new Justificacion(idJuego, motivacion);
            try {
                MANAGER.fillContent(reply, justificacion);
            } catch (CodecException | OntologyException ignored) {msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);}
        }
        else if (action.getAction() instanceof IniciarPartida){
            var idJuego = ((IniciarPartida) action.getAction()).getPartida().getIdJuego();
            AccionPartida accionPartida = new AccionPartida(idJuego, EstadoPartida.ABANDONAR);
            try {
                MANAGER.fillContent(reply, accionPartida);
            } catch (CodecException | OntologyException ignored) {msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);}
        }
        else msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);

        send(reply);
    }

    void acceptProposal(ACLMessage msg){
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

        Action content = null;
        try {
            content = (Action)MANAGER.extractContent(msg);
        } catch (CodecException | OntologyException e) {msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);}
        if (content == null) {msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);}
        else if (content.getAction() instanceof ProponerJuego propuesta){
            //ProponerJuego idJuego = ((ProponerJuego)content).getJuego().getIdJuego();
            AgenteJugador agente = new AgenteJugador(getLocalName(), getAID(), TipoAgenteJugador.ESPIA);
            Registro registro = new Registro(propuesta.getJuego().getIdJuego(), agente);
            try {
                MANAGER.fillContent(reply, registro);
            } catch (CodecException | OntologyException ignored) {msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);}
        }
        else if (content.getAction() instanceof IniciarPartida propuesta){
            AccionPartida accionPartida = new AccionPartida(propuesta.getPartida().getIdPartida(), EstadoPartida.INICIO);
            try {
                MANAGER.fillContent(reply, accionPartida);
            } catch (CodecException | OntologyException ignored) {msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);}
        }
        else msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);
        //System.out.println(this.getName()+": "+reply);
        send(reply);
    }

    public class TareaSolicitudFinalizacion extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate template = MessageTemplate.MatchConversationId("TERMINAR EJECUCION");
            ACLMessage respuesta = receive(template);

            if (respuesta != null) {
                myAgent.doDelete();
            }
        }
    }

}