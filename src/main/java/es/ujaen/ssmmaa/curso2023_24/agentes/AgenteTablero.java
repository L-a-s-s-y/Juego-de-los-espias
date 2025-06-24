package es.ujaen.ssmmaa.curso2023_24.agentes;


import es.ujaen.ssmmaa.OntoJuegoEspias;
import es.ujaen.ssmmaa.Pago;
import es.ujaen.ssmmaa.Vocabulario;
import es.ujaen.ssmmaa.elementos.*;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.util.leap.LinkedList;
import jade.util.leap.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import static es.ujaen.ssmmaa.Vocabulario.*;
import static es.ujaen.ssmmaa.Vocabulario.TipoAgenteOrganizador.TABLERO;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acl00111
 * Esqueleto de agente para la estructura general que deben tener todos los
 * agentes
 */
public class AgenteTablero extends Agent {
    //Variables del agente
    private Properties prop = new Properties();
    private String nombreArchivoConfiguracion = "";

    private final Codec codec = new SLCodec();
    private ContentManager manager;
    private Ontology ontology;

    private Partida partida;
    private AgenteJugador espia;
    private AgenteJugador seguridad;

    //
    private Exploracion ultimaExploracionEspia = null;
    private Exploracion ultimaExploracionSeguridad = null;

    private boolean espiaFinalizaExploracion = false;
    private boolean seguridadFinalizaExploracion = false;

    private List espiaEstrategias = new jade.util.leap.ArrayList();
    private List seguridadEstrategias = new jade.util.leap.ArrayList();;

    private IncidenciaJuego incidenciaJuego;
    private boolean calculandoPuntuacion = false;

    private int puntuacionEspia = 0;
    private int puntuacionSeguridad = 0;

    //Repeticiones
    private Map<String, ArrayList<ACLMessage>> registroMovimientos = new HashMap<>();
    private ArrayList<String> listaPartidas = new ArrayList<>();
    private int COOLDOWN_REPETICION = 240;

    private ArrayList<Objetivo> distribucionObjetivos;

    private String organizador = "";


    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        Object[] args = getArguments();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    prop = (Properties) args[i];
                }
                if (i == 1) {
                    partida = (Partida) args[i];
                }
                if (i == 2) {
                    espia = (AgenteJugador) args[i];
                }
                if(i == 3){
                    seguridad = (AgenteJugador) args[i];
                }
                if (i == 4){
                    distribucionObjetivos = (ArrayList<Objetivo>) args[i];
                }
                if (i == 5){
                    organizador = (String) args[i];
                }
            }
            System.out.println("Se ha cargado el archivo de configuracion");
        }

        //Configuración del GUI

        //Registro del agente en las Páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(TABLERO.name());
        sd.setName(NOMBRE_SERVICIO);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.getMessage();
        }

        //Registro de la Ontología
        try {
            ontology = OntoJuegoEspias.getInstance();
            ontology = OntoJuegoEspias.getInstance();
            manager = getContentManager();
            manager.registerLanguage(codec);
            manager.registerOntology(ontology);
        } catch (BeanOntologyException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Se inicia la ejecución del agente: " + this.getName());
        //Añadir las tareas principales
        addBehaviour(new TareaIniciarPartida());
        addBehaviour(new TareaIniciarAsignacionEstrategias(this, 2000));
        addBehaviour(new TareaSolicitudFinalizacion(this));
    }

    @Override
    protected void takeDown() {
        //Eliminar registro del agente en las Páginas Amarillas
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.getMessage();
        }

        //Liberación de recursos, incluido el GUI

        //Despedida
        System.out.println("Finaliza la ejecución del agente: " + this.getName());
    }

    //Métodos de trabajo del agente

    private void mandarMensajeOrganizador(String mensaje, String idConversacion){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(organizador, AID.ISLOCALNAME));
        msg.setConversationId(idConversacion);
        msg.setContent(mensaje);
        send(msg);
    }


    public class TareaIniciarPartida extends OneShotBehaviour{

        @Override
        public void action() {
            SolicitarExploracion solicitarExploracion = new SolicitarExploracion(partida.getIdPartida());
            Action action = new Action(myAgent.getAID(), solicitarExploracion);
            ACLMessage inicioPartida = new ACLMessage(ACLMessage.CFP);
            inicioPartida.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
            inicioPartida.setSender(myAgent.getAID());
            inicioPartida.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
            inicioPartida.setOntology(ontology.getName());
            try {
                manager.fillContent(inicioPartida, action);
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
            addBehaviour(new TareaSolicitarExploracionIniciador(myAgent, inicioPartida));
        }
    }

    public class TareaIniciarAsignacionEstrategias extends TickerBehaviour{

        public TareaIniciarAsignacionEstrategias(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (espiaFinalizaExploracion && seguridadFinalizaExploracion) {
                AsignarEstrategias asignarEstrategias = new AsignarEstrategias(partida.getIdPartida());
                Action action = new Action(myAgent.getAID(), asignarEstrategias);
                ACLMessage inicioEstrategias = new ACLMessage(ACLMessage.CFP);
                inicioEstrategias.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                inicioEstrategias.setSender(myAgent.getAID());
                inicioEstrategias.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                inicioEstrategias.setOntology(ontology.getName());
                try {
                    manager.fillContent(inicioEstrategias, action);
                } catch (Codec.CodecException | OntologyException e) {
                    throw new RuntimeException(e);
                }
                addBehaviour(new TareaAsignarEstrategiasIniciador(myAgent, inicioEstrategias));
                stop();
            }
        }
    }

    public class TareaSolicitarExploracionIniciador extends ContractNetInitiator{

        public TareaSolicitarExploracionIniciador(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected Vector<ACLMessage> prepareCfps(ACLMessage cfp) {
            // Preparar el mensaje CFP para enviar a los agentes jugadores
            cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
            cfp.setLanguage(codec.getName());
            cfp.setOntology(ontology.getName());
            cfp.setReplyByDate(new Date(System.currentTimeMillis() + 1000)); // 1 seg timeout

            if(!espiaFinalizaExploracion)
                cfp.addReceiver(new AID(espia.getApodo(), AID.ISLOCALNAME));
            if(!seguridadFinalizaExploracion)
                cfp.addReceiver(new AID(seguridad.getApodo(), AID.ISLOCALNAME));

            Vector<ACLMessage> v = new Vector<>();
            v.add(cfp);
            return v;
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            try {
                throw new NotUnderstoodException("No se ha entendido la Ontología");
            } catch (NotUnderstoodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handlePropose(ACLMessage propose, Vector acceptances) {
            System.out.println(this.myAgent.getLocalName()+"-->LLega propose de "+propose.getSender());
            try {
                Exploracion action = (Exploracion) manager.extractContent(propose);

                // Aceptar la propuesta
                ACLMessage accept = propose.createReply();
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ResultadoExploracion resultado = new ResultadoExploracion();
                // Rellenar el resultado con los datos apropiados
                resultado.setIdPartida(partida.getIdPartida());
                List operacionExploracion = new jade.util.leap.ArrayList();
                OperacionExploracion exploracionJugador = new OperacionExploracion();
                OperacionExploracion exploracionRival = new OperacionExploracion();

                if(propose.getSender().getLocalName().contains("Espia")){
                    ultimaExploracionEspia = action;
                    exploracionJugador.setNumZona(action.getNumZona());
                    Vocabulario.Objetivo objetivo = distribucionObjetivos.get(action.getNumZona());
                    int puntosAccion = action.getPuntosAccion() * 20;
                    if(aleatorio.nextInt(D100) < puntosAccion){
                        exploracionJugador.setObjetivo(objetivo);
                    }else{
                        exploracionJugador.setObjetivo(Vocabulario.Objetivo.DESCONOCIDO);
                    }
                    if(ultimaExploracionSeguridad == null){
                        exploracionRival.setNumZona(10);
                        exploracionRival.setObjetivo(Vocabulario.Objetivo.DESCONOCIDO);
                    }else{
                        exploracionRival.setNumZona(ultimaExploracionSeguridad.getNumZona());
                        exploracionRival.setObjetivo(Vocabulario.Objetivo.DESCONOCIDO);
                    }
                    String mensaje = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "EXP;ESPIA;" + action.getNumZona();
                    mandarMensajeOrganizador(mensaje, "EXPLORACION");


                } else if (propose.getSender().getLocalName().contains("Seguridad")) {
                    //System.out.println(this.myAgent.getLocalName()+" HOLA 1");
                    ultimaExploracionSeguridad = action;
                    exploracionJugador.setNumZona(action.getNumZona());

                    Vocabulario.Objetivo objetivo = distribucionObjetivos.get(action.getNumZona());
                    int puntosAccion = action.getPuntosAccion() * 20;
                    if(aleatorio.nextInt(100) < puntosAccion){
                        exploracionJugador.setObjetivo(objetivo);
                    }else{
                        exploracionJugador.setObjetivo(Vocabulario.Objetivo.DESCONOCIDO);
                    }
                    if(ultimaExploracionEspia == null){
                        exploracionRival.setNumZona(10);
                        exploracionRival.setObjetivo(Vocabulario.Objetivo.DESCONOCIDO);
                    }else{
                        exploracionRival.setNumZona(ultimaExploracionEspia.getNumZona());
                        exploracionRival.setObjetivo(Vocabulario.Objetivo.DESCONOCIDO);
                    }

                    String mensaje = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "EXP;SEGURIDAD;" + action.getNumZona();
                    mandarMensajeOrganizador(mensaje, "EXPLORACION");
                }

                operacionExploracion.add(exploracionJugador);
                operacionExploracion.add(exploracionRival);

                resultado.setOperacionExploracion(operacionExploracion);
                manager.fillContent(accept, resultado);
                System.out.println(this.myAgent.getLocalName()+"--> Envio accept a "+accept.getAllReceiver().toString());
                acceptances.add(accept);

            } catch (Codec.CodecException | OntologyException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            AccionPartida action = null;
            try {
                action = (AccionPartida) manager.extractContent(refuse);
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
            if(refuse.getSender().getLocalName().contains("Espia")){
                if(action.getAccion() == Vocabulario.EstadoPartida.FIN_EXPLORACION){
                    espiaFinalizaExploracion = true;
                    System.out.println("El espia " + refuse.getSender().getLocalName() + " finaliza la exploracion.");
                }
            } else if (refuse.getSender().getLocalName().contains("Seguridad")) {
                if(action.getAccion() == Vocabulario.EstadoPartida.FIN_EXPLORACION){
                    seguridadFinalizaExploracion = true;
                    System.out.println("La seguridad " + refuse.getSender().getLocalName() + " finaliza la exploracion.");
                }
            }
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            ACLMessage respuesta = new ACLMessage(ACLMessage.CFP);
            try {
                AccionPartida action = (AccionPartida) manager.extractContent(inform);
                System.out.println(this.myAgent.getLocalName()+"--> Llega inform de "+inform.getSender());
                if(inform.getSender().getLocalName().contains("Espia")){
                    if(action.getAccion() == Vocabulario.EstadoPartida.FIN_EXPLORACION){
                        espiaFinalizaExploracion = true;
                        System.out.println("El espia " + inform.getSender().getLocalName() + " finaliza la exploracion.");
                    } else if (action.getAccion() == Vocabulario.EstadoPartida.SEGUIR_JUGANDO) {
                        espiaFinalizaExploracion = false;
                        System.out.println("El espia " + inform.getSender().getLocalName() + " quiere seguir jugando en el Juego " + partida.getIdJuego());

                        SolicitarExploracion solicitarExploracion = new SolicitarExploracion(partida.getIdPartida());
                        Action actionSolicitud = new Action(myAgent.getAID(), solicitarExploracion);
                        //ACLMessage respuesta = inform.createReply(ACLMessage.CFP);
                        respuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                        respuesta.setSender(myAgent.getAID());
                        respuesta.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                        respuesta.setOntology(ontology.getName());
                        try {
                            manager.fillContent(respuesta, actionSolicitud);
                        } catch (Codec.CodecException | OntologyException e) {
                            throw new RuntimeException(e);
                        }

                        //addBehaviour(new TareaSolicitarExploracionIniciador(myAgent, respuesta));

                    }
                } else if (inform.getSender().getLocalName().contains("Seguridad")) {
                    if(action.getAccion() == Vocabulario.EstadoPartida.FIN_EXPLORACION){
                        seguridadFinalizaExploracion = true;
                        System.out.println("La seguridad " + inform.getSender().getLocalName() + " finaliza la exploracion.");
                    } else if (action.getAccion() == Vocabulario.EstadoPartida.SEGUIR_JUGANDO) {
                        seguridadFinalizaExploracion = false;
                        System.out.println("La seguridad " + inform.getSender().getLocalName() + " quiere seguir jugando en el Juego " + partida.getIdJuego());

                        SolicitarExploracion solicitarExploracion = new SolicitarExploracion(partida.getIdPartida());
                        Action actionSolicitud = new Action(myAgent.getAID(), solicitarExploracion);
                        //ACLMessage respuesta = inform.createReply(ACLMessage.CFP);
                        respuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                        respuesta.setSender(myAgent.getAID());
                        respuesta.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                        respuesta.setOntology(ontology.getName());
                        try {
                            manager.fillContent(respuesta, actionSolicitud);
                        } catch (Codec.CodecException | OntologyException e) {
                            throw new RuntimeException(e);
                        }

                        //addBehaviour(new TareaSolicitarExploracionIniciador(myAgent, respuesta));
                    }
                }
                addBehaviour(new TareaSolicitarExploracionIniciador(myAgent, respuesta));
            } catch (Codec.CodecException | OntologyException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class TareaAsignarEstrategiasIniciador extends ContractNetInitiator{

        public TareaAsignarEstrategiasIniciador(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected Vector<ACLMessage> prepareCfps(ACLMessage cfp) {
            // Preparar el mensaje CFP para enviar a los agentes jugadores
            cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
            cfp.setLanguage(codec.getName());
            cfp.setOntology(ontology.getName());
            cfp.setReplyByDate(new Date(System.currentTimeMillis() + 2000)); // 2 seg timeout

            cfp.addReceiver(new AID(espia.getApodo(), AID.ISLOCALNAME));
            cfp.addReceiver(new AID(seguridad.getApodo(), AID.ISLOCALNAME));

            Vector<ACLMessage> v = new Vector<>();
            v.add(cfp);
            return v;
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            try {
                throw new NotUnderstoodException("No se ha entendido la Ontología");
            } catch (NotUnderstoodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            //super.handleAllResponses(responses, acceptances); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        
            if(responses.size() == 0){
                System.out.println("Han llegado 0 respuestas, se cancela el juego");
            } else if(responses.size() == 1){
                //if(responses.get(0))
                ACLMessage msg = (ACLMessage) responses.get(0);
                if(msg.getPerformative() == ACLMessage.PROPOSE){
                    System.out.println(msg.getSender()+" Ha ganado");
                } else if(msg.getPerformative() == ACLMessage.REFUSE){
                    System.out.println("Uno REFUSe otro no contesta");
                }
            } else if(responses.size() == 2){
                System.out.println(this.myAgent.getLocalName()+"--> Se han recibido ambas estrategias.");
                // Se suponen dos propose
                for (int j = 0; j < responses.size(); j++) {
                    ACLMessage msg = (ACLMessage) responses.get(j);
                    ContentElement accionEstrategia;
                    try {
                        //ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        accionEstrategia = manager.extractContent(msg);
                        AsignacionEstrategias asignacionEstrategias = (AsignacionEstrategias) accionEstrategia;
                        if (msg.getSender().getLocalName().contains("Espia")) {
                            espiaEstrategias = asignacionEstrategias.getListaAsignacion();
                            String mensaje = "";
                            for (int i = 0; i < espiaEstrategias.size(); i++) {
                                mensaje = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "EST;ESPIA;" + espiaEstrategias.get(i).toString() + "\n";
                                mandarMensajeOrganizador(mensaje, "ESTRATEGIA");

                            }
                            //mandarMensajeOrganizador(mensaje, "ESTRATEGIA");

                        } else if (msg.getSender().getLocalName().contains("Seguridad")) {
                            seguridadEstrategias = asignacionEstrategias.getListaAsignacion();
                            String mensaje = "";
                            for (int i = 0; i < seguridadEstrategias.size(); i++) {
                                mensaje = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "EST;SEGURIDAD;" + seguridadEstrategias.get(i).toString() + "\n";
                                mandarMensajeOrganizador(mensaje, "ESTRATEGIA");

                            }

                            //mandarMensajeOrganizador(mensaje, "ESTRATEGIA");

                        }
                    } catch (Codec.CodecException ex) {
                        Logger.getLogger(AgenteTablero.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (OntologyException ex) {
                        Logger.getLogger(AgenteTablero.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                for (int i = 0; i < responses.size(); i++) {
                    ACLMessage msg = (ACLMessage) responses.get(i);
                    ACLMessage responder_estrategias = msg.createReply();
                    responder_estrategias.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    if(msg.getSender().getLocalName().contains("Espia")){
                        AsignacionEstrategias asignacionRival = new AsignacionEstrategias(partida.getIdPartida(), seguridadEstrategias);
                        try {
                            manager.fillContent(responder_estrategias, asignacionRival);
                            acceptances.add(responder_estrategias);
                        } catch (Codec.CodecException ex) {
                            Logger.getLogger(AgenteTablero.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (OntologyException ex) {
                            Logger.getLogger(AgenteTablero.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if(msg.getSender().getLocalName().contains("Seguridad")){
                        AsignacionEstrategias asignacionRival = new AsignacionEstrategias(partida.getIdPartida(), espiaEstrategias);
                        try {
                            manager.fillContent(responder_estrategias, asignacionRival);
                            acceptances.add(responder_estrategias);

                        } catch (Codec.CodecException ex) {
                            Logger.getLogger(AgenteTablero.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (OntologyException ex) {
                            Logger.getLogger(AgenteTablero.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        
        

//        @Override
//        protected void handlePropose(ACLMessage propose, Vector acceptances) {
//            try {
//                ContentElement accionEstrategia = manager.extractContent(propose);
//                AsignacionEstrategias asignacionEstrategias = (AsignacionEstrategias) accionEstrategia;
//
//                //TODO: mandar las estrategia seleccionada por el jugador
//
//                // Aceptar la propuesta
//                // lo que hay que rellenar de las respuestas son intencion y contenido
//                //el juego se compone de 1 a n partidas, dependiendo si el tipo de juego es individual o por equipos
//                ACLMessage accept = propose.createReply();
//                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
//                if(propose.getSender().getLocalName().contains("Espia")){
//                    espiaEstrategias = asignacionEstrategias.getListaAsignacion();
//
//                    if(seguridadEstrategias.isEmpty()){
//                        doWait(1000);
//                    }
//
//                    AsignacionEstrategias asignacionRival = new AsignacionEstrategias(partida.getIdPartida(), seguridadEstrategias);
//
//                    manager.fillContent(accept, asignacionRival);
//
//                    String mensaje = "";
//                    for (int i = 0; i < espiaEstrategias.size(); i++) {
//                        mensaje = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "EST;ESPIA;" + espiaEstrategias.get(i).toString() + "\n";
//                    }
//
//                    mandarMensajeOrganizador(mensaje, "ESTRATEGIA");
//
//                } else if (propose.getSender().getLocalName().contains("Seguridad")) {
//                    seguridadEstrategias = asignacionEstrategias.getListaAsignacion();
//
//                    if(espiaEstrategias.isEmpty()){
//                        doWait(1000);
//                    }
//
//                    AsignacionEstrategias asignacionRival = new AsignacionEstrategias(partida.getIdPartida(), espiaEstrategias);
//
//                    manager.fillContent(accept, asignacionRival);
//
//                    String mensaje = "";
//                    for (int i = 0; i < seguridadEstrategias.size(); i++) {
//                        mensaje = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "EST;SEGURIDAD;" + seguridadEstrategias.get(i).toString() + "\n";
//                    }
//
//                    mandarMensajeOrganizador(mensaje, "ESTRATEGIA");
//
//                }
//                acceptances.add(accept);
//            } catch (Codec.CodecException | OntologyException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        @Override
//        protected void handleRefuse(ACLMessage refuse) {
//            incidenciaJuego =  new IncidenciaJuego(partida.getIdJuego(), Vocabulario.Incidencia.CANCELADO);
//        }

        @Override
        protected void handleInform(ACLMessage inform) {
            if(!calculandoPuntuacion){
                addBehaviour(new TareaCalcularPuntuacion());
                calculandoPuntuacion = true;
            }
        }
    }

    public class TareaCalcularPuntuacion extends OneShotBehaviour{

        @Override
        public void action() {
//            for (int i = 0; i < espiaEstrategias.size(); i++) {
//                Pago pago = obtenerPago.apply((EstrategiaEspia) espiaEstrategias.get(i), (EstrategiaSeguridad) seguridadEstrategias.get(i));
//                puntuacionEspia += pago.getPago(TipoAgenteJugador.ESPIA);
//                puntuacionSeguridad += pago.getPago(TipoAgenteJugador.SEGURIDAD);
//
//                //manda la puntuacion de los agentes
//                String mensajePuntuacionEspia = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "ESPIA" + ";" + puntuacionEspia;
//                mandarMensajeOrganizador(mensajePuntuacionEspia, "PUNTUACION");
//                String mensajePuntuacionSeguridad = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "SEGURIDAD" + ";" + puntuacionSeguridad;
//                mandarMensajeOrganizador(mensajePuntuacionSeguridad, "PUNTUACION");
//            }

//            System.out.println("Estrategias de la Seguridad");
//            for (int i = 0; i < seguridadEstrategias.size(); i++) {
//                System.out.println(seguridadEstrategias.get(i));
//            }
//            System.out.println("Estrategias de la Espia");
//            for (int i = 0; i < espiaEstrategias.size(); i++) {
//                System.out.println(espiaEstrategias.get(i));
//            }
//                Asignacion sokorro = (Asignacion) seguridadEstrategias.get(i);
//                System.out.println();
//                EstrategiaSeguridad nueva = (EstrategiaSeguridad)sokorro.getEstrategia().getEstrategia();
//                if(sokorro.getEstrategia().getEstrategia().equals(EstrategiaSeguridad.DI.toString())){
//                    System.out.println(EstrategiaSeguridad.DI);
//                }
            //System.out.println(obtenerPago.apply(EstrategiaEspia.EO.getNombre(), EstrategiaSeguridad.DI));
            
            //System.out.println(EstrategiaSeguridad.valueOf(EstrategiaSeguridad.DI.toString()).getClass());
            for (int i = 0; i < NUM_ZONAS; i++) {
                int posSeguridad = posEstrategiaZonaSeguridad(i);
                int posEspia = posEstrategiaZonaEspia(i);
                if(posSeguridad != -1 && posEspia != -1){
                    Asignacion seguridad = (Asignacion) seguridadEstrategias.get(posSeguridad);
                    EstrategiaSeguridad seguridad_strat= EstrategiaSeguridad.valueOf(seguridad.getEstrategia().getEstrategia());
                    Asignacion espia = (Asignacion) espiaEstrategias.get(posEspia);
                    EstrategiaEspia espia_strat = EstrategiaEspia.valueOf(espia.getEstrategia().getEstrategia());

                    Pago pago = obtenerPago.apply(espia_strat, seguridad_strat);
//                    System.out.println(pago.getPago(TipoAgenteJugador.ESPIA));
//                    System.out.println(pago.getPago(TipoAgenteJugador.SEGURIDAD));
                    puntuacionEspia += pago.getPago(TipoAgenteJugador.ESPIA);
                    puntuacionSeguridad += pago.getPago(TipoAgenteJugador.SEGURIDAD);
                } else if(posSeguridad != -1){
                    //System.out.println("El espia se lleva la zona: "+i);
                    puntuacionEspia += distribucionObjetivos.get(i).getValor()+distribucionObjetivos.get(i).getValor()*BONUS*0.01;
                } else if(posEspia != -1){
                    //System.out.println("La seguridad se lleva la zona "+i);
                    puntuacionSeguridad += distribucionObjetivos.get(i).getValor() + distribucionObjetivos.get(i).getValor() * BONUS * 0.01;
                }
            }
//            System.out.println("PUNTUACION ESPIA: "+puntuacionEspia);
//            System.out.println("PUNTUACION SEGURIDAD: "+puntuacionSeguridad);
            String mensajePuntuacionEspia = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "PUNT;ESP;" + puntuacionEspia;
            String mensajePuntuacionSeguridad = partida.getIdJuego() + ";" + partida.getIdPartida() + ";" + "PUNT;SEG;" + puntuacionSeguridad;

            mandarMensajeOrganizador(mensajePuntuacionEspia, "PUNTUACION");
            mandarMensajeOrganizador(mensajePuntuacionSeguridad, "PUNTUACION");
        }
    }
    
    private int posEstrategiaZonaSeguridad(int numZona){
        int pos = -1;
        for (int i = 0; i < seguridadEstrategias.size(); i++) {
            Asignacion sokorro = (Asignacion) seguridadEstrategias.get(i);
            if(sokorro.getZona()==numZona){
                pos = i;
                return pos;
            }
        }
        return pos;
    }
    
    private int posEstrategiaZonaEspia(int numZona){
        int pos = -1;
        for (int i = 0; i < espiaEstrategias.size(); i++) {
            Asignacion sokorro = (Asignacion) espiaEstrategias.get(i);
            if(sokorro.getZona()==numZona){
                pos = i;
                return pos;
            }
        }
        return pos;
    }

    public class TareaReproduccion extends TickerBehaviour{
        final String partida;
        ArrayList<ACLMessage> movimientos;
        int i = 0;
        int sizePartida = -1;

        public TareaReproduccion(Agent a, String partida) {
            super(a, COOLDOWN_REPETICION);
            this.partida = partida;
        }


        @Override
        protected void onTick() {

            if (sizePartida == -1){
                movimientos = registroMovimientos.get(partida);
                sizePartida = movimientos.size();
            }

            if (i == sizePartida) { stop(); }
            else{
                send(movimientos.get(i));
                i++;
            }
        }

    }

    void agregarMovimiento(String partida, ACLMessage movimiento){
        if (!listaPartidas.contains(partida)){
            listaPartidas.add(partida);
            registroMovimientos.put(partida, new ArrayList<>());
        }
        registroMovimientos.get(partida).add(movimiento);
    }

    public class TareaSolicitudFinalizacion extends CyclicBehaviour {
        
        public TareaSolicitudFinalizacion(Agent a) {
            super(a);
        }

        @Override
        public void action() {

            MessageTemplate template = MessageTemplate.MatchConversationId("TERMINAR EJECUCION");
            ACLMessage respuesta = receive(template);
            
            //System.out.println(this.myAgent.getLocalName()+" Tareas en el pool -->"+this.myAgent.getBehavioursCnt());

            if (respuesta != null) {
                myAgent.doDelete();
            } else {
                block();
            }
        }
    }

}
